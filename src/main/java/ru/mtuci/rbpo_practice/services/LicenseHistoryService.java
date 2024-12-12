package ru.mtuci.rbpo_practice.services;

import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_practice.models.License;
import ru.mtuci.rbpo_practice.models.LicenseHistory;
import ru.mtuci.rbpo_practice.models.ApplicationUser;
import ru.mtuci.rbpo_practice.repositories.LicenseRepository;
import ru.mtuci.rbpo_practice.repositories.LicenseHistoryRepository;

import java.util.Date;
import java.util.Optional;

@Service
public class LicenseHistoryService {
    private final LicenseHistoryRepository licenseHistoryRepository;
    private final LicenseRepository licenseRepository;

    public LicenseHistoryService(LicenseRepository licenseRepository, LicenseHistoryRepository licenseHistoryRepository) {
        this.licenseRepository = licenseRepository;
        this.licenseHistoryRepository = licenseHistoryRepository;
    }

    public Optional<LicenseHistory> getHistoryById(Long id) {
        return licenseHistoryRepository.findById(id);
    }

    public void makeNewRecord(String status, String description, License license, ApplicationUser user){
        LicenseHistory record = new LicenseHistory();
        record.setLicense(license);
        record.setUser(user);
        record.setChangeDate(new Date());
        record.setStatus(status);
        record.setDescription(description);

        licenseHistoryRepository.save(record);
    }
}
