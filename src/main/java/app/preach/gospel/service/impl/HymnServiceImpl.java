package app.preach.gospel.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.tika.Tika;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.benmanes.caffeine.cache.Cache;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.dto.dtokey.DocKey;
import app.preach.gospel.dto.dtokey.IdfKey;
import app.preach.gospel.dto.dtokey.TokKey;
import app.preach.gospel.dto.dtokey.VecKey;
import app.preach.gospel.mapper.HymnMapper;
import app.preach.gospel.model.Hymn;
import app.preach.gospel.model.HymnWork;
import app.preach.gospel.model.Student;
import app.preach.gospel.repository.HymnRepository;
import app.preach.gospel.repository.HymnWorkRepository;
import app.preach.gospel.repository.StudentRepository;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.CoSortsUtils;
import app.preach.gospel.utils.CoStringUtils;
import app.preach.gospel.utils.LineNumber;
import app.preach.gospel.utils.Pagination;
import app.preach.gospel.utils.SnowflakeUtils;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 賛美歌サービス実装クラス - Spring Data JDBC 移行版(部分1)
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Log4j2
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HymnServiceImpl implements IHymnService {

	/**
	 * 日時フォマーター
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * KOMORAN-API
	 */
	private static final Komoran KOMORAN = new Komoran(DEFAULT_MODEL.FULL);

	/**
	 * Korean Language
	 */
	private static final String KR = "Korean";

	/**
	 * ランドム選択
	 */
	private static final Random RANDOM = new Random();

	/**
	 * ダイジェストメソッド
	 */
	private static final ThreadLocal<MessageDigest> SHA256 = ThreadLocal.withInitial(() -> {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	});

	/**
	 * 怪しいキーワードリスト
	 */
	private static final String[] STRANGE_ARRAY = { "insert", "delete", "update", "create", "drop", "#", "$", "%", "(",
			")", "\"", "\'", "@", ":", "select" };

	/**
	 * コサイン類似度を計算する
	 *
	 * @param vectorA ベクターA
	 * @param vectorB ベクターB
	 * @return コサイン類似度
	 */
	private static double cosineSimilarity(final double @NotNull [] vectorA, final double[] vectorB) {
		double dotProduct = 0.00;
		double normA = 0.00;
		double normB = 0.00;
		for (var i = 0; i < Math.min(vectorA.length, vectorB.length); i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}
		if ((normA == 0) || (normB == 0)) {
			return 0;
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	/**
	 * 通常検索条件を取得する
	 *
	 * @param keyword キーワード
	 * @return Specification<Hymn>
	 */
	private static @NotNull String getHymnSpecification(final String keyword) {
		return CoStringUtils.isEmpty(keyword) ? CoStringUtils.HANKAKU_PERCENTSIGN
				: CoStringUtils.HANKAKU_PERCENTSIGN.concat(keyword).concat(CoStringUtils.HANKAKU_PERCENTSIGN);
	}

	/**
	 * セリフの全角スペースを削除する
	 *
	 * @param serif セリフ
	 * @return トリムドのセリフ
	 */
	private static @NotNull String trimSerif(final @NotNull String serif) {
		final var zenkakuSpace = "\u3000";
		return serif.replace(zenkakuSpace, CoStringUtils.EMPTY_STRING).trim();
	}

	// Entity2DTO Mapper
	private final HymnMapper hymnMapper;

	// jOOQの依存関係を排除し、Spring Data JDBCリポとMapStructマッパーを注入
	private final HymnRepository hymnRepository;
	private final HymnWorkRepository hymnWorkRepository;
	@Qualifier("nlpCache")
	private final Cache<Object, Object> nlpCache;

	private final StudentRepository studentRepository;

	@Transactional(readOnly = true)
	@Override
	public CoResult<Integer, DataAccessException> checkDuplicated(final String id, final String nameJp) {
		try {
			int count;
			if (CoStringUtils.isDigital(id)) {
				count = this.hymnRepository.countByVisibleFlgTrueAndNameJpAndIdNot(nameJp, Long.parseLong(id));
			} else {
				count = this.hymnRepository.countByVisibleFlgTrueAndNameJp(nameJp);
			}
			return CoResult.ok(count);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<Integer, DataAccessException> checkDuplicated2(final String id, final String nameKr) {
		try {
			int count;
			if (CoStringUtils.isDigital(id)) {
				count = this.hymnRepository.countByVisibleFlgTrueAndNameKrAndIdNot(nameKr, Long.parseLong(id));
			} else {
				count = this.hymnRepository.countByVisibleFlgTrueAndNameKr(nameKr);
			}
			return CoResult.ok(count);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		}
	}

	private double[] computeTfIdfVector(final String lang, final String hymnVersion, final String text,
			final Object2DoubleOpenHashMap<String> idf) {
		final var key = new VecKey(lang, hymnVersion, this.hash(text));
		final var cached = (double[]) this.nlpCache.getIfPresent(key);
		if (cached != null) {
			return cached;
		}
		final var tokens = this.tokenize(lang, "KOMORAN", text);
		final var tf = new Object2IntOpenHashMap<String>();
		tokens.forEach(t -> tf.merge(t, 1, Integer::sum));
		final var vec = new double[idf.size()];
		final Object2IntOpenHashMap<String> termIndex = this.indexOf(idf.keySet());
		tf.object2IntEntrySet().fastForEach(en -> {
			final var term = en.getKey();
			final int cnt = en.getIntValue();
			if (termIndex.containsKey(term)) {
				vec[termIndex.getInt(term)] = cnt * idf.getOrDefault(term, 0.0);
			}
		});
		this.nlpCache.put(key, vec);
		return vec;
	}

	/**
	 * イメージからPDFへ変換する
	 *
	 * @param img            イメージ
	 * @param pdfDiscernment タイプ
	 * @return byte[]
	 */
	private byte[] convertCenteredImage(final byte[] img, final String pdfDiscernment) {
		if (CoStringUtils.isEqual(MediaType.APPLICATION_PDF_VALUE, pdfDiscernment)) {
			return img;
		}
		try (final var doc = new PDDocument()) {
			final var page = new PDPage(PDRectangle.A4);
			doc.addPage(page);
			final BufferedImage image = (CoStringUtils.isEqual(MediaType.IMAGE_JPEG_VALUE, pdfDiscernment))
					? CoSortsUtils.readAndNormalizeOrientation(img)
					: ImageIO.read(new ByteArrayInputStream(img));

			final float pageWidth = page.getMediaBox().getWidth();
			final float pageHeight = page.getMediaBox().getHeight();
			final float imgWidth = image.getWidth();
			final float imgHeight = image.getHeight();

			final var scale = Math.min(pageWidth / imgWidth, pageHeight / imgHeight);
			final var drawWidth = imgWidth * scale;
			final var drawHeight = imgHeight * scale;

			final PDImageXObject pdfImage = LosslessFactory.createFromImage(doc, image);
			final var x = (pageWidth - drawWidth) / 2f;
			final var y = (pageHeight - drawHeight) / 2f;
			try (var contentStream = new PDPageContentStream(doc, page)) {
				contentStream.drawImage(pdfImage, x, y, drawWidth, drawHeight);
			}
			final var out = new ByteArrayOutputStream();
			doc.save(out);
			return out.toByteArray();
		} catch (final IOException e) {
			return ProjectConstants.EMPTY_ARR;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 最も似てる賛美歌を取得する
	 *
	 * @param target   目標テキスト
	 * @param elements 賛美歌リスト
	 * @return List<HymnsRecord>
	 */
	private List<HymnDto> findTopMatches(final String[] target, final List<HymnDto> elements) {
		final String corpusVersion = this.getCorpusVersion();
		final var hymnsStream = elements.stream().map(e -> this.tokenize(KR, "KOMORAN", e.lyric()));
		final Object2DoubleOpenHashMap<String> idf = this.getIdf(target, hymnsStream);
		var targetVector = new double[0];
		if (target.length == 1) {
			targetVector = this.computeTfIdfVector(KR, corpusVersion, target[0], idf);
		} else {
			for (final var vn : target) {
				final var tmp = targetVector;
				final double[] computeTfIdfVector = this.computeTfIdfVector(KR, corpusVersion, vn, idf);
				targetVector = new double[tmp.length + computeTfIdfVector.length];
				System.arraycopy(tmp, 0, targetVector, 0, tmp.length);
				System.arraycopy(computeTfIdfVector, 0, targetVector, tmp.length, computeTfIdfVector.length);
			}
		}
		final var elementVectors = elements.stream()
				.map(item -> this.computeTfIdfVector(KR, corpusVersion, item.lyric(), idf)).toList();
		final var maxHeap = new ArrayList<Object2DoubleOpenHashMap.Entry<HymnDto>>();
		for (var i = 0; i < elements.size(); i++) {
			final double similarity = cosineSimilarity(targetVector, elementVectors.get(i));
			maxHeap.add(new Object2DoubleOpenHashMap.BasicEntry<>(elements.get(i), similarity));
		}
		return maxHeap.stream().sorted(Comparator.comparing(Entry<HymnDto>::getDoubleValue).reversed()).map(item -> {
			final var similarity = item.getDoubleValue();
			log.warn("類似程度：" + similarity);
			final var hymnDto = item.getKey();
			if (similarity >= 0.33 && hymnDto.lineNumber() == LineNumber.SNOWY) {
				return new HymnDto(hymnDto.id(), hymnDto.nameJp(), hymnDto.nameKr(), hymnDto.lyric(), hymnDto.link(),
						hymnDto.score(), hymnDto.updatedUser(), hymnDto.updatedTime(), LineNumber.CADMIUM);
			}
			if (similarity >= 0.21 && hymnDto.lineNumber() == LineNumber.SNOWY) {
				return new HymnDto(hymnDto.id(), hymnDto.nameJp(), hymnDto.nameKr(), hymnDto.lyric(), hymnDto.link(),
						hymnDto.score(), hymnDto.updatedUser(), hymnDto.updatedTime(), LineNumber.BURGUNDY);
			}
			if (similarity >= 0.07 && hymnDto.lineNumber() == LineNumber.SNOWY) {
				return new HymnDto(hymnDto.id(), hymnDto.nameJp(), hymnDto.nameKr(), hymnDto.lyric(), hymnDto.link(),
						hymnDto.score(), hymnDto.updatedUser(), hymnDto.updatedTime(), LineNumber.NAPLES);
			}
			return hymnDto;
		}).toList();
	}

	@Transactional(readOnly = true)
	protected String getCorpusVersion() {
		// 1. MAX(updated_time) をリポジトリ経由で取得
		final LocalDateTime maxUpdatedAt = this.hymnRepository.findMaxUpdatedTime();
		// 2. null の場合は現在時刻を代替
		if (maxUpdatedAt == null) {
			return LocalDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		// 3. ISO 文字列に変換
		return maxUpdatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<HymnDto, DataAccessException> getHymnInfoById(final Long id) {
		try {
			final Hymn hymn = this.hymnRepository.findByIdAndVisibleFlgTrue(id)
					.orElseThrow(() -> new DataAccessException("Hymn not found") {
					});
			final HymnWork work = this.hymnWorkRepository.findByWorkId(id)
					.orElseThrow(() -> new DataAccessException("HymnWork not found") {
					});
			final Student student = this.studentRepository.findByIdAndVisibleFlgTrue(hymn.updatedUser())
					.orElseThrow(() -> new DataAccessException("Student not found") {
					});
			// 時差計算 (+9時間)
			final LocalDateTime targetLocalTime = hymn.updatedTime();
			final String formattedTime = FORMATTER.format(targetLocalTime);
			// MapStruct マッパーによる高度な複数集約オブジェクト変換
			final HymnDto hymnDto = this.hymnMapper.toDto(hymn, work, student, formattedTime);
			return CoResult.ok(hymnDto);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<Pagination<HymnDto>, DataAccessException> getHymnsInfoByPagination(final Integer pageNum,
			final String keyword) {
		try {
			// 総件数の取得
			final long totalRecords = this.hymnRepository.countByVisibleFlgTrue();
			final int offset = (pageNum - 1) * ProjectConstants.DEFAULT_PAGE_SIZE;
			final int margrave = (int) ((offset + ProjectConstants.DEFAULT_PAGE_SIZE) > totalRecords ? totalRecords
					: offset + ProjectConstants.DEFAULT_PAGE_SIZE);
			final var docKey = new DocKey(keyword, this.getCorpusVersion(), totalRecords);
			@SuppressWarnings("unchecked")
			final var nlpedHymnDtos = (List<HymnDto>) this.nlpCache.getIfPresent(docKey);
			if (nlpedHymnDtos != null) {
				final var subList = nlpedHymnDtos.subList(offset, margrave);
				final var pagination = Pagination.of(subList, totalRecords, pageNum,
						ProjectConstants.DEFAULT_PAGE_SIZE);
				return CoResult.ok(pagination);
			}
			// 有効な讃美歌の一覧を取得し、MapStruct で DTO へ一括変換
			final List<HymnDto> hymnDtos = this.hymnRepository.findByVisibleFlgTrueOrderByIdAsc().stream()
					.map(h -> this.hymnMapper.toDto2(h, LineNumber.SNOWY))
					.collect(Collectors.toCollection(ArrayList::new)); // 後のremoveIf等の破壊的操作に備えて可変リストにする
			if (CoStringUtils.isEmpty(keyword)) {
				final var pagination = Pagination.of(hymnDtos.subList(offset, margrave), totalRecords, pageNum,
						ProjectConstants.DEFAULT_PAGE_SIZE);
				this.nlpCache.put(docKey, hymnDtos);
				return CoResult.ok(pagination);
			}

			for (final String starngement : STRANGE_ARRAY) {
				if (keyword.toLowerCase().contains(starngement) || keyword.length() >= 100) {
					log.warn("怪しいキーワード： " + keyword);
					final var pagination = Pagination.of(
							hymnDtos.subList(offset, offset + ProjectConstants.DEFAULT_PAGE_SIZE), totalRecords,
							pageNum, ProjectConstants.DEFAULT_PAGE_SIZE);
					this.nlpCache.put(docKey, hymnDtos);
					return CoResult.ok(pagination);
				}
			}

			// NAME_KRのライク検索に該当する一覧を取得し、MapStructでDTO化
			final List<HymnDto> hymnDtos2 = this.hymnRepository
					.findActiveHymnsByNameKrLike(getHymnSpecification(keyword)).stream()
					.map(h -> this.hymnMapper.toDto2(h, LineNumber.CADMIUM)).toList();

			if (CollectionUtils.isEmpty(hymnDtos2)) {
				final String[] splits = keyword.split("&");
				final List<HymnDto> topMatches = this.findTopMatches(splits, hymnDtos);
				final var sortedHymnDtos = topMatches.stream()
						.sorted(Comparator.comparingInt(item -> item.lineNumber().getLineNo())).toList();
				final var pagination = Pagination.of(sortedHymnDtos.subList(offset, margrave), totalRecords, pageNum,
						ProjectConstants.DEFAULT_PAGE_SIZE);
				this.nlpCache.put(docKey, sortedHymnDtos);
				return CoResult.ok(pagination);
			}

			final var list = hymnDtos2.stream().map(HymnDto::lyric).toList();
			final var targets = new String[list.size()];
			for (var i = 0; i < targets.length; i++) {
				targets[i] = list.get(i);
			}
			final var ids = hymnDtos2.stream().map(HymnDto::id).toList();
			hymnDtos.removeIf(a -> ids.contains(a.id()));
			hymnDtos.addAll(hymnDtos2);

			final List<HymnDto> topMatches = this.findTopMatches(targets, hymnDtos);
			final var sortedHymnDtos = topMatches.stream()
					.sorted(Comparator.comparingInt(item -> item.lineNumber().getLineNo())).toList();
			final var pagination = Pagination.of(sortedHymnDtos.subList(offset, margrave), totalRecords, pageNum,
					ProjectConstants.DEFAULT_PAGE_SIZE);
			this.nlpCache.put(docKey, sortedHymnDtos);
			return CoResult.ok(pagination);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<List<HymnDto>, DataAccessException> getHymnsInfoByRandom(final String keyword) {
		try {
			for (final String starngement : STRANGE_ARRAY) {
				if (keyword.toLowerCase().contains(starngement) || keyword.length() >= 100) {
					// 件数制限付き取得の代わりに、リポジトリから条件2（classical=false）のリストを取得して上限処理
					final List<HymnDto> hymnDtos = this.hymnRepository
							.findByVisibleFlgTrueAndClassicalFalseOrderByIdAsc().stream()
							.limit(ProjectConstants.DEFAULT_PAGE_SIZE)
							.map(h -> this.hymnMapper.toDto2(h, LineNumber.SNOWY)).toList();
					log.warn("怪しいキーワード： " + keyword);
					return CoResult.ok(hymnDtos);
				}
			}
			// 全レコードのDTO変換リスト（可変）
			final List<HymnDto> totalRecords = this.hymnRepository.findByVisibleFlgTrueAndClassicalFalseOrderByIdAsc()
					.stream().map(h -> this.hymnMapper.toDto2(h, LineNumber.SNOWY))
					.collect(Collectors.toCollection(ArrayList::new));
			if (CoStringUtils.isEmpty(keyword)) {
				final List<HymnDto> hymnDtos = this.randomFiveLoop2(totalRecords);
				return CoResult.ok(hymnDtos);
			}
			final List<HymnDto> hymnDtos2 = this.hymnRepository
					.findActiveHymnsByNameKrLike(getHymnSpecification(keyword)).stream()
					.map(h -> this.hymnMapper.toDto2(h, LineNumber.CADMIUM)).toList();
			if (CollectionUtils.isEmpty(hymnDtos2)) {
				final String[] splits = keyword.split("&");
				final List<HymnDto> topMatches = this.findTopMatches(splits, totalRecords);
				final List<HymnDto> randomFiveLoop = this.randomFiveLoop(topMatches.subList(0, 10), totalRecords);
				return CoResult.ok(randomFiveLoop.stream()
						.sorted(Comparator.comparingInt(item -> item.lineNumber().getLineNo())).toList());
			}
			final var list = hymnDtos2.stream().map(HymnDto::lyric).toList();
			final var targets = new String[list.size()];
			for (var i = 0; i < targets.length; i++) {
				targets[i] = list.get(i);
			}
			final var ids = hymnDtos2.stream().map(HymnDto::id).toList();
			totalRecords.removeIf(a -> ids.contains(a.id()));
			totalRecords.addAll(hymnDtos2);
			final List<HymnDto> topMatches = this.findTopMatches(targets, totalRecords);
			final List<HymnDto> randomFiveLoop = this.randomFiveLoop(topMatches.subList(0, 10), totalRecords);
			return CoResult.ok(randomFiveLoop.stream()
					.sorted(Comparator.comparingInt(item -> item.lineNumber().getLineNo())).toList());
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object2DoubleOpenHashMap<String> getIdf(final String[] corpusVersion, final Stream<List<String>> allDocs) {
		final var key = new IdfKey(corpusVersion);
		@SuppressWarnings("unchecked")
		final var cached = (Object2DoubleOpenHashMap<String>) this.nlpCache.getIfPresent(key);
		if (cached != null) {
			return cached;
		}
		final var df = new Object2IntOpenHashMap<String>();
		final var list = allDocs.toList();
		for (final var docs : list) {
			docs.stream().distinct().forEach(term -> df.merge(term, 1, Integer::sum));
		}
		final long totalDocs = list.size();
		final var res = new Object2DoubleOpenHashMap<String>();
		df.object2IntEntrySet().fastForEach(en -> {
			final double an = Math.log((totalDocs + 1.0) / (en.getIntValue() + 1.0)) + 1.0;
			res.put(en.getKey(), an);
		});
		this.nlpCache.put(key, res);
		return res;
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<Long, DataAccessException> getTotalCounts() {
		try {
			final long totalRecords = this.hymnRepository.countByVisibleFlgTrue();
			return CoResult.ok(totalRecords);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		}
	}

	/**
	 * ハッシュ化する
	 *
	 * @param text テキスト
	 * @return String
	 */
	private String hash(final String text) {
		final var b = SHA256.get().digest(text.getBytes(CoStringUtils.CHARSET_UTF8));
		final var sb = new StringBuilder();
		for (final byte x : b) {
			sb.append(String.format("%02x", x));
		}
		return sb.toString();
	}

	/**
	 * インデクスを取得する
	 *
	 * @param terms リスト
	 * @return Object2IntOpenHashMap<String>
	 */
	private Object2IntOpenHashMap<String> indexOf(final Collection<String> terms) {
		final var map = new Object2IntOpenHashMap<String>(terms.size() * 2);
		int i = 0;
		for (final var t : terms) {
			map.put(t, i++);
		}
		return map;
	}

	@Transactional
	@Override
	public CoResult<String, DataAccessException> infoDeletion(final Long id) {
		try {
			final Hymn hymn = this.hymnRepository.findByIdAndVisibleFlgTrue(id)
					.orElseThrow(() -> new DataAccessException("Hymn not found") {
					});

			// 論理削除フラグを偽に変更した新しいレコードインスタンスを保存
			final Hymn deletedHymn = new Hymn(hymn.id(), hymn.nameJp(), hymn.nameKr(), hymn.link(), hymn.updatedTime(),
					hymn.updatedUser(), hymn.lyric(), Boolean.FALSE, hymn.classical());
			this.hymnRepository.save(deletedHymn);
			return CoResult.ok(ProjectConstants.MESSAGE_STRING_DELETED);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		}
	}

	@Transactional
	@Override
	public CoResult<Integer, DataAccessException> infoStorage(final @NotNull HymnDto hymnDto) {
		final var updateTime = LocalDateTime.now();
		try {
			final long newHymnId = SnowflakeUtils.snowflakeId();
			final String trimmedSerif = trimSerif(hymnDto.lyric());
			// 1. HYMNSテーブルへインサート
			final var newHymn = new Hymn(newHymnId, hymnDto.nameJp(), hymnDto.nameKr(), hymnDto.link(), updateTime,
					Long.parseLong(hymnDto.updatedUser()), trimmedSerif, Boolean.TRUE, Boolean.FALSE);
			this.hymnRepository.save(newHymn);
			// 2. HYMNS_WORKテーブルへインサート
			final int nextWorkSequenceId = this.hymnWorkRepository.countAllRecords() + 1;
			final var newWork = new HymnWork(Long.valueOf(nextWorkSequenceId), newHymnId, null);
			this.hymnWorkRepository.save(newWork);
			// 3. 最大ページ数の算定
			final long totalRecords = this.hymnRepository.countByVisibleFlgTrue();
			final int discernLargestPage = CoStringUtils.discernLargestPage(totalRecords);
			return CoResult.ok(discernLargestPage);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional
	@Override
	public CoResult<String, DataAccessException> infoUpdate(final @NotNull HymnDto hymnDto) {
		final LocalDateTime updateTime = LocalDateTime.now();
		try {
			final var targetId = Long.valueOf(hymnDto.id());
			// 既存の最新データをDBから直接取得（jOOQのセーブポイント比較に準拠）
			final Hymn existingHymn = this.hymnRepository.findByIdAndVisibleFlgTrue(targetId)
					.orElseThrow(() -> new DataAccessException("Hymn not found") {
					});
			// 楽観的排他チェック
			if (existingHymn.updatedTime().isAfter(updateTime)) {
				return CoResult.err(new org.springframework.dao.OptimisticLockingFailureException(
						ProjectConstants.MESSAGE_OPTIMISTIC_ERROR) {
				});
			}
			// 入力値と既存レコードから仮想上書きオブジェクトを組み立てる（レコード比較のため）
			final String trimmedSerif = trimSerif(hymnDto.lyric());
			final var virtualUpdatedHymn = new Hymn(targetId, hymnDto.nameJp(), hymnDto.nameKr(), hymnDto.link(),
					existingHymn.updatedTime(), existingHymn.updatedUser(), trimmedSerif, Boolean.TRUE,
					existingHymn.classical());
			// イミュータブル特性を活かしたデータ無変更チェック（record型は全プロパティのequalsが備わっています）
			if (existingHymn.equals(virtualUpdatedHymn)) {
				return CoResult.ok(ProjectConstants.MESSAGE_STRING_NO_CHANGE);
			}
			// 変動があった場合のみ永続化
			final HymnWork existingWork = this.hymnWorkRepository.findByWorkId(targetId)
					.orElseThrow(() -> new DataAccessException("HymnWork not found") {
					});
			// 更新用インスタンスの作成（時間・ユーザーIDの上書き）
			final var finalUpdatedHymn = new Hymn(targetId, hymnDto.nameJp(), hymnDto.nameKr(), hymnDto.link(),
					updateTime, Long.valueOf(hymnDto.updatedUser()), trimmedSerif, Boolean.TRUE,
					existingHymn.classical());
			final var finalUpdatedWork = new HymnWork(existingWork.id(), existingWork.workId(), existingWork.score());
			this.hymnWorkRepository.save(finalUpdatedWork);
			this.hymnRepository.save(finalUpdatedHymn);
			return CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ランドム選択ループ1
	 *
	 * @param hymnsRecords 選択したレコード
	 * @param totalRecords 総合レコード
	 * @return List<HymnDto>
	 */
	private @NotNull List<HymnDto> randomFiveLoop(final @NotNull List<HymnDto> hymnsRecords,
			final @NotNull List<HymnDto> totalRecords) {
		final var ids = hymnsRecords.stream().map(HymnDto::id).distinct().toList();
		final var filteredRecords = totalRecords.stream().filter(item -> !ids.contains(item.id())).toList();
		final var result = new ArrayList<>(hymnsRecords);
		while (result.stream().distinct().count() < ProjectConstants.DEFAULT_PAGE_SIZE && !filteredRecords.isEmpty()) {
			final int indexOf = RANDOM.nextInt(filteredRecords.size());
			result.add(filteredRecords.get(indexOf));
		}
		return result.stream().distinct().toList();
	}

	/**
	 * ランドム選択ループ2
	 *
	 * @param hymnsRecords 選択したレコード
	 * @return List<HymnDto>
	 */
	private @NotNull List<HymnDto> randomFiveLoop2(final List<HymnDto> hymnsRecords) {
		final List<HymnDto> concernList1 = new ArrayList<>();
		for (int i = 1; i <= ProjectConstants.DEFAULT_PAGE_SIZE; i++) {
			final int indexOf = RANDOM.nextInt(hymnsRecords.size());
			final var hymnsRecord = hymnsRecords.get(indexOf);
			concernList1.add(hymnsRecord);
		}
		final var concernList2 = concernList1.stream().distinct().toList();
		if (concernList2.size() == ProjectConstants.DEFAULT_PAGE_SIZE) {
			return concernList2;
		}
		return this.randomFiveLoop(concernList2, hymnsRecords);
	}

	@Transactional
	@Override
	public CoResult<String, DataAccessException> scoreStorage(final @NotNull byte[] file, final Long id) {
		try {
			final HymnWork hymnsWorkRecord = this.hymnWorkRepository.findByWorkId(id)
					.orElseThrow(() -> new DataAccessException(ProjectConstants.MESSAGE_HYMNSWORK_NOT_FOUND) {
					});
			final var tika = new Tika();
			final String pdfDiscernment = tika.detect(file);
			final byte[] centeredImage = this.convertCenteredImage(file, pdfDiscernment);
			// コンテンツ変更チェック
			if ((CoStringUtils.isEqual(MediaType.APPLICATION_PDF_VALUE, pdfDiscernment)
					&& Arrays.equals(hymnsWorkRecord.score(), file))
					|| (CoStringUtils.isNotEqual(MediaType.APPLICATION_PDF_VALUE, pdfDiscernment)
							&& Arrays.equals(hymnsWorkRecord.score(), centeredImage))) {
				return CoResult.err(new DataRetrievalFailureException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)); // 適切な例外オブジェクトへフォールバック
			}
			final HymnWork updatedWork;
			if (Arrays.equals(ProjectConstants.EMPTY_ARR, centeredImage)) {
				updatedWork = new HymnWork(hymnsWorkRecord.id(), hymnsWorkRecord.workId(), file);
			} else {
				updatedWork = new HymnWork(hymnsWorkRecord.id(), hymnsWorkRecord.workId(), centeredImage);
			}
			this.hymnWorkRepository.save(updatedWork);
			return CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 1) 形態素解析キャッシュ
	private List<String> tokenize(final String lang, final String tokenizer, final String text) {
		final var regex = "\\p{IsHangul}";
		final var builder = new StringBuilder();
		for (final var ch : text.toCharArray()) {
			if (Pattern.matches(regex, String.valueOf(ch))) {
				builder.append(ch);
			}
		}
		final var koreanText = builder.toString();
		if (CoStringUtils.isEmpty(koreanText)) {
			return new ArrayList<>();
		}
		final var key = new TokKey(lang, tokenizer, this.hash(koreanText));
		@SuppressWarnings("unchecked")
		final var cached = (List<String>) this.nlpCache.getIfPresent(key);
		if (cached != null) {
			return cached;
		}
		final var tokens = KOMORAN.analyze(koreanText).getTokenList().stream().map(t -> t.getMorph()).toList();
		this.nlpCache.put(key, tokens);
		return tokens;
	}

}