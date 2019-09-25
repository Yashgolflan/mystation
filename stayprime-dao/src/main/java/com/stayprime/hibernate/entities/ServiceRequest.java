package com.stayprime.hibernate.entities;
// Generated Sep 17, 2014 5:18:02 PM by Hibernate Tools 4.3.1


import com.aeben.golfclub.RequestType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * ServiceRequest generated by hbm2java
 */
@Entity
@Table(name = "service_request")
public class ServiceRequest implements java.io.Serializable {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int cartNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private int type;

    @Column(nullable = false)
    private int status = RequestType.REQUEST_CREATED;

    @Temporal(TemporalType.TIMESTAMP)
    private Date replyTime;

    private String repliedBy;
    
    @Transient
    private String additionalInfo;
    
    public ServiceRequest() {}
    
    public ServiceRequest(int cartNumber, int type, int status, Date time) {
        this.cartNumber = cartNumber;
        this.type = type;
        this.status = status;
        this.time = time;        
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCartNumber() {
        return this.cartNumber;
    }

    public void setCartNumber(int cartNumber) {
        this.cartNumber = cartNumber;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getReplyTime() {
        return this.replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public String getRepliedBy() {
        return this.repliedBy;
    }

    public void setRepliedBy(String repliedBy) {
        this.repliedBy = repliedBy;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }

}
