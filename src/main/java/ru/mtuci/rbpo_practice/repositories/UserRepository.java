package ru.mtuci.rbpo_practice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbpo_practice.models.ApplicationUser;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findById(Long id);
    Optional<ApplicationUser> findByEmail(String email);
}
