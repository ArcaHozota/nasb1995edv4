package app.preach.gospel.handler;

import java.io.Serial;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import app.preach.gospel.utils.Pagination;
import jakarta.annotation.Resource;

/**
 * 賛美歌管理ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RequestMapping(ProjectURLConstants.URL_HYMNS_NAMESPACE)
@Controller
public final class HymnsController {

	@Serial
	private static final long serialVersionUID = -6535194800678567557L;

	/**
	 * 賛美歌サービスインターフェス
	 */
	@Resource
	private IHymnService iHymnService;

	/**
	 * 歌の名称の重複性をチェックする
	 *
	 * @param id     ID
	 * @param nameJp 日本語名称
	 * @return ResponseEntity<String>
	 */
	@GetMapping(ProjectURLConstants.URL_CHECK_NAME)
	@ResponseBody
	public @NotNull ResponseEntity<String> checkDuplicated(@RequestParam final String id,
			@RequestParam final String nameJp) {
		final CoResult<Integer, DataAccessException> checkDuplicated = this.iHymnService.checkDuplicated(id, nameJp);
		if (!checkDuplicated.isOk()) {
			throw checkDuplicated.getErr();
		}
		final Integer checkDuplicatedOk = checkDuplicated.getData();
		if (checkDuplicatedOk >= 1) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ProjectConstants.MESSAGE_HYMN_NAME_DUPLICATED);
		}
		return ResponseEntity.ok(CoStringUtils.EMPTY_STRING);
	}

	/**
	 * 歌の名称の重複性をチェックする2
	 *
	 * @param id     ID
	 * @param nameKr 韓国語名称
	 * @return ResponseEntity<String>
	 */
	@GetMapping(ProjectURLConstants.URL_CHECK_NAME2)
	@ResponseBody
	public @NotNull ResponseEntity<String> checkDuplicated2(@RequestParam final String id,
			@RequestParam final String nameKr) {
		final CoResult<Integer, DataAccessException> checkDuplicated = this.iHymnService.checkDuplicated2(id, nameKr);
		if (!checkDuplicated.isOk()) {
			throw checkDuplicated.getErr();
		}
		final Integer checkDuplicatedOk = checkDuplicated.getData();
		if (checkDuplicatedOk >= 1) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ProjectConstants.MESSAGE_HYMN_NAME_DUPLICATED);
		}
		return ResponseEntity.ok(CoStringUtils.EMPTY_STRING);
	}

	/**
	 * 削除権限チェック
	 *
	 * @return ResponseEntity<String>
	 */
	@GetMapping(ProjectURLConstants.URL_CHECK_DELETE)
	@ResponseBody
	public @NotNull ResponseEntity<String> deletionCheck() {
		return ResponseEntity.ok(CoStringUtils.EMPTY_STRING);
	}

	/**
	 * IDによって賛美歌情報を検索する
	 *
	 * @param hymnId ID
	 * @return ResponseEntity<HymnDto>
	 */
	@GetMapping(ProjectURLConstants.URL_GET_INFO_ID)
	@ResponseBody
	public ResponseEntity<HymnDto> getInfoById(@RequestParam final Long hymnId) {
		final CoResult<HymnDto, DataAccessException> hymnInfoById = this.iHymnService.getHymnInfoById(hymnId);
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		return ResponseEntity.ok(hymnInfoById.getData());
	}

	/**
	 * 賛美歌情報を削除する
	 *
	 * @param deleteId 編集ID
	 * @return ResponseEntity<String>
	 */
	@DeleteMapping(ProjectURLConstants.URL_INFO_DELETION)
	@ResponseBody
	public @NotNull ResponseEntity<String> infoDeletion(@RequestParam final Long deleteId) {
		final CoResult<String, DataAccessException> infoDeletion = this.iHymnService.infoDeletion(deleteId);
		if (!infoDeletion.isOk()) {
			throw infoDeletion.getErr();
		}
		return ResponseEntity.ok(infoDeletion.getData());
	}

	/**
	 * 賛美歌情報を保存する
	 *
	 * @param hymnDto 情報転送クラス
	 * @return ResponseEntity<Integer>
	 */
	@PostMapping(ProjectURLConstants.URL_INFO_STORAGE)
	@ResponseBody
	public @NotNull ResponseEntity<Integer> infoStorage(@RequestBody final HymnDto hymnDto) {
		final CoResult<Integer, DataAccessException> infoStorage = this.iHymnService.infoStorage(hymnDto);
		if (!infoStorage.isOk()) {
			throw infoStorage.getErr();
		}
		return ResponseEntity.ok(infoStorage.getData());
	}

	/**
	 * 賛美歌情報を更新する
	 *
	 * @param hymnDto 情報転送クラス
	 * @return ResponseEntity<String>
	 */
	@PutMapping(ProjectURLConstants.URL_INFO_UPDATE)
	@ResponseBody
	public @NotNull ResponseEntity<String> infoUpdate(@RequestBody final HymnDto hymnDto) {
		final CoResult<String, DataAccessException> infoUpdation = this.iHymnService.infoUpdate(hymnDto);
		if (!infoUpdation.isOk()) {
			throw infoUpdation.getErr();
		}
		return ResponseEntity.ok(infoUpdation.getData());
	}

	/**
	 * 情報一覧画面初期表示する
	 *
	 * @param pageNum ページナンバー
	 * @param keyword キーワード
	 * @return ResponseEntity<Pagination<HymnDto>>
	 */
	@GetMapping(ProjectURLConstants.URL_PAGINATION)
	@ResponseBody
	public @NotNull ResponseEntity<Pagination<HymnDto>> pagination(@RequestParam final Integer pageNum,
			@RequestParam(required = false, defaultValue = CoStringUtils.EMPTY_STRING) final String keyword) {
		final CoResult<Pagination<HymnDto>, DataAccessException> hymnsByKeyword = this.iHymnService
				.getHymnsInfoByPagination(pageNum, keyword);
		if (!hymnsByKeyword.isOk()) {
			throw hymnsByKeyword.getErr();
		}
		final Pagination<HymnDto> pagination = hymnsByKeyword.getData();
		return ResponseEntity.ok(pagination);
	}

	/**
	 * ランダム五つを検索する
	 *
	 * @param keyword キーワード
	 * @return ResponseEntity<List<HymnDto>>
	 */
	@GetMapping(ProjectURLConstants.URL_RANDOM_RETRIEVE)
	@ResponseBody
	public @NotNull ResponseEntity<List<HymnDto>> randomRetrieve(@RequestParam final String keyword) {
		final CoResult<List<HymnDto>, DataAccessException> hymnsRandomFive = this.iHymnService
				.getHymnsInfoByRandom(keyword);
		if (!hymnsRandomFive.isOk()) {
			throw hymnsRandomFive.getErr();
		}
		final List<HymnDto> hymnDtos = hymnsRandomFive.getData();
		return ResponseEntity.ok(hymnDtos);
	}

	/**
	 * 情報追加画面へ移動する
	 *
	 * @param pageNum ページナンバー
	 * @return ModelAndView
	 */
	@GetMapping(ProjectURLConstants.URL_TO_ADDITION)
	public @NotNull ModelAndView toAddition(@RequestParam final String pageNum) {
		final ModelAndView modelAndView = new ModelAndView("hymns-addition");
		modelAndView.addObject(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
		return modelAndView;
	}

	/**
	 *
	 * /** 情報更新画面へ移動する
	 *
	 * @param editId  編集ID
	 * @param pageNum ページナンバー
	 * @return ModelAndView
	 */
	@GetMapping(ProjectURLConstants.URL_TO_EDITION)
	public @NotNull ModelAndView toEdition(@RequestParam final Long editId, @RequestParam final Integer pageNum) {
		final ModelAndView modelAndView = new ModelAndView("hymns-edition");
		final CoResult<HymnDto, DataAccessException> hymnInfoById = this.iHymnService.getHymnInfoById(editId);
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final HymnDto hymnDto = hymnInfoById.getData();
		modelAndView.addObject(ProjectConstants.ATTRNAME_EDITED_INFO, hymnDto);
		modelAndView.addObject(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
		return modelAndView;
	}

	/**
	 * 情報一覧画面へ移動する
	 *
	 * @param pageNum ページナンバー
	 * @return ModelAndView
	 */
	@GetMapping(ProjectURLConstants.URL_TO_PAGES)
	public @NotNull ModelAndView toPages(@RequestParam final String pageNum) {
		final ModelAndView modelAndView = new ModelAndView("hymns-pagination");
		if (CoStringUtils.isDigital(pageNum)) {
			modelAndView.addObject(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
			return modelAndView;
		}
		modelAndView.addObject(ProjectConstants.ATTRNAME_PAGE_NUMBER, "1");
		return modelAndView;
	}

}
