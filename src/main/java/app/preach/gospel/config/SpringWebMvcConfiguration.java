package app.preach.gospel.config;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import lombok.extern.log4j.Log4j2;

/**
 * SpringMVC配置クラス
 *
 * @author ArkamaHozota
 * @since 6.11
 */
@Log4j2
@Configuration
public class SpringWebMvcConfiguration extends WebMvcConfigurationSupport {

	/**
	 * 静的なリソースのマッピングを設定する
	 *
	 * @param registry レジストリ
	 */
	@Override
	protected void addResourceHandlers(final @NonNull ResourceHandlerRegistry registry) {
		log.info(ProjectConstants.MESSAGE_SPRING_MAPPER);
		registry.addResourceHandler(ProjectURLConstants.URL_STATIC_RESOURCE).addResourceLocations("classpath:/static/");
	}

	/**
	 * ビューのコントローラを定義する
	 *
	 * @param registry
	 */
	@Override
	protected void addViewControllers(final @NonNull ViewControllerRegistry registry) {
		registry.addViewController(ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(ProjectURLConstants.URL_TO_LOGIN))
				.setViewName("login-toroku");
		registry.addViewController(
				ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(ProjectURLConstants.URL_TO_MAINMENU))
				.setViewName("mainmenu");
		registry.addViewController(
				ProjectURLConstants.URL_HYMNS_NAMESPACE.concat(ProjectURLConstants.URL_TO_RANDOM_FIVE))
				.setViewName("hymns-random-five");
	}

	/**
	 * SpringMVCフレームワークを拡張するメッセージ・コンバーター
	 *
	 * @param converters コンバーター
	 */
	@Override
	protected void extendMessageConverters(final @NonNull List<HttpMessageConverter<?>> converters) {
		log.info(ProjectConstants.MESSAGE_SPRING_MVCCONVERTOR);
		final MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setObjectMapper(new JacksonObjectMapper());
		converters.add(0, messageConverter);
	}

	// @Configuration
	@Order(15)
	@Bean
	@Qualifier("nlpCache")
	protected Cache<Object, Object> nlpCache() {
		return Caffeine.newBuilder().maximumSize(3300).expireAfterWrite(Duration.ofHours(3L)).recordStats().build();
	}

}
