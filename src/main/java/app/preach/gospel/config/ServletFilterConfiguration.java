package app.preach.gospel.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.log4j.Log4j2;

/**
 * Struts2設定クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Log4j2
@Configuration
public class ServletFilterConfiguration {

	// @Configuration
	@Order(15)
	@Bean
	@Qualifier("nlpCache")
	protected Cache<Object, Object> nlpCache() {
		return Caffeine.newBuilder().maximumSize(3300).expireAfterWrite(Duration.ofHours(3L)).recordStats().build();
	}

}
