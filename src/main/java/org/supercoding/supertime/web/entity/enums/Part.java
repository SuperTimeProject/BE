package org.supercoding.supertime.web.entity.enums;

public enum Part {
    PART_UNDEFINED("미정"),
    PART_FULL("풀타임"),
    PART_BE("백엔드"),
    PART_FE("프론트엔드");

    private final String type;

    Part(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
