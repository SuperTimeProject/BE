package org.supercoding.supertime.web.entity.enums;

public enum Part {
    PART_FULL("PART_FULL"),
    PART_BE("PART_BE"),
    PART_FE("PART_FE");
    private final String type;

    Part(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
