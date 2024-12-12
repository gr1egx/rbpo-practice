package ru.mtuci.rbpo_practice.models;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationRequest {
    private Long deviceId;
    private String activationCode;
    private String mac_address;
    private String name;
}
