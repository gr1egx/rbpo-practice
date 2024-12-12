package ru.mtuci.rbpo_practice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_practice.models.LicenseType;
import java.util.Optional;

public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
    Optional<LicenseType> findById(Long id);
    Optional<LicenseType> findTopByOrderByIdDesc();
}