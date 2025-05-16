package be.labil.anacarde.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> conv : converters) {
			if (conv instanceof MappingJackson2HttpMessageConverter jackson) {
				// ajoute application/octet-stream à la liste des types supportés
				var types = new ArrayList<>(jackson.getSupportedMediaTypes());
				types.add(MediaType.APPLICATION_OCTET_STREAM);
				jackson.setSupportedMediaTypes(types);
			}
		}
	}
}
