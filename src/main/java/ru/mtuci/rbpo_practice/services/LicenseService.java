package ru.mtuci.rbpo_practice.services;

import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_practice.models.*;
import ru.mtuci.rbpo_practice.repositories.LicenseRepository;
import java.util.*;
import java.security.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final LicenseHistoryService licenseHistoryService;
    private final UserDetailsService userDetailsService;
    private final LicenseTypeService licenseTypeService;
    private final DeviceLicenseService deviceLicenseService;
    private final ProductService productService;
    private final DeviceService deviceService;

    public LicenseService(LicenseRepository licenseRepository, ProductService productService, LicenseTypeService licenseTypeService, DeviceService deviceService,
                          DeviceLicenseService deviceLicenseService,
                          LicenseHistoryService licenseHistoryService, UserDetailsService userDetailsService) {
        this.licenseRepository = licenseRepository;
        this.productService = productService;
        this.deviceService = deviceService;
        this.licenseHistoryService = licenseHistoryService;
        this.userDetailsService = userDetailsService;
        this.deviceLicenseService = deviceLicenseService;
        this.licenseTypeService = licenseTypeService;
    }

    public Optional<License> getLicenseById(Long id) {
        return licenseRepository.findById(id);
    }

    public Ticket getLicensesForDevice(Device device, String code) {
        Ticket ticket = new Ticket();

        List<DeviceLicense> devices = deviceLicenseService.getLicensesByDeviceId(device);
        List<Long> licenseIds = devices.stream()
                .map(license -> license.getLicense() != null ? license.getLicense().getId() : null)
                .collect(Collectors.toList());
        Optional<License> applicationLicense = licenseRepository.findByIdInAndCode(licenseIds, code);

        if (applicationLicense.isEmpty()){
            ticket.setStatus("Error");
            ticket.setInfo("License not found.");
            return ticket;
        }

        ticket = makeTicket(applicationLicense.get().getUser(), applicationLicense.get(), device,
                "License info", "OK");

        return ticket;
    }

    public Long createLicense(Long ownerId, ApplicationUser user, Long productId, Long licenseTypeId, Long count) {
        Product product = productService.getProductById(productId).get();
        LicenseType type = licenseTypeService.getLicenseTypeById(licenseTypeId).get();

        License license = new License();

        String code = String.valueOf(UUID.randomUUID());
        int maxAttempts = 2000;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (!licenseRepository.findByCode(code).isPresent()) {
                break;
            }
            code = String.valueOf(UUID.randomUUID());
        }

        license.setOwnerId(userDetailsService.getUserById(ownerId).get());
        license.setLicenseType(type);
        license.setCode(code);
        license.setDeviceCount(count);
        license.setProduct(product);
        license.setDuration(type.getDefaultDuration());
        license.setBlocked(product.isBlocked());
        license.setDescription(type.getDescription());

        licenseRepository.save(license);

        licenseHistoryService.makeNewRecord("Not activated", "New license was created", licenseRepository.findTopByOrderByIdDesc().get(), user
        );

        return licenseRepository.findTopByOrderByIdDesc().get().getId();
    }

    public String updateLicense(Long id, Long ownerId, Long typeId, Long productId, Boolean isBlocked,
                                String description, Long deviceCount){
        Optional<License> license = getLicenseById(id);
        if (license.isEmpty()) {
            return "license not found.";
        }

        if (productService.getProductById(productId).isEmpty()){
            return "Product not found.";
        }

        if (licenseTypeService.getLicenseTypeById(typeId).isEmpty()){
            return "License type not found.";
        }

        License licenseObj = license.get();

        String code = String.valueOf(UUID.randomUUID());
        int maxAttempts = 2000;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (!licenseRepository.findByCode(code).isPresent()) {
                break;
            }
            code = String.valueOf(UUID.randomUUID());
        }
        licenseObj.setCode(code);

        licenseObj.setOwnerId(userDetailsService.getUserById(ownerId).get());
        licenseObj.setDuration(licenseTypeService.getLicenseTypeById(typeId).get().getDefaultDuration());
        licenseObj.setProduct(productService.getProductById(productId).get());
        licenseObj.setLicenseType(licenseTypeService.getLicenseTypeById(typeId).get());
        licenseObj.setDescription(description);
        licenseObj.setBlocked(isBlocked);
        licenseObj.setDeviceCount(deviceCount);

        licenseRepository.save(licenseObj);

        return "OK";
    }

    private String generateSignature(Ticket ticket) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            ObjectMapper mapper = new ObjectMapper();
            String ticketJson = mapper.writeValueAsString(ticket);

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privateKey);
            sig.update(ticketJson.getBytes());
            return Base64.getEncoder().encodeToString(sig.sign());
        } catch (Exception e) {
            return "Signature isn't valid.";
        }
    }


    public Ticket makeTicket(ApplicationUser user, License license, Device device, String info, String status) {
        Ticket ticket = new Ticket();
        ticket.setCurrentDate(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1);
        ticket.setLifetime(calendar.getTime());

        if (device != null){
            ticket.setDeviceId(device.getId());
        }

        if (user != null){
            ticket.setUserId(user.getId());
        }

        if (license != null){
            ticket.setActivationDate(license.getFirstActivationDate());
            ticket.setExpirationDate(license.getEndingDate());
            ticket.setLicenseBlocked(license.isBlocked());
        }

        ticket.setStatus(status);
        ticket.setInfo(info);
        ticket.setSignature(generateSignature(ticket));
        return ticket;
    }

    public Ticket activateLicense(ApplicationUser user, String code, Device device) {
        Ticket ticket = new Ticket();
        Optional<License> license = licenseRepository.findByCode(code);
        if (license.isEmpty()) {
            ticket.setInfo("License was not found.");
            ticket.setStatus("Error");
            deviceService.removeLastDevice(user);
            return ticket;
        }

        License licenseObj = license.get();
        if (licenseObj.isBlocked() ||
                (licenseObj.getEndingDate() != null && new Date().after(licenseObj.getEndingDate())) ||
                (licenseObj.getUser() != null && !Objects.equals(licenseObj.getUser().getId(), user.getId())) ||
                deviceLicenseService.getDevicesForLicense(licenseObj.getId()) >= licenseObj.getDeviceCount()) {
            ticket.setStatus("Error");
            ticket.setInfo("Activation is not possible.");
            deviceService.removeLastDevice(user);
            return ticket;
        }

        if (licenseObj.getFirstActivationDate() == null){
            licenseObj.setUser(user);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, Math.toIntExact(licenseObj.getDuration()));
            licenseObj.setEndingDate(calendar.getTime());
            licenseObj.setFirstActivationDate(new Date());
        }

        deviceLicenseService.createDeviceLicense(device, licenseObj);
        licenseRepository.save(licenseObj);
        licenseHistoryService.makeNewRecord("Activated", "Active license", licenseObj, user
        );

        ticket = makeTicket(user, licenseObj, device, "Successful activation.", "OK");

        return ticket;
    }

    public Ticket renewalLicense(String code, ApplicationUser user) {
        Ticket ticket = new Ticket();
        Optional<License> license = licenseRepository.findByCode(code);
        if (license.isEmpty()) {
            ticket.setInfo("The license key isn't valid");
            ticket.setStatus("Error");
            return ticket;
        }
        License licenseObj = license.get();
        if (licenseObj.isBlocked() ||
                (licenseObj.getEndingDate() != null && new Date().after(licenseObj.getEndingDate())) ||
                (licenseObj.getOwnerId() != null && !Objects.equals(licenseObj.getOwnerId().getId(), user.getId())) ||
                licenseObj.getFirstActivationDate() == null) {
            ticket.setStatus("Error");
            ticket.setInfo("Error during license renewal");
            return ticket;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(licenseObj.getEndingDate());
        calendar.add(Calendar.DAY_OF_MONTH, Math.toIntExact(licenseObj.getDuration()));
        licenseObj.setEndingDate(calendar.getTime());
        licenseRepository.save(licenseObj);
        licenseHistoryService.makeNewRecord("Renewal", "Active license", licenseObj, user);

        ticket = makeTicket(user, licenseObj, null, "The license has been successfully renewed", "OK");

        return ticket;
    }
}
