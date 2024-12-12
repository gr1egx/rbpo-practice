package ru.mtuci.rbpo_practice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.rbpo_practice.models.LicenseTypeCreationRequest;
import ru.mtuci.rbpo_practice.models.LicenseTypeUpdateRequest;
import ru.mtuci.rbpo_practice.services.LicenseTypeService;

import java.util.Objects;

@RestController
@RequestMapping("/api/type")
@RequiredArgsConstructor
public class LicenseTypeController {

    private final LicenseTypeService licenseTypeService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<String> createLicenseType(@RequestBody LicenseTypeCreationRequest request) {
        try {
            Long licenseTypeId = licenseTypeService.createLicenseType(request.getName(), request.getDuration(), request.getDescription());

            return ResponseEntity.status(HttpStatus.OK).body("License type created successfully.\nID: " + licenseTypeId);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<String> updateLicenseType(@RequestBody LicenseTypeUpdateRequest request) {
        try {

            String result = licenseTypeService.updateLicenseType(request.getId(), request.getName(), request.getDuration(),
                    request.getDescription());

            if (!Objects.equals(result, "OK")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }

            return ResponseEntity.status(HttpStatus.OK).body("License type has been updated successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }
}
