package app.preach.gospel.handler;

import java.io.Serial;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.NoDataFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.CoStringUtils;
import jakarta.annotation.Resource;

/**
 * 賛美歌楽譜管理ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RequestMapping(ProjectURLConstants.URL_HYMNS_NAMESPACE)
@Controller
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
	@GetMapping(ProjectURLConstants.URL_SCORE_DOWNLOAD)
	@ResponseBody
	public @NotNull ResponseEntity<byte[]> scoreDownload(@RequestParam final Long id) {
		final CoResult<HymnDto, DataAccessException> hymnInfoById = this.iHymnService.getHymnInfoById(id);
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final HymnDto hymnDto = hymnInfoById.getData();
		final String biko = hymnDto.biko();
		if (CoStringUtils.isEmpty(biko)) {
			throw new NoDataFoundException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR);
		}
		final HttpHeaders headers = new HttpHeaders();
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
	@PostMapping(ProjectURLConstants.URL_SCORE_UPLOAD)
	@ResponseBody
	public @NotNull ResponseEntity<String> scoreUpload(@RequestBody final HymnDto hymnDto) {
		final CoResult<String, DataAccessException> scoreStorage = this.iHymnService.scoreStorage(hymnDto.score(),
				hymnDto.id());
		if (!scoreStorage.isOk()) {
			throw scoreStorage.getErr();
		}
		return ResponseEntity.ok(scoreStorage.getData());
	}

	/**
	 * 楽譜アプロード画面へ移動する
	 *
	 * @param pageNum ページナンバー
	 * @return ModelAndView
	 */
	@GetMapping(ProjectURLConstants.URL_TO_SCORE_UPLOAD)
	public @NotNull ModelAndView toScoreUpload(@RequestParam final Long scoreId, @RequestParam final Integer pageNum) {
		final ModelAndView modelAndView = new ModelAndView("hymns-score-upload");
		final CoResult<HymnDto, DataAccessException> hymnInfoById = this.iHymnService.getHymnInfoById(scoreId);
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final HymnDto hymnDto = hymnInfoById.getData();
		modelAndView.addObject(ProjectConstants.ATTRNAME_EDITED_INFO, hymnDto);
		modelAndView.addObject(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
		return modelAndView;
	}

}
