package com.sigmat.lms.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sigmat.lms.models.Permission.*;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER(Set.of()), 
    ADMIN(Set.of(
            ADMIN_READ,
            ADMIN_CREATE,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            INSTRUCTOR_READ,
            INSTRUCTOR_CREATE,
            INSTRUCTOR_UPDATE,
            INSTRUCTOR_DELETE
    )),
    INSTRUCTOR(Set.of(
            INSTRUCTOR_READ,
            INSTRUCTOR_CREATE,
            INSTRUCTOR_UPDATE,
            INSTRUCTOR_DELETE
    ));

    private final Set<Permission> permissions;

    public Collection<GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getAuthority()))
                .collect(Collectors.toList());
    }

    public String getRoleName() {
        return "ROLE_" + this.name();
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}