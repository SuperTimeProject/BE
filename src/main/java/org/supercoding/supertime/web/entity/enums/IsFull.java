package org.supercoding.supertime.web.entity.enums;

public enum IsFull {
    FULL("FULL"),
    HALF("HALF");

    private final String type;

    IsFull(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
