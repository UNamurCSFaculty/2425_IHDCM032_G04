package be.labil.anacarde.config;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
/**
 * Cette classe fournit les définitions de beans pour la résolution des messages et la gestion de la langue. Elle
 * configure un MessageSource pour l'internationalisation en chargeant les fichiers de ressources (par exemple,
 * messages.properties, messages_fr.properties, etc.) et un LocaleResolver pour définir la langue par défaut de
 * l'application.
 */
public class MessageSourceConfig {

	/**
	 * Instancie un ResourceBundleMessageSource, définit son basename à "messages" (correspondant aux fichiers
	 * messages.properties, messages_fr.properties, etc.) et fixe l'encodage par défaut à UTF-8.
	 *
	 * @return Une instance de MessageSource correctement configurée.
	 */
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages"); // messages.properties, messages_fr.properties, etc.
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	/**
	 * Instancie un SessionLocaleResolver et définit sa langue par défaut sur le français. Le LocaleResolver détermine
	 * la langue de l'application en fonction de la session utilisateur.
	 *
	 * @return Une instance de LocaleResolver correctement configurée.
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.FRENCH);
		return localeResolver;
	}
}
