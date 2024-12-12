package ru.mtuci.rbpo_practice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_practice.models.License;
import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findById(Long id);
    Optional<License> findByCode(String code);
    Optional<License> findTopByOrderByIdDesc();
    Optional<License> findByIdInAndCode(List<Long> ids, String code);
}
