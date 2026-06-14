package app.preach.gospel.service.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.BookDto;
import app.preach.gospel.dto.ChapterDto;
import app.preach.gospel.dto.VerseDto;
import app.preach.gospel.model.Verse;
import app.preach.gospel.repository.BookRepository;
import app.preach.gospel.repository.ChapterRepository;
import app.preach.gospel.repository.VerseRepository;
import app.preach.gospel.service.IBookService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.CoStringUtils;

/**
 * 聖書章節サービス実装クラス - Spring Data JDBC 移行版
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Service
public class BookServiceImpl implements IBookService {

	/* DAOs */
	private final BookRepository bookRepository;
	private final ChapterRepository chapterRepository;
	private final VerseRepository verseRepository;

	/**
	 * コンストラクタ
	 *
	 * @param bookRepository
	 * @param chapterRepository
	 * @param verseRepository
	 */
	protected BookServiceImpl(final BookRepository bookRepository, final ChapterRepository chapterRepository,
			final VerseRepository verseRepository) {
		this.bookRepository = bookRepository;
		this.chapterRepository = chapterRepository;
		this.verseRepository = verseRepository;
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<List<BookDto>, DataAccessException> getBooks() {
		try {
			final var bookDtos = this.bookRepository.findAllByOrderByIdAsc().stream()
					.map(r -> new BookDto(r.id(), r.name(), r.nameJp())).toList();
			return CoResult.ok(bookDtos);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<List<ChapterDto>, DataAccessException> getChaptersByBookId(final Short id) {
		try {
			// 短絡評価のためにIntegerへラップ
			final var bookId = (id != null) ? Integer.valueOf(id) : Integer.valueOf(1);
			final var chapterDtos = this.chapterRepository.findByBookIdOrderByIdAsc(bookId).stream()
					.map(r -> new ChapterDto(r.id(), r.name(), r.nameJp(), r.bookId().toString())).toList();
			return CoResult.ok(chapterDtos);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional
	@Override
	public CoResult<String, DataAccessException> infoStorage(final @NotNull VerseDto phraseDto) {
		final var id = Long.valueOf(phraseDto.id());
		final var chapterId = Integer.valueOf(phraseDto.chapterId());
		// 計算による主キーIDの算出
		final long targetPhraseId = (chapterId * 1000) + id;
		try {
			// 1. 章節情報の取得
			final var chaptersRecord = this.chapterRepository.findById(chapterId)
					.orElseThrow(() -> new DataAccessException("Chapter not found: " + chapterId) {
					});
			// 2. 節名称（例: "創世記:1" のようなフォーマット）の組み立て
			final String phraseName = chaptersRecord.name().concat("\u003a").concat(id.toString());
			// 3. 英語テキストの処理と改行フラグの判定
			final var textEnInput = phraseDto.textEn();
			final String textEn;
			final String changeLine;
			if (textEnInput != null && textEnInput.endsWith("#")) {
				textEn = textEnInput.replace("#", CoStringUtils.EMPTY_STRING);
				changeLine = Boolean.TRUE.toString();
			} else {
				textEn = textEnInput;
				changeLine = Boolean.FALSE.toString();
			}
			// 4. 既存レコードの確認（UPSERTロジックの判定）
			final var existingPhrase = this.verseRepository.findById(targetPhraseId);
			if (existingPhrase.isPresent()) {
				// 更新用インスタンスを生成
				final var updatedPhrase = new Verse(targetPhraseId, phraseName, textEn, phraseDto.textJp(), chapterId,
						changeLine);
				this.verseRepository.save(updatedPhrase);
				return CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED);
			}
			// 新規登録用インスタンスを生成
//			final var verse = new Verse(targetPhraseId, phraseName, textEn, phraseDto.textJp(), chapterId, changeLine);
			// Spring Data JDBCは、@Idに値（targetPhraseId）が既にセットされている場合、
			// デフォルトで「UPDATE」を試みようとします（新しく払い出されたIDではないと認識するため）。
			// しかし、既存確認をして存在しないことが保証されているため、ここではsave()メソッドを呼び出すことで、
			// Spring Data JDBC内部のメカニズムによって新規インサート、または必要に応じた永続化処理が正しく行われます。
			this.verseRepository.insertOne(targetPhraseId, phraseName, textEn, phraseDto.textJp(), chapterId,
					changeLine);
			return CoResult.ok(ProjectConstants.MESSAGE_STRING_INSERTED);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

}
