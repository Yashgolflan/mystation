/*
 * 
 */
package com.stayprime.hibernate.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
@Entity
public class CartAssignment {

    @Id
    private int cartNumber;

    private Date startTime;

    private int validity;

    private Date updated;

    private Date unitUpdated;

    @Transient
    private List<String> players;

    public CartAssignment() {
        this(0);
    }

    public CartAssignment(int cartNumber) {
        this.cartNumber = cartNumber;
        players = new ArrayList<>();
    }

    public int getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(int cartNumber) {
        this.cartNumber = cartNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getUnitUpdated() {
        return unitUpdated;
    }

    public void setUnitUpdated(Date unitUpdated) {
        this.unitUpdated = unitUpdated;
    }

    public void addPlayer(String p) {
        if (p != null) {
            players.add(p);
        }
    }

    public void setPlayer(int i, String p) {
        if (i < players.size()) {
            if (p != null) {
                players.set(i, p);
            }
            else {
                players.remove(i);
            }
        }
        else if (p != null) {
            players.add(p);
        }
    }

    public void clearPlayers() {
        players.clear();
    }

    public String getPlayer(int i) {
        if (i < players.size()) {
            return players.get(i);
        }
        return null;
    }

    @Access(AccessType.PROPERTY)
    public String getPlayers() {
        return StringUtils.join(players, ';');
    }

    public void setPlayers(String p) {
        players.clear();
        if (p != null) {
            String[] split = p.split(";");
            for (String s : split) {
                if (StringUtils.isNotBlank(s)) {
                    players.add(s);
                }
            }
        }
    }

    public List<String> getPlayersList() {
        return players;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public static int indexOf(List<CartAssignment> list, int cartNumber) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                CartAssignment ca = list.get(i);
                if (ca.getCartNumber() == cartNumber) {
                    return i;
                }
            }
        }
        return -1;
    }

}