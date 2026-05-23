package app.preach.gospel.config;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

/**
 * SpringMVCフレームワークを拡張するメッセージ・コンバーター
 *
 * @author ArkamaHozota
 * @since 6.11
 */
@Configuration
public class JacksonConfiguration {

	@Bean
	protected Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
		return builder -> {
			builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
			builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
					new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
					new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
			builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
					new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
					new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		};
	}

	@Bean
	protected Module numberStringModule() {
		final var module = new SimpleModule();
		module.addSerializer(Long.class, ToStringSerializer.instance);
		module.addSerializer(Long.TYPE, ToStringSerializer.instance);
		module.addSerializer(BigInteger.class, ToStringSerializer.instance);
		return module;
	}

}
