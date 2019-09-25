/*
 * 
 */
package com.stayprime.hibernate.entities;

import com.stayprime.util.ConfigUtil;
import java.util.Properties;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author benjamin
 */
@Embeddable
public class PropertiesColumn {

    private Properties properties;

    public PropertiesColumn() {
        properties = new Properties();
    }

    @Column
    @Access(AccessType.PROPERTY)
    public String getProperties() {
        return ConfigUtil.getPropertiesString(properties);
    }

    public void setProperties(String properties) {
        this.properties = ConfigUtil.loadPropertiesString(properties);
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void put(String key, String value) {
        properties.put(key, value);
    }

    public void clear(String key) {
        properties.remove(key);
    }

}
