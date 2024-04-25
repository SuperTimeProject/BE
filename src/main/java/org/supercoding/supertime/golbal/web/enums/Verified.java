package org.supercoding.supertime.golbal.web.enums;

public enum Verified {
    PENDING("PENDING"),
    DENIED("DENIED"),
    COMPLETED("COMPLETED"),
    NEEDED("NEEDED"),
    ADMIN("ADMIN");

    private final String type;

    Verified(String type) {this.type = type;}

    public String getType() {return type;}
}
