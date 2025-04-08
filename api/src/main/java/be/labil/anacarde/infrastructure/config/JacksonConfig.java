package be.labil.anacarde.infrastructure.config;

import be.labil.anacarde.infrastructure.config.jackson.JtsModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer addJtsModule() {
		return builder -> builder.modulesToInstall(new JtsModule());
	}
}