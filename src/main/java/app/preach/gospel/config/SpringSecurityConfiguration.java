package app.preach.gospel.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.listener.ProjectLoginSuccessHandler;
import app.preach.gospel.listener.ProjectLogoutSuccessHandler;
import app.preach.gospel.listener.ProjectUserDetailsService;

/**
 * SpringSecurity配置クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

	/**
	 * 除外するパス
	 */
	private static final String[] IGNORANCE_PATHS = { "/home/index", "/home/page", "/home/to-home-page",
			"/home/to-login-with-error", "/error-page2", "/static/**", "/swagger-ui/**", "/v3/api-docs/**",
			"/hymns/pagination", "/hymns/get-info-id", "/hymns/get-records", "/hymns/kanumi-retrieve",
			"/hymns/random-retrieve", "/hymns/score-download" };

	private static final Logger log = LogManager.getLogger(SpringSecurityConfiguration.class);

	/**
	 * ログインエラー処理
	 */
	private final ProjectAuthenticationEntryPoint projectAuthenticationEntryPoint;

	/**
	 * ログインサービス
	 */
	private final ProjectLoginSuccessHandler projectLoginSuccessHandler;

	/**
	 * ログアウトサービス
	 */
	private final ProjectLogoutSuccessHandler projectLogoutSuccessHandler;

	/**
	 * ログインDTOサービス
	 */
	private final ProjectUserDetailsService projectUserDetailsService;

	/**
	 * コンストラクタ
	 *
	 * @param projectAuthenticationEntryPoint
	 * @param projectLoginSuccessHandler
	 * @param projectLogoutSuccessHandler
	 * @param projectUserDetailsService
	 */
	protected SpringSecurityConfiguration(final ProjectAuthenticationEntryPoint projectAuthenticationEntryPoint,
			final ProjectLoginSuccessHandler projectLoginSuccessHandler,
			final ProjectLogoutSuccessHandler projectLogoutSuccessHandler,
			final ProjectUserDetailsService projectUserDetailsService) {
		this.projectAuthenticationEntryPoint = projectAuthenticationEntryPoint;
		this.projectLoginSuccessHandler = projectLoginSuccessHandler;
		this.projectLogoutSuccessHandler = projectLogoutSuccessHandler;
		this.projectUserDetailsService = projectUserDetailsService;
	}

	@Bean
	@Order(1)
	protected AuthenticationManager authenticationManager(final @NonNull AuthenticationManagerBuilder authBuilder) {
		return authBuilder.authenticationProvider(this.daoAuthenticationProvider()).getObject();
	}

	@Bean
	@Order(0)
	protected DaoAuthenticationProvider daoAuthenticationProvider() {
		final ProjectDaoAuthenticationProvider provider = new ProjectDaoAuthenticationProvider();
		provider.setUserDetailsService(this.projectUserDetailsService);
		provider.setPasswordEncoder(new BCryptPasswordEncoder(BCryptVersion.$2A, 7));
		return provider;
	}

	@Bean
	@Order(2)
	protected SecurityFilterChain filterChain(final @NonNull HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.authorizeHttpRequests(authorize -> authorize.requestMatchers(IGNORANCE_PATHS).permitAll()
						.requestMatchers("/hymns/to-edition").hasAuthority("hymns%edition")
						.requestMatchers("/hymns/delete-check").hasAuthority("hymns%deletion")
						.requestMatchers("/students/to-edition").hasAuthority("students%retrievEdition").anyRequest()
						.authenticated())
				.csrf(csrf -> csrf.ignoringRequestMatchers("/static/**")
						.csrfTokenRepository(new CookieCsrfTokenRepository()))
				.exceptionHandling(handling -> {
					handling.authenticationEntryPoint(this.projectAuthenticationEntryPoint);
					handling.accessDeniedHandler((request, response, ex) -> {
						response.setStatus(HttpStatus.FORBIDDEN.value());
						response.setContentType("application/json;charset=UTF-8");
						final String body = """
								{
								  "status": 403,
								  "error": "Forbidden",
								  "message": "%s",
								  "path": "%s"
								}
								""".formatted(ProjectConstants.MESSAGE_SPRINGSECURITY_REQUIRED_AUTH,
								request.getRequestURI());
						response.getWriter().write(body);
						log.warn(ProjectConstants.MESSAGE_SPRINGSECURITY_REQUIRED_AUTH, ex);
					});
				})
				.formLogin(formLogin -> formLogin.loginPage("/home/to-login").loginProcessingUrl("/home/do-login")
						.successHandler(this.projectLoginSuccessHandler).permitAll().usernameParameter("loginAcct")
						.passwordParameter("userPswd"))
				.logout(logout -> logout.logoutUrl("/home/do-logout")
						.logoutSuccessHandler(this.projectLogoutSuccessHandler));
		log.info(ProjectConstants.MESSAGE_SPRING_SECURITY);
		return httpSecurity.build();
	}

//	@Bean
//	@Order(4)
//	protected WebSecurityCustomizer webSecurityCustomizer() {
//		return (web) -> web.ignoring().requestMatchers("/favicon.ico", "/error");
//	}

}
