/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.hibernate.entities;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 * @author Omer
 */
@Entity
@Table(name = "huts_info", uniqueConstraints = @UniqueConstraint(columnNames = "hutNumber"))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

public class HutsInfo implements java.io.Serializable {

    public static int generateHutNumber(EventList<HutsInfo> sourceList) {
        if (!CollectionUtils.isEmpty(sourceList)) {
            List<Integer> huts = new ArrayList<Integer>();
            for (HutsInfo h: sourceList){
                huts.add(h.getHutNumber());
            }
            return (Collections.max(huts) + 1);
        }

        return 1;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private int hutNumber;

    private String phoneNumber;

    private String fromNumber;

    private String email;

    private Integer type;

    private String holes;

    public HutsInfo() {
    }

    public HutsInfo(int hutNumber) {
        this.hutNumber = hutNumber;
        this.setPhoneNumber("+");
    }

    public HutsInfo(int hutNumber, String phoneNumber, String email, int type, String holes) {
        this.hutNumber = hutNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.type = type;
        this.holes = holes;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getHutNumber() {
        return this.hutNumber;
    }

    public void setHutNumber(int hutNumber) {
        this.hutNumber = hutNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getHoles() {
        return holes;
    }

    public void setHoles(String holes) {
        this.holes = holes;
    }

    public static HutsInfo findByHutNumber(List<HutsInfo> list, int hutNumber) {
        for (HutsInfo hi : list) {
            if (hi.getHutNumber() == hutNumber) {
                return hi;
            }
        }
        return null;
    }

    public static HutsInfo findById(List<HutsInfo> list, int hutId) {
        for (HutsInfo hi : list) {
            if (hi.getId()== hutId) {
                return hi;
            }
        }
        return null;
    }

}
