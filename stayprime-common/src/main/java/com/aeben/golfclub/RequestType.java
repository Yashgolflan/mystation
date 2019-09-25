/*
 * 
 */
package com.aeben.golfclub;

/**
 *
 * @author benjamin
 */
public enum RequestType {
    RANGER(1), EMERGENCY(2), AMBULANCE(3), FNB(4);
    public final int type;

    private RequestType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static RequestType getType(int type) {
        switch (type) {
            case 1:
                return RANGER;
            case 2:
                return EMERGENCY;
            case 3:
                return AMBULANCE;
            case 4:
                return FNB;
            default:
                throw new IllegalArgumentException("Invalid service type id: " + type);
        }
    }
    
    public static final int REQUEST_CREATED = -1;
    public static final int REQUEST_PLACED = 0;
    public static final int REQUEST_RECEIVED = 1;
    public static final int REQUEST_SERVICED = 2;
    public static final int REQUEST_DENIED = 3;
    public static final int REQUEST_DISMISSED = 4;
}
