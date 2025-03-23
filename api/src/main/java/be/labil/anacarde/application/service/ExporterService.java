package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.ExporterDto;
import java.util.List;

/**
 * Interface définissant les opérations de gestion des exportateurs.
 */
public interface ExporterService {

	/**
	 * Crée un nouvel exportateur dans le système.
	 *
	 * @param exporterDto
	 *            Le ExporterDto contenant les informations du nouvel exportateur.
	 * @return Un ExporterDto représentant l'exportateur créé.
	 */
	ExporterDto createExporter(ExporterDto exporterDto);

	/**
	 * Retourne l'exportateur correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique de l'exportateur.
	 * @return Un ExporterDto représentant l'exportateur avec l'ID spécifié.
	 */
	ExporterDto getExporterById(Integer id);

	/**
	 * Récupère tous les exportateurs du système.
	 *
	 * @return Une liste de ExporterDto représentant tous les exportateurs.
	 */
	List<ExporterDto> listExporters();

	/**
	 * Met à jour un exportateur identifié par l'ID donné.
	 *
	 * @param id
	 *            L'identifiant unique de l'exportateur à mettre à jour.
	 * @param exporterDto
	 *            Le ExporterDto contenant les nouvelles informations.
	 * @return Un ExporterDto représentant l'exportateur mis à jour.
	 */
	ExporterDto updateExporter(Integer id, ExporterDto exporterDto);

	/**
	 * Supprime un exportateur du système.
	 *
	 * @param id
	 *            L'identifiant unique de l'exportateur à supprimer.
	 */
	void deleteExporter(Integer id);
}