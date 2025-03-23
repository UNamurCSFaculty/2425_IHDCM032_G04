package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.ProducerDto;
import java.util.List;

/**
 * Interface définissant les méthodes de gestion des producteurs.
 */
public interface ProducerService {

	/**
	 * Crée un nouveau producteur.
	 *
	 * @param producerDto
	 *            Le DTO du producteur à créer.
	 * @return Le ProducerDto créé.
	 */
	ProducerDto createProducer(ProducerDto producerDto);

	/**
	 * Récupère un producteur par son identifiant.
	 *
	 * @param id
	 *            L'identifiant unique du producteur.
	 * @return Le ProducerDto correspondant.
	 */
	ProducerDto getProducerById(Integer id);

	/**
	 * Liste tous les producteurs.
	 *
	 * @return Une liste de ProducerDto.
	 */
	List<ProducerDto> listProducers();

	/**
	 * Met à jour un producteur existant.
	 *
	 * @param id
	 *            L'identifiant du producteur à mettre à jour.
	 * @param producerDto
	 *            Les nouvelles informations du producteur.
	 * @return Le ProducerDto mis à jour.
	 */
	ProducerDto updateProducer(Integer id, ProducerDto producerDto);

	/**
	 * Supprime un producteur.
	 *
	 * @param id
	 *            L'identifiant du producteur à supprimer.
	 */
	void deleteProducer(Integer id);
}
