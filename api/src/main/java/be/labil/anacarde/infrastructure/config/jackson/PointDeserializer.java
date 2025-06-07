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

/**
 * Désérialiseur Jackson pour convertir une représentation JSON d’un point
 * en instance JTS {@link Point}.
 * <p>
 * Attend un objet JSON avec des champs « x » et « y » (coordonnées).
 * Utilise une {@link GeometryFactory} pour créer le {@code Point}.
 */
public class PointDeserializer extends StdDeserializer<Point> {

	private GeometryFactory geometryFactory = new GeometryFactory();
	public PointDeserializer() {
		super(Point.class);
	}

	/**
	 * Lit le flux JSON et extrait les valeurs « x » et « y » pour reconstruire
	 * un objet {@link Point}.
	 *
	 * @param p     le parser JSON en cours
	 * @param ctxt  contexte de désérialisation (non utilisé ici)
	 * @return      un {@code Point} JTS avec les coordonnées lues
	 * @throws IOException              en cas d’erreur de lecture JSON
	 * @throws JsonProcessingException  en cas d’erreur de traitement JSON
	 */
	@Override
	public Point deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		double x = node.get("x").asDouble();
		double y = node.get("y").asDouble();
		return geometryFactory.createPoint(new Coordinate(x, y));
	}
}
