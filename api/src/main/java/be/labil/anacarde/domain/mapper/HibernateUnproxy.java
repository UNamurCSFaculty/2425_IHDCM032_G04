package be.labil.anacarde.domain.mapper;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;

@Component
public class HibernateUnproxy {

	/**
	 * Si c'est un proxy non initialisé, renvoie null ; sinon la vraie instance.
	 */
	public <T> T unproxy(T entity) {
		if (entity instanceof HibernateProxy proxy) {
			if (!Hibernate.isInitialized(proxy)) {
				return null;
			}
			// retourne l’implémentation « réelle »
			@SuppressWarnings("unchecked")
			T unproxied = (T) proxy.getHibernateLazyInitializer().getImplementation();
			return unproxied;
		}
		return entity;
	}
}