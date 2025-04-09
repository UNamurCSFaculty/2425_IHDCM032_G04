package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
}
