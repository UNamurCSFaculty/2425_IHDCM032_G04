package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
}
