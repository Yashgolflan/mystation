/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.comm.gprs;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Omer
 */
public class GPRSEvent {

    private String startTime;
    private String endTime;
    private String connectionState;
    private String networkCode;
    private String networkName;
    private String signal;
    private boolean connected;
    
    
    public GPRSEvent() {
    }   

    public boolean isconnected() {
        return connected;
    }

    public void setConnected(boolean isconnected) {
        this.connected = isconnected;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(String connectionState) {
        this.connectionState = connectionState;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
