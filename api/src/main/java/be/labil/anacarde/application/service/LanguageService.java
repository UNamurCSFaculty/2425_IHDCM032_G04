package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.LanguageDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des langues.
 */
public interface LanguageService {

	/**
	 * Crée une nouvelle langue dans le système en utilisant le LanguageDto fourni.
	 *
	 * @param languageDto
	 *            Le DTO contenant les informations de la langue à créer.
	 * @return Un LanguageDto représentant la langue créée.
	 */
	LanguageDto createLanguage(LanguageDto languageDto);

	/**
	 * Retourne la langue correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique de la langue.
	 * @return Un LanguageDto représentant la langue avec l'ID spécifié.
	 */
	LanguageDto getLanguageById(Integer id);

	/**
	 * Récupère toutes les langues du système.
	 *
	 * @return Une List de LanguageDto représentant toutes les langues.
	 */
	List<LanguageDto> listLanguages();

	/**
	 * Met à jour la langue identifiée par l'ID donné avec les informations fournies dans le LanguageDto.
	 *
	 * @param id
	 *            L'identifiant unique de la langue à mettre à jour.
	 * @param languageDto
	 *            Le DTO contenant les informations mises à jour.
	 * @return Un LanguageDto représentant la langue mise à jour.
	 */
	LanguageDto updateLanguage(Integer id, LanguageDto languageDto);

	/**
	 * Supprime la langue identifiée par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique de la langue à supprimer.
	 */
	void deleteLanguage(Integer id);
}
