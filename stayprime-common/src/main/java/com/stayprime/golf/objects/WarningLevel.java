package com.stayprime.golf.objects;

public enum WarningLevel {
    OFF(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    public final int id;

    private WarningLevel(int id) {
	this.id = id;
    }

    public static WarningLevel getLevel(int id) {
        switch (id) {
            case 0: return OFF;
            case 1: return LOW;
            case 2: return MEDIUM;
            case 3: return HIGH;
            default: return null;
        }
    }
}
