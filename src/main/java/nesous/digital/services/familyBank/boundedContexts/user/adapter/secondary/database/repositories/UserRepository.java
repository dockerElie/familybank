package nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.repositories;

import nesous.digital.services.familyBank.boundedContexts.user.adapter.secondary.database.entities.UserEntity;
import nesous.digital.services.familyBank.boundedContexts.user.hexagon.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByIdentifier(String identifier);

    UserEntity findByPrivilege(Privilege privilege);
}
