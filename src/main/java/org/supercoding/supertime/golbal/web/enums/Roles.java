package org.supercoding.supertime.golbal.web.enums;

public enum Roles {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");
    private final String type;

    Roles(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
