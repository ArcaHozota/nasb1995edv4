package app.preach.gospel.listener;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ログアウトコントローラ
 *
 * @author ArkamaHozota
 * @since 2.33
 */
@Component
public class ProjectLogoutSuccessHandler implements LogoutSuccessHandler {

	private static final Logger log = LogManager.getLogger(ProjectLogoutSuccessHandler.class);

	/**
	 * キャッシュ
	 */
	private final Cache<Object, Object> nlpCache;

	/**
	 * コンストラクタ
	 *
	 * @param nlpCache キャッシュ
	 */
	public ProjectLogoutSuccessHandler(@Qualifier("nlpCache") final Cache<Object, Object> nlpCache) {
		this.nlpCache = nlpCache;
	}

	@Override
	public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException {
		// 🔥 清空缓存
		this.nlpCache.invalidateAll();
		// 如有需要可打印统计信息
		log.info("Caffeine stats: {}", this.nlpCache.stats());
		// ホームページへの遷移
		response.sendRedirect("/home/index");
	}

}
