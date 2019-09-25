/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.localservice;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Benjamin
 */
public interface CartUnitCommunication {

    /*
     * Report unit and status
     */

    void reportDevice(String mac, Integer cartId, Map<String, String> deviceInfo);

    @Deprecated
    void reportUnit(String mac, Integer unitId);

    void reportSimInformation(String mac, String simIccid, String simIpAddress);

    public void reportSoftwareVersion(String mac, String version);

    public void reportFirmwareVersion(String mac, String version);

    /*
     * Request info and flags
     */

    long getCurrentTime();

    boolean stayConnected();

    /*
     * Check and sync assets
     */

    String getAssetVersion(String key);

    @Deprecated
    Date getAssetLastUpdated(String key);

    String getMD5Checksum(String filename);

    InputStream download(String filename);

    void reportAssetVersion(String clientId, String key, String version);

    @Deprecated
    void reportAssetUpdated(String macAddress, Date updated, String key);

    /*
     * Old methods to be removed later, can be empty implementations
     */

    @Deprecated
    public Date getCourseLastUpdated();

    @Deprecated
    public Date getPinLocationLastUpdated();

    @Deprecated
    public Date getAdsLastUpdated();

    @Deprecated
    public Date getMenuLastUpdated();

    @Deprecated
    public Date getTournamentLastUpdated();

    @Deprecated
    public void reportCourseUpdated(byte[] mac, Date parse);

    @Deprecated
    public void reportPinLocationUpdated(byte[] mac, Date updated);

    @Deprecated
    public void reportAdsUpdated(byte[] mac, Date lastUpdated);

    @Deprecated
    public void reportMenuUpdated(byte[] mac, Date lastUpdated);

}
