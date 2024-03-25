package org.supercoding.supertime.golbal.web.enums;

public enum InquiryClosed {
    OPEN("OPEN"),
    CLOSED("CLOSED");

    private final String type;

    InquiryClosed(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
