package ru.mtuci.rbpo_practice.models;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    private String name;
    private Boolean isBlocked;
}
