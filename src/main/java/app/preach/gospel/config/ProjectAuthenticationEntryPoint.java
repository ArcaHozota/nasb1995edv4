package app.preach.gospel.config;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import app.preach.gospel.common.ProjectConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ログインエラーコントローラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Component
public class ProjectAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger log = LogManager.getLogger(ProjectAuthenticationEntryPoint.class);

	@Override
	public void commence(final HttpServletRequest request, final @NotNull HttpServletResponse response,
			final AuthenticationException authException) throws IOException {
		request.getSession().setAttribute("notLoginMsg", ProjectConstants.MESSAGE_STRING_NOT_LOGIN);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		response.sendRedirect("/home/index");
		log.warn(ProjectConstants.MESSAGE_STRING_NOT_LOGIN);
	}

}
