package be.labil.anacarde.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration Spring MVC pour personnaliser les convertisseurs de messages HTTP.
 * <p>
 * Permet d’ajouter le type MIME {@code application/octet-stream} aux convertisseurs JSON de Jackson
 * afin de traiter les réponses au format binaire comme du JSON.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	/**
	 * Étend la liste des {@link HttpMessageConverter} enregistrés par Spring MVC.
	 * <p>
	 * Parcourt les convertisseurs existants et, pour chaque
	 * {@link MappingJackson2HttpMessageConverter}, ajoute
	 * {@link MediaType#APPLICATION_OCTET_STREAM} aux media types supportés.
	 *
	 * @param converters
	 *            liste des convertisseurs HTTP à modifier
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> conv : converters) {
			if (conv instanceof MappingJackson2HttpMessageConverter jackson) {
				var types = new ArrayList<>(jackson.getSupportedMediaTypes());
				types.add(MediaType.APPLICATION_OCTET_STREAM);
				jackson.setSupportedMediaTypes(types);
			}
		}
	}
}
