package app.preach.gospel.listener;

import java.io.IOException;

import org.jooq.exception.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import app.preach.gospel.service.IStudentService;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

/**
 * ログインコントローラ
 *
 * @author ArkamaHozota
 * @since 2.16
 */
@Log4j2
@Component
public class ProjectLoginSuccessHandler implements AuthenticationSuccessHandler {

	/**
	 * 奉仕者サービスインターフェス
	 */
	@Resource
	private IStudentService iStudentService;

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {
		final String loginAcct = authentication.getName();
		final CoResult<String, DataAccessException> preLoginUpdate = this.iStudentService.preLoginUpdate(loginAcct);
		if (!preLoginUpdate.isOk()) {
			throw preLoginUpdate.getErr();
		}
		log.info("ログイン成功: " + loginAcct);
		request.getSession().setAttribute("loginMessage", preLoginUpdate.getData());
		response.sendRedirect("/home/to-mainmenu-with-login");
	}

}
