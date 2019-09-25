/*
 * 
 */
package com.stayprime.tournament.model;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author benjamin
 */
//@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"roundId", "extId"}))
@XmlAccessorType(XmlAccessType.FIELD)
public class Player {

    @Id @GeneratedValue
    @XmlAttribute
    private int id;

    //If the player has been removed, we can indicate active=false
    @XmlAttribute
    private boolean active = true;

    @XmlAttribute
    private String extId;

    @XmlElement
    private String name;

    @XmlAttribute
    private Integer cart;

    public Player() {
        this(0, StringUtils.EMPTY);
    }

    public Player(int id, String name) {
        this(id, null, name);
    }

    public Player(String extId, String name) {
        this(0, extId, name);
    }

    public Player(int id, String extId, String name) {
        this.id = id;
        this.name = name;
        this.extId = extId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCart() {
        return cart;
    }

    public void setCart(Integer cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return id + "-" + extId + " " + name + " " + cart;
    }

    public boolean isSamePlayer(Player externalPlayer) {
        return externalPlayer != null && ObjectUtils.equals(extId, externalPlayer.getExtId());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player)obj).hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.id;
        hash = 79 * hash + (this.active ? 1 : 0);
        hash = 79 * hash + (this.extId != null ? this.extId.hashCode() : 0);
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + (this.cart != null ? this.cart.hashCode() : 0);
        return hash;
    }

    public static Player findByExtId(Collection<Player> players, String extId) {
        if (players != null) {
            for (Player p : players) {
                if (p != null && ObjectUtils.equals(p.getExtId(), extId)) {
                    return p;
                }
            }
        }
        return null;
    }

    public static int indexOfExtId(List<Player> players, String extId) {
        if (players != null) {
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                if (p != null && ObjectUtils.equals(p.getExtId(), extId)) {
                    return i;
                }
            }
        }
        return -1;
    }

}
