package com.sigmat.lms.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Role {
    LEARNER(Set.of()), 
    ADMIN(Set.of(
            Permission.ADMIN_READ,
            Permission.ADMIN_CREATE,
            Permission.ADMIN_UPDATE,
            Permission.ADMIN_DELETE,
            Permission.MANAGER_READ,
            Permission.MANAGER_CREATE,
            Permission.MANAGER_UPDATE,
            Permission.MANAGER_DELETE
    )),
    MANAGER(Set.of(
            Permission.MANAGER_READ,
            Permission.MANAGER_CREATE,
            Permission.MANAGER_UPDATE,
            Permission.MANAGER_DELETE
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