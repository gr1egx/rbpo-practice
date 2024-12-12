package ru.mtuci.rbpo_practice.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "license_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LicenseType {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Long defaultDuration;

    private String description;
}

