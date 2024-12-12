package ru.mtuci.rbpo_practice.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String email;
    private String token;
}
