package ru.mtuci.rbpo_practice.models;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseUpdateRequest {
    private Long id;
    private Long ownerId;
    private Long productId;
    private Long typeId;
    private Long deviceCount;
    private String description;
    private Boolean isBlocked;
}
