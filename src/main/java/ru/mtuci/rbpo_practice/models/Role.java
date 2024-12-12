package ru.mtuci.rbpo_practice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Role {
    USER(Set.of(Permission.READ)),
    ADMIN(Set.of(Permission.READ, Permission.MODIFICATION));

    private final Set<Permission> permissions;

    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + name()));
        return authorities;
    }
}
