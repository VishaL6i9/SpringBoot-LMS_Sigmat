package com.flux.lms.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.flux.lms.models.Permission.*;

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
    )),
    INSTITUTION(Set.of(
            INSTITUTION_READ,
            INSTITUTION_CREATE,
            INSTITUTION_UPDATE,
            INSTITUTION_DELETE,
            ADMIN_READ,
            ADMIN_CREATE,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            INSTRUCTOR_READ,
            INSTRUCTOR_CREATE,
            INSTRUCTOR_UPDATE,
            INSTRUCTOR_DELETE
    )),
    SUPER_ADMIN(Set.of(
            ADMIN_READ,
            ADMIN_CREATE,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            INSTRUCTOR_READ,
            INSTRUCTOR_CREATE,
            INSTRUCTOR_UPDATE,
            INSTRUCTOR_DELETE,
            INSTITUTION_READ,
            INSTITUTION_CREATE,
            INSTITUTION_UPDATE,
            INSTITUTION_DELETE,
            SUPER_ADMIN_READ,
            SUPER_ADMIN_CREATE,
            SUPER_ADMIN_UPDATE,
            SUPER_ADMIN_DELETE,
            SUPER_ADMIN_OWNER
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

    public boolean isSuperAdmin() {
        return this == SUPER_ADMIN;
    }

    public boolean hasOwnerAccess() {
        return this == SUPER_ADMIN && permissions.contains(SUPER_ADMIN_OWNER);
    }
}