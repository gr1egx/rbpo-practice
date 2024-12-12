package ru.mtuci.rbpo_practice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.rbpo_practice.configuration.JwtProvider;
import ru.mtuci.rbpo_practice.models.*;
import ru.mtuci.rbpo_practice.services.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/license")
@RequiredArgsConstructor
public class LicenseController {

    private final ProductService productService;
    private final LicenseService licenseService;
    private final UserDetailsService userService;
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final LicenseTypeService licenseTypeService;
    private final DeviceService deviceService;

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@RequestBody ActivationRequest request, HttpServletRequest req) {
        try {
            String email = jwtProvider.getUsername(req.getHeader("Authorization").substring(7));
            ApplicationUser user = userDetailsService.getUserByEmail(email).get();
            Device device = deviceService.replaceDevice(request.getName(), request.getMac_address(), user, request.getDeviceId());

            Ticket ticket = licenseService.activateLicense(user, request.getActivationCode(), device);

            if (!ticket.getStatus().equals("OK")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ticket.getInfo());
            }

            return ResponseEntity.status(HttpStatus.OK).body(ticket);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<String> createLicense(@RequestBody LicenseCreateRequest request, HttpServletRequest req) {
        try {
            Long licenseTypeId = request.getLicenseTypeId();
            Long ownerId = request.getOwnerId();
            Long productId = request.getProductId();

            if (userService.getUserById(ownerId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Owner doesn't exist.");
            }

            if (licenseTypeService.getLicenseTypeById(licenseTypeId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("License type doesn't exist.");
            }

            if (productService.getProductById(productId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product doesn't exist.");
            }

            if (productService.getProductById(productId).get().isBlocked()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This product isn't available.");
            }

            String email = jwtProvider.getUsername(req.getHeader("Authorization").substring(7));
            ApplicationUser user = userDetailsService.getUserByEmail(email).get();

            Long id = licenseService.createLicense(ownerId, user, productId, licenseTypeId, request.getCount());

            return ResponseEntity.status(HttpStatus.OK).body("License created successfully.\nID: " + id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }

    @PostMapping("/info")
    public ResponseEntity<?> getLicenseInfo(@RequestBody LicenseInfoRequest request, HttpServletRequest req) {
        try {
            String email = jwtProvider.getUsername(req.getHeader("Authorization").substring(7));
            ApplicationUser user = userDetailsService.getUserByEmail(email).get();
            Optional<Device> device = deviceService.getDeviceByInfo(request.getMac_address(), user, request.getName());

            if (device.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Device not found.");
            }

            Ticket ticket = licenseService.getLicensesForDevice(device.get(), request.getActivationCode());

            if (!ticket.getStatus().equals("OK")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ticket.getInfo());
            }

            return ResponseEntity.status(HttpStatus.OK).body(ticket);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<String> createLicense(@RequestBody LicenseUpdateRequest request) {
        try {

            String result = licenseService.updateLicense(request.getId(), request.getOwnerId(), request.getTypeId(), request.getProductId(),
                    request.getIsBlocked(), request.getDescription(), request.getDeviceCount());

            if (!Objects.equals(result, "OK")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }

            return ResponseEntity.status(HttpStatus.OK).body("License has been updated successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }

    @PostMapping("/renewal")
    public ResponseEntity<?> renewalLicense(@RequestBody LicenseRenewalRequest request, HttpServletRequest req) {
        try {
            String email = jwtProvider.getUsername(req.getHeader("Authorization").substring(7));
            ApplicationUser user = userDetailsService.getUserByEmail(email).get();

            Ticket ticket = licenseService.renewalLicense(request.getActivationCode(), user);

            if (!ticket.getStatus().equals("OK")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ticket.getInfo());
            }

            return ResponseEntity.status(HttpStatus.OK).body(ticket);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }
}
