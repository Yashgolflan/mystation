/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.storage.domain;

import com.stayprime.hibernate.entities.CartUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author priyanshu
 */
public class ReportedUnit {

    private Integer unitId;
    private String mac;
    private Date time;
    private String ip;
    private String simIccid;
    private String simIpAddress;
    private String softwareVersion;
    private String firmwareVersion;

    private final Map<String, String> assetVersions;

    public ReportedUnit(String mac) {
        this(mac, null, null);
    }

    public ReportedUnit(String mac, String simIccid, String simIpAddress) {
        this.mac = mac;
        this.simIccid = simIccid;
        this.simIpAddress = simIpAddress;
        assetVersions = new HashMap<String, String>();
    }

    public String getSimIccid() {
        return simIccid;
    }

    public String getSimIpAddress() {
        return simIpAddress;
    }

    public void setSimIccid(String simIccid) {
        this.simIccid = simIccid;
    }

    public void setSimIpAddress(String simIpAddress) {
        this.simIpAddress = simIpAddress;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void putAssetVersion(String key, String version) {
        assetVersions.put(key, version);
    }

    public Set<String> getAssetKeys() {
        return assetVersions.keySet();
    }

    public String getAssetUpdated(String key) {
        return assetVersions.get(key);
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public void updateCartUnit(CartUnit unit) {
        updateCartUnit(unit, this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReportedUnit) {
            ReportedUnit reportedUnit = (ReportedUnit) obj;
            return ObjectUtils.equals(reportedUnit.mac, mac);
        }

        return false;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

    public static ReportedUnit findByMac(List<ReportedUnit> list, String mac) {
        if (list != null) {
            for (ReportedUnit ru : list) {
                if (ObjectUtils.equals(mac, ru.getMac())) {
                    return ru;
                }
            }
        }
        return null;
    }

    public static void updateCartUnit(CartUnit unit, ReportedUnit ru) {
        if (ru.getTime() != null) {
            Integer cn = ru.getUnitId();
            unit.setCartNumber((cn != null && cn == 0) ? null : cn);
            unit.setIpUpdated(ru.getTime());
            unit.setIpAddress(ru.getIp());
            //jpaService.reportUnit(ru.getUnitId(), ru.getMac(), ru.getIp());
        }
        if (StringUtils.isNotBlank(ru.getSimIccid())) {
            unit.setSimIccid(ru.getSimIccid());
            unit.setSimIp(ru.getSimIpAddress());
            //jpaService.reportSimInformation(ru.getMac(), ru.getSimIccid(), ru.getSimIpAddress());
        }
        if (StringUtils.isNotBlank(ru.getSoftwareVersion())) {
            unit.setSoftwareVersion(ru.getSoftwareVersion());
        }
        if (StringUtils.isNotBlank(ru.getFirmwareVersion())) {
            unit.setFirmwareVersion(ru.getFirmwareVersion());
        }
        for (String key : ru.getAssetKeys()) {
            long timestamp = NumberUtils.toLong(ru.getAssetUpdated(key));
            unit.setAssetUpdated(key, timestamp == 0? null : new Date(timestamp));
        }
    }

}
