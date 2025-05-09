package be.labil.anacarde.domain.dto.db;

/**
 * Cette interface déclare différents groupes de validation utilisés pour différencier les règles de
 * validation lors des opérations sur les utilisateurs. Elle inclut le groupe Create pour la
 * création d'un utilisateur et le groupe Update pour la mise à jour d'un utilisateur.
 */
public interface ValidationGroups {

	/** Groupe de validation utilisé pour la création d'un nouvel utilisateur. */
	interface Create {
	}

	/** Groupe de validation utilisé pour la mise à jour d'un utilisateur existant. */
	interface Update {
	}

	/** Groupe de validation utilisé pour la lecture des informations d'un utilisateur. */
	interface Read {
	}

	/** Groupe de validation utilisé pour la suppression d'un utilisateur. */
	interface Delete {
	}
}
