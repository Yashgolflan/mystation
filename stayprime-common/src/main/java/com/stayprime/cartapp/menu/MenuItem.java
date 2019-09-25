/*
 *
 */
package com.stayprime.cartapp.menu;

/**
 *
 * @author benjamin
 */
public class MenuItem {
    private int id;
    private String name;
    private String description;
    private String icon;
    private String image;
    private float price;

    public MenuItem() {
    }

    public MenuItem(int id, String name, String description, String icon, float price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return name;
    }

    public void setCode(String name) {
        this.name = name;
    }

    public String getName() {
        if (description != null && description.contains(";")) {
            int i = description.indexOf(";");
            String name = description.substring(0,i);
            return name;
        }
        //This description is actually the name itself.
        return description;
    }

    public String getDescription() {
        if (description != null && description.contains(";")) {
            int i = description.indexOf(";");
            String details = description.substring(i+1, description.length());
            return details;
        }
        //This description is actually the name itself.
        return null;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}