package app.preach.gospel.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.log4j.Log4j2;

/**
 * SpringMVC配置クラス
 *
 * @author ArkamaHozota
 * @since 6.11
 */
@Log4j2
@Configuration
public class SpringWebMvcConfiguration implements WebMvcConfigurer {

//	/**
//	 * 静的なリソースのマッピングを設定する
//	 *
//	 * @param registry レジストリ
//	 */
//	@Override
//	protected void addResourceHandlers(final @NonNull ResourceHandlerRegistry registry) {
//		log.info(ProjectConstants.MESSAGE_SPRING_MAPPER);
//		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//	}

	/**
	 * ビューのコントローラを定義する
	 *
	 * @param registry
	 */
	@Override
	public void addViewControllers(final @NonNull ViewControllerRegistry registry) {
		registry.addViewController("/home/to-login").setViewName("login-toroku");
//		registry.addViewController("/home/to-mainmenu").setViewName("mainmenu");
		registry.addViewController("/hymns/to-random-five").setViewName("hymns-random-five");
	}

//	/**
//	 * SpringMVCフレームワークを拡張するメッセージ・コンバーター
//	 *
//	 * @param converters コンバーター
//	 */
//	@Override
//	public void extendMessageConverters(final @NonNull List<HttpMessageConverter<?>> converters) {
//		log.info(ProjectConstants.MESSAGE_SPRING_MVCCONVERTOR);
//		final var messageConverter = new MappingJackson2HttpMessageConverter();
//		messageConverter.setObjectMapper(new JacksonObjectMapper());
//		converters.add(1, messageConverter);
//	}

	// @Configuration
	@Order(15)
	@Bean
	@Qualifier("nlpCache")
	protected Cache<Object, Object> nlpCache() {
		return Caffeine.newBuilder().maximumSize(3300).expireAfterWrite(Duration.ofHours(3L)).recordStats().build();
	}

}
