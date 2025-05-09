package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.model.User;
import org.hibernate.Hibernate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.mapstruct.Condition;
import org.mapstruct.Named;

public class HibernateLazyCondition {
	@Condition
	protected static boolean isInitialized(Object obj) {
		return obj != null && Hibernate.isInitialized(obj);
	}

	@Named("pointToString")
	protected static String pointToString(Point point) {
		return point != null ? new WKTWriter().write(point) : null;
	}

	@Named("stringToPoint")
	protected static Point stringToPoint(String wkt) {
		if (wkt == null || wkt.isBlank()) return null;
		try {
			return (Point) new WKTReader(new GeometryFactory()).read(wkt);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Format WKT invalide : " + wkt, e);
		}
	}

	@Named("userIdToUser")
	protected static User userIdToUser(Integer userId) {
		if (userId == null) return null;
		User user = new User() {
		}; // si User est abstract, on crée une instance anonyme
		user.setId(userId);
		return user;
	}
}
