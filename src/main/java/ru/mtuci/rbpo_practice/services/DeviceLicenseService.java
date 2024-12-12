package ru.mtuci.rbpo_practice.services;

import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_practice.models.Device;
import ru.mtuci.rbpo_practice.models.DeviceLicense;
import ru.mtuci.rbpo_practice.models.License;
import ru.mtuci.rbpo_practice.repositories.DeviceLicenseRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class DeviceLicenseService {
    private final DeviceLicenseRepository deviceLicenseRepository;

    public DeviceLicenseService(DeviceLicenseRepository deviceLicenseRepository) {
        this.deviceLicenseRepository = deviceLicenseRepository;
    }

    public Optional<DeviceLicense> getDeviceById(Long id) {
        return deviceLicenseRepository.findById(id);
    }

    public List<DeviceLicense> getLicensesByDeviceId(Device device) {
        return deviceLicenseRepository.findByDeviceId(device.getId());
    }

    public Long getDevicesForLicense(Long licenseId) {
        return deviceLicenseRepository.countByLicenseId(licenseId);
    }

    public void createDeviceLicense(Device device, License license) {
        DeviceLicense newLicense = new DeviceLicense();
        newLicense.setActivationDate(new Date());
        newLicense.setDevice(device);
        newLicense.setLicense(license);
        deviceLicenseRepository.save(newLicense);
    }
}
