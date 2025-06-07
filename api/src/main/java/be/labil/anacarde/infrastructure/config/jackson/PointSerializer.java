package be.labil.anacarde.infrastructure.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.locationtech.jts.geom.Point;

/**
 * Sérialiseur Jackson pour convertir une instance JTS {@link Point}
 * en représentation JSON simple.
 * <p>
 * Produit un objet JSON contenant les propriétés « x » et « y »
 * correspondant aux coordonnées du point.
 */
public class PointSerializer extends StdSerializer<Point> {

	public PointSerializer() {
		super(Point.class);
	}

	/**
	 * Sérialise un {@link Point} JTS en JSON.
	 * <p>
	 * Si le point est {@code null}, écrit une valeur JSON null.
	 * Sinon, écrit un objet JSON avec deux champs numériques :
	 * <ul>
	 *   <li>{@code "x"} : coordonnée X du point</li>
	 *   <li>{@code "y"} : coordonnée Y du point</li>
	 * </ul>
	 *
	 * @param point    l’objet {@code Point} à sérialiser
	 * @param gen      le générateur JSON Jackson
	 * @param provider le fournisseur de sérialiseurs (non utilisé ici)
	 * @throws IOException en cas d’erreur d’écriture dans le flux JSON
	 */
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
