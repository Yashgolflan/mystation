/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.device;

import com.aeben.golfcourse.cart.ObservablePositionInfo;

/**
 *
 * @author benjamin
 */
public interface UnitInfo {
    //Unit identification info
    public Integer getUnitId();
    public String getMacAddress();

    //Unit hardware info
//    public NetInfo getNetInfo();
    public ObservablePositionInfo getPositionInfo();

    //Should these become objects? Maybe...
    public String getFirmwareVersion();
    public String getSoftwareVersion();

    //Observer pattern
    public void addUnitObserver(Observer observer);
    public void removeUnitObserver(Observer observer);

    public interface Observer {
	public void unitIdChanged(UnitInfo ui);
	public void firmwareVersionChanged(UnitInfo ui);
	public void softwareVersionChanged(UnitInfo ui);
	public void positioningSystemChanged(UnitInfo ui);
    }
}
