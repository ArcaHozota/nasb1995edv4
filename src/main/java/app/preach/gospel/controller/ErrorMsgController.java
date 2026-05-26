package app.preach.gospel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.utils.CoStringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * エラー処理ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Controller
@Tag(name = "エラー処理ハンドラ", description = "エラー処理に関わる操作を扱うエンドポイント")
public final class ErrorMsgController {

	@GetMapping("/error-page")
	public ModelAndView errorHandle(@RequestParam final String errMsg) {
		final var mav = new ModelAndView("error");
		if (CoStringUtils.isEqual(errMsg, ProjectConstants.MESSAGE_STRING_FATAL_ERROR)) {
			mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
			mav.addObject("message", errMsg);
			return mav;
		}
		mav.addObject("status", HttpStatus.CONFLICT.toString());
		mav.addObject("message", errMsg);
		return mav;
	}

}
