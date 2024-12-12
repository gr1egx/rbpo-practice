package ru.mtuci.rbpo_practice.models;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseTypeUpdateRequest {
    private String name;
    private Long id;
    private Long duration;
    private String description;
}
