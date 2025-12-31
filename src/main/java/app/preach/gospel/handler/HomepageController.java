package app.preach.gospel.handler;

import java.io.Serial;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;

/**
 * 共通とSVGイメージハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Controller
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
	@GetMapping("/category/login-with-error")
	public @NotNull ModelAndView loginWithError() {
		final CoResult<Long, DataAccessException> totalCounts = this.iHymnService.getTotalCounts();
		if (!totalCounts.isOk()) {
			throw totalCounts.getErr();
		}
		final ModelAndView modelAndView = new ModelAndView("login-toroku");
		modelAndView.addObject(ProjectConstants.ATTRNAME_RECORDS, totalCounts.getData());
		modelAndView.addObject(ProjectConstants.ATTRNAME_TOROKU_MSG, ProjectConstants.MESSAGE_STRING_NOT_LOGIN);
		return modelAndView;
	}

	/**
	 * ホームページへ移動する
	 *
	 * @return ModelAndView
	 */
	@GetMapping(value = { "/home/index", "/home/page", "/home/to-home-page", "/", "index.action" })
	public @NotNull ModelAndView toHomePage() {
		final ModelAndView modelAndView = new ModelAndView("index");
		final CoResult<Long, DataAccessException> totalCounts = this.iHymnService.getTotalCounts();
		if (!totalCounts.isOk()) {
			throw totalCounts.getErr();
		}
		modelAndView.addObject(ProjectConstants.ATTRNAME_RECORDS, totalCounts.getData());
		return modelAndView;
	}

	/**
	 * メインメニュへ移動する
	 *
	 * @return ModelAndView
	 */
	@GetMapping("/category/to-mainmenu-with-login")
	public @NotNull ModelAndView toMainmenuWithLogin() {
		final ModelAndView modelAndView = new ModelAndView("mainmenu");
		modelAndView.addObject("loginMsg", ProjectConstants.MESSAGE_STRING_LOGIN_SUCCESS);
		return modelAndView;
	}

}
