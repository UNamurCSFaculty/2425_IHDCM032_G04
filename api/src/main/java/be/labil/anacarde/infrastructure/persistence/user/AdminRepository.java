package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends GenericUserRepository<Admin> {

}
