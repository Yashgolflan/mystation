package com.stayprime.hibernate.entities;
// Generated Nov 27, 2014 11:16:00 AM by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang.ObjectUtils;

/**
 * UserLogin generated by hbm2java
 */
@Entity
@Table(name = "user_login", uniqueConstraints = @UniqueConstraint(columnNames = "userName"))
public class UserLogin implements java.io.Serializable {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    private String firstName;

    private String lastName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    public UserLogin() {
    }

    public UserLogin(String userName, String password, Date dateCreated) {
        this.userName = userName;
        this.password = password;
        this.dateCreated = dateCreated;
    }

    public UserLogin(String userName, String password, String firstName, String lastName, Date dateCreated) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateCreated = dateCreated;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public static UserLogin findByUserPass(List<UserLogin> list, String user, String pass) {
        for (UserLogin ul : list) {
            if (ObjectUtils.equals(ul.getUserName(), user) && ObjectUtils.equals(ul.getPassword(), pass)) {
                return ul;
            }
        }
        return null;
    }

}