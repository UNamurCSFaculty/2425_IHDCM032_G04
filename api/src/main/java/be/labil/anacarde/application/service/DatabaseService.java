package be.labil.anacarde.application.service;

import java.io.IOException;

/**
 * Service pour la gestion de la base de données. Permet de vérifier si la base de données est
 * initialisée, de la supprimer et de la créer.
 */
public interface DatabaseService {

	/**
	 * Vérifie si la base de données est initialisée.
	 *
	 * @return true si la base de données est initialisée, false sinon.
	 */
	boolean isInitialized();

	/**
	 * Supprime la base de données.
	 *
	 * @throws IOException
	 *             si une erreur survient lors de la suppression de la base de données.
	 */
	void dropDatabase() throws IOException;

	/**
	 * Crée la base de données.
	 *
	 * @throws IOException
	 *             si une erreur survient lors de la création de la base de données.
	 */
	void createDatabase() throws IOException;
}
