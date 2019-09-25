package com.aeben.golfcourse.elements;

public enum WarningLevel {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    public final int id;

    private WarningLevel(int id) {
	this.id = id;
    }
}
