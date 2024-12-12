package ru.mtuci.rbpo_practice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ticket {
    private Long userId;
    private Long deviceId;

    private String signature;
    private String info;
    private String status;

    private Date currentDate;
    private Date lifetime;
    private Date activationDate;
    private Date expirationDate;

    private boolean licenseBlocked;
}
