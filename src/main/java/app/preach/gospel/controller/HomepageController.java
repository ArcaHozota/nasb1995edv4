package app.preach.gospel.controller;

import java.io.Serial;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.CoStringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

/**
 * 共通とSVGイメージハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RequestMapping("/home")
@Controller
@Tag(name = "ホームページハンドラ", description = "共通とSVGイメージに関わる操作を扱うエンドポイント")
public final class HomepageController {

	@Serial
	private static final long serialVersionUID = -3971408230922185628L;

	/**
	 * 賛美歌サービスインターフェス
	 */
	@Resource
	private IHymnService iHymnService;

	/**
	 * ログインページへ移動する
	 *
	 * @return ModelAndView
	 */
	@GetMapping("/to-login-with-error")
	@Operation(summary = "画面遷移", description = "ログインページへ移動する")
	public @NotNull ModelAndView loginWithError() {
		final CoResult<Long, DataAccessException> totalCounts = this.iHymnService.getTotalCounts();
		if (!totalCounts.isOk()) {
			throw totalCounts.getErr();
		}
		final var modelAndView = new ModelAndView("login-toroku");
		modelAndView.addObject(ProjectConstants.ATTRNAME_RECORDS, totalCounts.getData());
		modelAndView.addObject(ProjectConstants.ATTRNAME_TOROKU_MSG, ProjectConstants.MESSAGE_STRING_NOT_LOGIN);
		return modelAndView;
	}

	/**
	 * ホームページへ移動する
	 *
	 * @return ModelAndView
	 */
	@GetMapping(value = { "/index", "/page", "/to-home-page" })
	@Operation(summary = "画面遷移", description = "ホームページへ移動する")
	public @NotNull ModelAndView toHomePage(final HttpSession session) {
		final var modelAndView = new ModelAndView("index");
		final CoResult<Long, DataAccessException> totalCounts = this.iHymnService.getTotalCounts();
		if (!totalCounts.isOk()) {
			throw totalCounts.getErr();
		}
		final var message = (String) session.getAttribute("notLoginMsg");
		if (CoStringUtils.isEmpty(message)) {
			modelAndView.addObject(ProjectConstants.ATTRNAME_RECORDS, totalCounts.getData());
			return modelAndView;
		}
		modelAndView.addObject(ProjectConstants.ATTRNAME_TOROKU_MSG, message);
		modelAndView.addObject(ProjectConstants.ATTRNAME_RECORDS, totalCounts.getData());
		return modelAndView;
	}

	/**
	 * メインメニュへ移動する
	 *
	 * @return ModelAndView
	 */
	@GetMapping("/to-mainmenu")
	@Operation(summary = "画面遷移", description = "メインメニュへ移動する")
	public @NotNull ModelAndView toMainmenuWithLogin(final HttpSession session) {
		final var modelAndView = new ModelAndView("mainmenu");
		final var message = (String) session.getAttribute("loginMessage");
		if (CoStringUtils.isEmpty(message)) {
			modelAndView.addObject("loginMsg", CoStringUtils.EMPTY_STRING);
			return modelAndView;
		}
		session.removeAttribute("loginMessage");
		modelAndView.addObject("loginMsg", message);
		return modelAndView;
	}

}
