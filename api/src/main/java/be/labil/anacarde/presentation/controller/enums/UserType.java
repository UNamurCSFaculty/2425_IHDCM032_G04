package be.labil.anacarde.presentation.controller.enums;

/**
 * Enumération des types d’utilisateurs reconnus par l’API.
 * <p>
 * Utilisée pour paramétrer la désérialisation polymorphique et la documentation OpenAPI.
 */
public enum UserType {
	admin,
	producer,
	transformer,
	quality_inspector,
	exporter,
	carrier,
	trader
}
