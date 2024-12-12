package ru.mtuci.rbpo_practice.services;

import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_practice.models.Device;
import ru.mtuci.rbpo_practice.models.ApplicationUser;
import ru.mtuci.rbpo_practice.repositories.DeviceRepository;

import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<Device> getDeviceByIdAndUser(ApplicationUser user, Long id) {
        return deviceRepository.findByIdAndUser(id, user);
    }

    public Optional<Device> getDeviceByInfo(String mac_address, ApplicationUser user,  String name) {
        return deviceRepository.findByUserAndMacAddressAndName(user, mac_address, name);
    }

    public void removeLastDevice(ApplicationUser user) {
        Optional<Device> lastDevice = deviceRepository.findTopByUserOrderByIdDesc(user);
        lastDevice.ifPresent(deviceRepository::delete);
    }

    public Device replaceDevice(String name, String mac, ApplicationUser user, Long deviceId){
        Optional<Device> oldDevice = getDeviceByIdAndUser(user, deviceId);
        Device newDevice = new Device();
        if (oldDevice.isPresent()) {
            newDevice = oldDevice.get();
        }

        newDevice.setName(name);
        newDevice.setMacAddress(mac);
        newDevice.setUser(user);

        deviceRepository.save(newDevice);
        return newDevice;
    }
}
