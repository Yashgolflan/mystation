/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.demo;

/**
 *
 * @author Omer
 */
public interface DemoUnitCommunication {

    
    public void reportUnit(Integer unitId, byte[] macAddress);

    public long getCurrentTime();

    public boolean stayConnected();

    public String sendLocation(int cartNumber, double latitude, double longitude, float angle,
            int course, int hole, int setStatus, int clearStatus, Integer paceSecondsLeft, int requestData);
    
    public int getCartNumber(String macAddress);
}
