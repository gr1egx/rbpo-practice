package ru.mtuci.rbpo_practice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_practice.models.Device;
import ru.mtuci.rbpo_practice.models.ApplicationUser;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findById(Long id);
    Optional<Device> findByUserAndMacAddressAndName(ApplicationUser user, String mac_address, String name);
    Optional<Device> findTopByUserOrderByIdDesc(ApplicationUser user);
    Optional<Device> findByIdAndUser(Long id, ApplicationUser user);
}
