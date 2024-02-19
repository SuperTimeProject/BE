package org.supercoding.supertime.web.entity.enums;

public enum IsOauth {
    OAUTH("OAUTH"),
    NORMAL("NORMAL");

    private final String type;

    IsOauth(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}



