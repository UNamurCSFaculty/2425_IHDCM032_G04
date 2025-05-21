package be.labil.anacarde.infrastructure.security;

import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("docSecurity")
@RequiredArgsConstructor
public class DocSecurity {

	private final DocumentRepository docRepo;

	/**
	 * @return true si le document appartient à l’utilisateur.
	 */
	public boolean isOwner(Integer docId, Integer userId) {
		return docRepo.existsByIdAndUserId(docId, userId);
	}
}