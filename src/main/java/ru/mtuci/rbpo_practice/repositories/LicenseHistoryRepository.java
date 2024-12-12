package ru.mtuci.rbpo_practice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_practice.models.LicenseHistory;
import java.util.Optional;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
    Optional<LicenseHistory> findById(Long id);
}
