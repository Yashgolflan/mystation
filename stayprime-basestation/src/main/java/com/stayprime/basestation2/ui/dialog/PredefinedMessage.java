/*
 * 
 */
package com.stayprime.basestation2.ui.dialog;

/**
 *
 * @author benjamin
 */
public class PredefinedMessage {
    private int code;
    private int unrequireAck;
    private int statusMask;
    private boolean statusEnable;
    private String name;

    public PredefinedMessage(int code, int unrequireAck,
            int statusMask, boolean statusEnable, String name) {
        this.code = code;
        this.unrequireAck = unrequireAck;
        this.statusMask = statusMask;
        this.statusEnable = statusEnable;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getUnrequireCode() {
        return unrequireAck;
    }

    public int getStatusMask() {
        return statusMask;
    }

    public boolean getStatusEnable() {
        return statusEnable;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
