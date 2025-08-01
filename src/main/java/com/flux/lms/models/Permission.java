package com.flux.lms.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_CREATE("admin:create"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),
    INSTRUCTOR_READ("instructor:read"),
    INSTRUCTOR_CREATE("instructor:create"),
    INSTRUCTOR_UPDATE("instructor:update"),
    INSTRUCTOR_DELETE("instructor:delete"),
    INSTITUTION_READ("institution:read"),
    INSTITUTION_CREATE("institution:create"),
    INSTITUTION_UPDATE("institution:update"),
    INSTITUTION_DELETE("institution:delete"),
    SUPER_ADMIN_READ("super_admin:read"),
    SUPER_ADMIN_CREATE("super_admin:create"),
    SUPER_ADMIN_UPDATE("super_admin:update"),
    SUPER_ADMIN_DELETE("super_admin:delete"),
    SUPER_ADMIN_OWNER("super_admin:owner");

    private final String permission;

    public String getAuthority() {
        return this.permission;
    }
}