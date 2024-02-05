package org.supercoding.supertime.web.entity.enums;

public enum Valified {
    PENDING("PENDING"),
    DENIED("DENIED"),
    COMPLETED("COMPLETED"),
    NEEDED("NEEDED");

    private final String type;

    Valified(String type) {this.type = type;}

    public String getType() {return type;}
}
