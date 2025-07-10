package com.sigmat.lms.models;

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
    INSTRUCTOR_DELETE("instructor:delete");

    private final String permission;

    public String getAuthority() {
        return this.permission;
    }
}