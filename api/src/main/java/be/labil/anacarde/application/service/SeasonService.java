package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.SeasonDto;
import java.util.List;

/**
 * Interface définissant les méthodes de gestion des saisons agricoles.
 */
public interface SeasonService {

	/**
	 * Crée une nouvelle saison.
	 *
	 * @param seasonDto
	 *            Le DTO de la saison à créer.
	 * @return Le SeasonDto créé.
	 */
	SeasonDto createSeason(SeasonDto seasonDto);

	/**
	 * Récupère une saison par son identifiant.
	 *
	 * @param id
	 *            L'identifiant unique de la saison.
	 * @return Le SeasonDto correspondant.
	 */
	SeasonDto getSeasonById(Integer id);

	/**
	 * Liste toutes les saisons.
	 *
	 * @return Une liste de SeasonDto.
	 */
	List<SeasonDto> listSeasons();

	/**
	 * Met à jour une saison existante.
	 *
	 * @param id
	 *            L'identifiant de la saison à mettre à jour.
	 * @param seasonDto
	 *            Les nouvelles informations de la saison.
	 * @return Le SeasonDto mis à jour.
	 */
	SeasonDto updateSeason(Integer id, SeasonDto seasonDto);

	/**
	 * Supprime une saison.
	 *
	 * @param id
	 *            L'identifiant de la saison à supprimer.
	 */
	void deleteSeason(Integer id);
}
