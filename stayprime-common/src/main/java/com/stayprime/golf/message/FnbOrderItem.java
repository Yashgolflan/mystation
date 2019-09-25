/*
 *
 */
package com.stayprime.golf.message;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Representation of an order menu item. It keeps the order quantity, name,
 * description and price of the item.
 */
public class FnbOrderItem {

    private int quantity;
    private String name;
    private String code;

    private String description;
    private float price;
    private String icon;
    private String picture;

    public FnbOrderItem(int quantity, String code, String name, String description, float price) {
        this.quantity = quantity;
        this.name = name;
        this.code = code;
        this.description = description;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public FnbOrderItem clone() {
        return new FnbOrderItem(quantity, code, name, description, price);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.quantity;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 67 * hash + Float.floatToIntBits(this.price);
        return hash;
    }

}
