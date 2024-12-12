package ru.mtuci.rbpo_practice.models;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseCreateRequest {
    private Long licenseTypeId;
    private Long count;
    private Long productId;
    private Long ownerId;
}
