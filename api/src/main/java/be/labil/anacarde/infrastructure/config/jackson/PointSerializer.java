package be.labil.anacarde.infrastructure.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.locationtech.jts.geom.Point;

public class PointSerializer extends StdSerializer<Point> {

	public PointSerializer() {
		super(Point.class);
	}

	@Override
	public void serialize(Point point, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		if (point == null) {
			gen.writeNull();
		} else {
			gen.writeStartObject();
			gen.writeNumberField("x", point.getX());
			gen.writeNumberField("y", point.getY());
			gen.writeEndObject();
		}
	}
}
