package be.labil.anacarde.infrastructure.importdata;

import lombok.Data;

/**
 * DTO utilisé pour l’import de données de villes depuis une source externe.
 * <p>
 * Contient les informations de base d’une ville : son identifiant, son nom, l’identifiant de la
 * région associée et ses coordonnées géographiques (latitude et longitude) sous forme de chaînes.
 */
@Data
public class CityImportDto {
	public int id;
	public String name;
	public int region;
	public String lat;
	public String lon;
}