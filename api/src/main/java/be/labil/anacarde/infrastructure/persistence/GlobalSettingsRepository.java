package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Integer> {
}