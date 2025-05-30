package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Cooperative;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CooperativeRepository extends JpaRepository<Cooperative, Integer> {
	@Modifying
	@Transactional
	@Query(value = """
			  	UPDATE cooperative
			    SET creation_date = :newDate
			   	WHERE id = :id
			""", nativeQuery = true)
	void overrideCreationDateNative(@Param("id") Integer id,
			@Param("newDate") LocalDateTime newDate);
}
