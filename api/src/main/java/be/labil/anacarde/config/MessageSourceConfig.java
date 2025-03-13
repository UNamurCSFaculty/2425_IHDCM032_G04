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
 * This class provides bean definitions for message resolution and locale management. It configures
 * a MessageSource for internationalization by loading resource bundles (e.g., messages.properties)
 * and a LocaleResolver to determine the default locale for the application.
 */
public class MessageSourceConfig {

    /**
     * This method instantiates a ResourceBundleMessageSource, sets its basename to "messages"
     * (which corresponds to messages.properties, messages_fr.properties, etc.), and sets the
     * default encoding to UTF-8.
     *
     * @return A configured MessageSource instance.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages"); // messages.properties, messages_fr.properties, etc.
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * This method instantiates a SessionLocaleResolver and sets its default locale to French. The
     * LocaleResolver determines the locale for the application based on the current user session.
     *
     * @return A configured LocaleResolver instance.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.FRENCH);
        return localeResolver;
    }
}
