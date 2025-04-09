package be.labil.anacarde.infrastructure.config.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.locationtech.jts.geom.Point;

public class JtsModule extends SimpleModule {

	public JtsModule() {
		addSerializer(Point.class, new PointSerializer());
		addDeserializer(Point.class, new PointDeserializer());
	}
}
