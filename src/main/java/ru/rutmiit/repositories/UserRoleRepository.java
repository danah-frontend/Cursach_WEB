package ru.rutmiit.repositories;

import ru.rutmiit.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rutmiit.models.enums.UserRoles;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRoles name);
}