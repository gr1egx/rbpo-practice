package ru.mtuci.rbpo_practice.models;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseTypeCreationRequest {
    private Long duration;
    private String name;
    private String description;
}
