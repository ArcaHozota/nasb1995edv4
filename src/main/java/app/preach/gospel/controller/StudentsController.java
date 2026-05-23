package app.preach.gospel.controller;

import java.io.Serial;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.service.IStudentService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.CoStringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

/**
 * 奉仕者管理ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RequestMapping("/students")
@Controller
@Tag(name = "奉仕者管理ハンドラ", description = "奉仕者管理に関わる操作を扱うエンドポイント")
public final class StudentsController {

	@Serial
	private static final long serialVersionUID = 1592265866534993918L;

	/**
	 * 奉仕者サービスインターフェス
	 */
	@Resource
	private IStudentService iStudentService;

	/**
	 * アカウント重複チェック
	 *
	 * @param id           ID
	 * @param loginAccount アカウント
	 * @return ResultDto<String>
	 */
	@GetMapping("/check-duplicated")
	@ResponseBody
	@Operation(summary = "情報検索", description = "アカウント重複チェック")
	public @NotNull ResponseEntity<String> checkDuplicated(@RequestParam final String id,
			@RequestParam final String loginAccount) {
		final CoResult<Integer, DataAccessException> checkDuplicated = this.iStudentService.checkDuplicated(id,
				loginAccount);
		if (!checkDuplicated.isOk()) {
			throw checkDuplicated.getErr();
		}
		final var checkDuplicatedOk = checkDuplicated.getData();
		if (checkDuplicatedOk >= 1) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ProjectConstants.MESSAGE_STUDENT_NAME_DUPLICATED);
		}
		return ResponseEntity.ok(CoStringUtils.EMPTY_STRING);
	}

	/**
	 * 奉仕者情報を更新する
	 *
	 * @param studentDto 情報転送クラス
	 * @return ResultDto<String>
	 */
	@PutMapping("/info-update")
	@ResponseBody
	@Operation(summary = "情報更新", description = "奉仕者情報を更新する")
	public @NotNull ResponseEntity<String> infoUpdate(@RequestBody final StudentDto studentDto) {
		final CoResult<String, DataAccessException> infoUpdation = this.iStudentService.infoUpdation(studentDto);
		if (!infoUpdation.isOk()) {
			throw infoUpdation.getErr();
		}
		return ResponseEntity.ok(infoUpdation.getData());
	}

//	/**
//	 * ログイン時間記録
//	 *
//	 * @param loginAccount アカウント
//	 * @param password     パスワード
//	 * @return ResultDto<String>
//	 */
//	@GetMapping("/pre-login")
//	@ResponseBody
//	@Operation(summary = "情報更新", description = "ログイン時間記録")
//	public @NotNull ResponseEntity<String> preLogin(@RequestParam final String loginAccount,
//			@RequestParam final String password) {
//		final CoResult<String, DataAccessException> preLoginUpdation = this.iStudentService.preLoginUpdate(loginAccount,
//				password);
//		if (!preLoginUpdation.isOk()) {
//			throw preLoginUpdation.getErr();
//		}
//		return ResponseEntity.ok(preLoginUpdation.getData());
//	}

	/**
	 * 情報更新画面へ移動する
	 *
	 * @param editId 編集ID
	 * @return ModelAndView
	 */
	@GetMapping("/to-edition")
	public @NotNull ModelAndView toEdition(@RequestParam final Long userId) {
		final ModelAndView modelAndView = new ModelAndView("students-edition");
		final CoResult<StudentDto, DataAccessException> studentInfoById = this.iStudentService
				.getStudentInfoById(userId);
		if (!studentInfoById.isOk()) {
			throw studentInfoById.getErr();
		}
		modelAndView.addObject(ProjectConstants.ATTRNAME_EDITED_INFO, studentInfoById.getData());
		return modelAndView;
	}

}
