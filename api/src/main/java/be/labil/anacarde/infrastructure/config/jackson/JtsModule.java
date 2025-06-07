package be.labil.anacarde.infrastructure.config.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.locationtech.jts.geom.Point;

/**
 * Module Jackson personnalisé pour la (dé)sérialisation des objets géométriques JTS.
 * <p>
 * Enregistre un {@link PointSerializer} et un {@link PointDeserializer} afin de
 * convertir les instances de {@link Point} vers/depuis leur représentation JSON.
 */
public class JtsModule extends SimpleModule {

	/**
	 * Initialise le module en ajoutant les serializers et deserializers pour la classe {@link Point}.
	 * <p>
	 * Le {@code PointSerializer} transforme un objet {@code Point} en JSON (par exemple GeoJSON),
	 * tandis que le {@code PointDeserializer} reconstruit un objet {@code Point} à partir de son JSON.
	 */
	public JtsModule() {
		addSerializer(Point.class, new PointSerializer());
		addDeserializer(Point.class, new PointDeserializer());
	}
}
