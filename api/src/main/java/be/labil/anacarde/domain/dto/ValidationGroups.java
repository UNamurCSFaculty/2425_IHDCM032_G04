package be.labil.anacarde.domain.dto;

/**
 * Cette interface déclare différents groupes de validation utilisés pour différencier les règles de validation lors des
 * opérations sur les utilisateurs. Elle inclut le groupe Create pour la création d'un utilisateur et le groupe Update
 * pour la mise à jour d'un utilisateur.
 */
public interface ValidationGroups {

	/** Groupe de validation utilisé pour la création d'un nouvel utilisateur. */
	interface Create {
	}

	/** Groupe de validation utilisé pour la mise à jour d'un utilisateur existant. */
	interface Update {
	}
}
