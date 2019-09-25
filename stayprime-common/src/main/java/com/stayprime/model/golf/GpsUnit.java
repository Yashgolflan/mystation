package com.stayprime.model.golf;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.joda.time.DateTime;

//@Entity
public class GpsUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int localId;

    private long siteId;

    @ManyToOne
    private GolfCart cart;

    private String mode;

    // Position

    private Position position;

    private DateTime positionTimestamp;

    private int positionQuality;

    //WiFi

    private String wifiMac;

    private String wifiIp;

    private boolean wifiConnected;

    //Mobile

    private String simIccid;

    private String mobileIp;

    private boolean mobileConnected;

    //Hardware and software

    private String hardwareModel;

    private String serialNumber;

    private String firmwareVersion;

    private String softwareVersion;

    private String settingsVersion;

    private String assetsVersion;

    private String systemStatus;

    private float supplyVoltage;

    public GpsUnit() {
    }

    public long getId() {
        return id;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public GolfCart getCart() {
        return cart;
    }

    public void setCart(GolfCart cart) {
        this.cart = cart;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getPositionQuality() {
        return positionQuality;
    }

    public void setPositionQuality(int positionQuality) {
        this.positionQuality = positionQuality;
    }

    public DateTime getPositionTimestamp() {
        return positionTimestamp;
    }

    public void setPositionTimestamp(DateTime positionTimestamp) {
        this.positionTimestamp = positionTimestamp;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public void setWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
    }

    public String getWifiIp() {
        return wifiIp;
    }

    public void setWifiIp(String wifiIp) {
        this.wifiIp = wifiIp;
    }

    public boolean isWifiConnected() {
        return wifiConnected;
    }

    public void setWifiConnected(boolean wifiConnected) {
        this.wifiConnected = wifiConnected;
    }

    public String getSimIccid() {
        return simIccid;
    }

    public void setSimIccid(String simIccid) {
        this.simIccid = simIccid;
    }

    public String getMobileIp() {
        return mobileIp;
    }

    public void setMobileIp(String mobileIp) {
        this.mobileIp = mobileIp;
    }

    public boolean isMobileConnected() {
        return mobileConnected;
    }

    public void setMobileConnected(boolean mobileConnected) {
        this.mobileConnected = mobileConnected;
    }

    public String getHardwareModel() {
        return hardwareModel;
    }

    public void setHardwareModel(String hardwareModel) {
        this.hardwareModel = hardwareModel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getSettingsVersion() {
        return settingsVersion;
    }

    public void setSettingsVersion(String settingsVersion) {
        this.settingsVersion = settingsVersion;
    }

    public String getAssetsVersion() {
        return assetsVersion;
    }

    public void setAssetsVersion(String assetsVersion) {
        this.assetsVersion = assetsVersion;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }

    public float getSupplyVoltage() {
        return supplyVoltage;
    }

    public void setSupplyVoltage(float supplyVoltage) {
        this.supplyVoltage = supplyVoltage;
    }

}
