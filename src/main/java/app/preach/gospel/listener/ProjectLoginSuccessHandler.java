package app.preach.gospel.listener;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import app.preach.gospel.service.IStudentService;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ログインコントローラ
 *
 * @author ArkamaHozota
 * @since 2.16
 */
@Component
public class ProjectLoginSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger log = LogManager.getLogger(ProjectLoginSuccessHandler.class);

	/**
	 * 奉仕者サービスインターフェス
	 */
	@Resource
	private IStudentService iStudentService;

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {
		final String loginAcct = authentication.getName();
		final CoResult<String, Object> preLoginUpdate = this.iStudentService.preLoginUpdate(loginAcct);
		if (!preLoginUpdate.isOk()) {
			throw (DataAccessException) preLoginUpdate.getErr();
		}
		log.info("ログイン成功: " + loginAcct);
		request.getSession().setAttribute("loginMessage", preLoginUpdate.getData());
		response.sendRedirect("/home/to-mainmenu");
	}

}
