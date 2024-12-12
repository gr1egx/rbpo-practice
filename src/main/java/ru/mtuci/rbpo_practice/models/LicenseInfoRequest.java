package ru.mtuci.rbpo_practice.models;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseInfoRequest {
    private String name;
    private String activationCode;
    private String mac_address;
}
