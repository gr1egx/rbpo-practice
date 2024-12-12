package ru.mtuci.rbpo_practice.services;

import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_practice.models.LicenseType;
import ru.mtuci.rbpo_practice.repositories.LicenseTypeRepository;

import java.util.Optional;

@Service
public class LicenseTypeService {
    private final LicenseTypeRepository licenseTypeRepository;

    public LicenseTypeService(LicenseTypeRepository licenseTypeRepository) {
        this.licenseTypeRepository = licenseTypeRepository;
    }

    public Optional<LicenseType> getLicenseTypeById(Long id) {
        return licenseTypeRepository.findById(id);
    }

    public String updateLicenseType(Long id, String name, Long duration, String description) {
        Optional<LicenseType> licenseType = getLicenseTypeById(id);
        if (licenseType.isEmpty()) {
            return "License type not found.";
        }

        LicenseType type = licenseType.get();
        type.setName(name);
        type.setDescription(description);
        type.setDefaultDuration(duration);
        licenseTypeRepository.save(type);
        return "OK";
    }

    public Long createLicenseType(String name, Long duration, String description) {
        LicenseType licenseType = new LicenseType();
        licenseType.setDefaultDuration(duration);
        licenseType.setDescription(description);
        licenseType.setName(name);
        licenseTypeRepository.save(licenseType);
        return licenseTypeRepository.findTopByOrderByIdDesc().get().getId();
    }
}