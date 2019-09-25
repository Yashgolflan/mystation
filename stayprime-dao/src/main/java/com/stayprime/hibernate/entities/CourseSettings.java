package com.stayprime.hibernate.entities;
// Generated Sep 17, 2014 5:18:02 PM by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * CourseSettings generated by hbm2java
 */
@Entity
@Table(name = "course_settings")
public class CourseSettings implements java.io.Serializable {

    @Id @Column(unique = true, nullable = false)
    private String name;

    private String value;

    @Temporal(TemporalType.TIMESTAMP) @Column(nullable = false)
    private Date lastUpdated;

    public CourseSettings() {
    }

    public CourseSettings(String name, String value, Date lastUpdated) {
        this.name = name;
        this.value = value;
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String value(CourseSettings cs) {
        return cs == null? null : cs.getValue();
    }

    public static boolean booleanValue(CourseSettings cs) {
        return cs != null && Boolean.valueOf(cs.getValue());
    }

    public static Date getDateFromUnixTimestamp(CourseSettings cs) {
        return cs == null? null : getDateFromUnixTimestamp(cs.getValue());
    }

    public static Date getDateFromUnixTimestamp(String s) {
        long ts = NumberUtils.toLong(s);
        return ts > 0 ? new Date(ts) : null;
    }

    public static String findValue(List<CourseSettings> list, String name) {
        CourseSettings ss = findSetting(list, name);
        return ss == null? null : ss.getValue();
    }

    public static CourseSettings findSetting(List<CourseSettings> list, String name) {
        if (list == null) {
            return null;
        }
        for (CourseSettings ss : list) {
            if (ObjectUtils.equals(ss.getName(), name)) {
                return ss;
            }
        }
        return null;
    }

}