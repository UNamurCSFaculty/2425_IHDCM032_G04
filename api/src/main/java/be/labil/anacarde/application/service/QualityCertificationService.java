package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.QualityCertificationDto;
import java.util.List;

/**
 * Service pour la gestion des certifications de qualité.
 */
public interface QualityCertificationService {

	/**
	 * Crée une nouvelle certification de qualité.
	 *
	 * @param qualityCertificationDto
	 *            Les détails de la certification.
	 * @return La certification créée.
	 */
	QualityCertificationDto createQualityCertification(QualityCertificationDto qualityCertificationDto);

	/**
	 * Récupère une certification de qualité par son identifiant.
	 *
	 * @param id
	 *            L'identifiant de la certification.
	 * @return La certification correspondante.
	 */
	QualityCertificationDto getQualityCertificationById(Integer id);

	/**
	 * Récupère toutes les certifications de qualité.
	 *
	 * @return Une liste de toutes les certifications.
	 */
	List<QualityCertificationDto> listQualityCertifications();

	/**
	 * Met à jour une certification existante.
	 *
	 * @param id
	 *            L'identifiant de la certification à mettre à jour.
	 * @param qualityCertificationDto
	 *            Les nouvelles informations de la certification.
	 * @return La certification mise à jour.
	 */
	QualityCertificationDto updateQualityCertification(Integer id, QualityCertificationDto qualityCertificationDto);

	/**
	 * Supprime une certification de qualité.
	 *
	 * @param id
	 *            L'identifiant de la certification à supprimer.
	 */
	void deleteQualityCertification(Integer id);
}
