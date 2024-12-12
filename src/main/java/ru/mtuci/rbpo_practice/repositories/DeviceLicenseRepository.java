package ru.mtuci.rbpo_practice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_practice.models.DeviceLicense;
import java.util.List;
import java.util.Optional;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    Optional<DeviceLicense> findById(Long id);
    Long countByLicenseId(Long licenseId);
    List<DeviceLicense> findByDeviceId(Long deviceId);
}