package be.labil.anacarde.infrastructure.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class PointDeserializer extends StdDeserializer<Point> {

	private GeometryFactory geometryFactory = new GeometryFactory();

	public PointDeserializer() {
		super(Point.class);
	}

	@Override
	public Point deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		double x = node.get("x").asDouble();
		double y = node.get("y").asDouble();
		return geometryFactory.createPoint(new Coordinate(x, y));
	}
}
