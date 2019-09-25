package com.stayprime.basestation2.util;

import org.jdesktop.beans.AbstractBean;

/**
 *
 * @author Omer
 */
public class NameValuePair extends AbstractBean implements Cloneable {

    private String name;
    private String value;

    public NameValuePair() {
    }

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        String old = getName();
        this.name = name;
        firePropertyChange("name", old, name);
    }

    public final String getName() {
        return name;
    }

    public void setValue(String value) {
        String old = getValue();
        this.value = value;
        firePropertyChange("value", old, value);
    }

    public final String getValue() {
        return value;
    }

    @Override
    public NameValuePair clone() {
        return new NameValuePair(name, value);
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
