package app.preach.gospel.controller;

import java.io.IOException;
import java.io.Serial;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

/**
 * 賛美歌楽譜管理ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RequestMapping("/hymns")
@Controller
@Tag(name = "賛美歌楽譜管理ハンドラ", description = "賛美歌楽譜に関わる操作を扱うエンドポイント")
public final class HymnsScoreController {

	@Serial
	private static final long serialVersionUID = 4949258675703419344L;

	/**
	 * 賛美歌サービスインターフェス
	 */
	@Resource
	private IHymnService iHymnService;

	/**
	 * 賛美歌楽譜をダウンロードする
	 *
	 * @param id 賛美歌ID
	 * @return ResponseEntity<byte[]>
	 */
	@GetMapping("/score-download")
	@ResponseBody
	@Operation(summary = "ダウンロード", description = "IDを指定した賛美歌の楽譜をダウンロードする")
	public @NotNull ResponseEntity<byte[]> scoreDownload(@RequestParam final Long id) {
		final CoResult<HymnDto, DataAccessException> hymnInfoById = this.iHymnService.getHymnInfoById(id);
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final var hymnDto = hymnInfoById.getData();
		if (hymnDto.score() == null || hymnDto.score().length == 0) {
			throw new DataRetrievalFailureException(ProjectConstants.MESSAGE_HYMNSWORK_NOT_FOUND);
		}
		final var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", hymnDto.id() + ".pdf");
		return ResponseEntity.ok().headers(headers).body(hymnDto.score());
	}

	/**
	 * 楽譜の情報を保存する
	 *
	 * @param hymnDto 情報転送クラス
	 * @return ResponseEntity<String>
	 */
	@Operation(summary = "情報保存", description = "IDを指定した賛美歌の楽譜の情報を保存する")
	@PostMapping(value = "/score-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<String> scoreUpload(@RequestParam final Long id,
			@RequestPart("score") final MultipartFile score) {
		try {
			final CoResult<String, DataAccessException> scoreStorage = this.iHymnService.scoreStorage(score.getBytes(),
					id);
			if (!scoreStorage.isOk()) {
				throw scoreStorage.getErr();
			}
			return ResponseEntity.ok(scoreStorage.getData());
		} catch (final IOException e) {
			return ResponseEntity.badRequest().body("ファイル読み込みに失敗しました。");
		}
	}

	/**
	 * 楽譜アプロード画面へ移動する
	 *
	 * @param pageNum ページナンバー
	 * @return ModelAndView
	 */
	@GetMapping("/to-score-upload")
	@Operation(summary = "画面遷移", description = "楽譜アプロード画面へ移動する")
	public @NotNull ModelAndView toScoreUpload(@RequestParam final Long scoreId, @RequestParam final Integer pageNum) {
		final ModelAndView modelAndView = new ModelAndView("hymns-score-upload");
		final CoResult<HymnDto, DataAccessException> hymnInfoById = this.iHymnService.getHymnInfoById(scoreId);
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final var hymnDto = hymnInfoById.getData();
		modelAndView.addObject(ProjectConstants.ATTRNAME_EDITED_INFO, hymnDto);
		modelAndView.addObject(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
		return modelAndView;
	}

}
