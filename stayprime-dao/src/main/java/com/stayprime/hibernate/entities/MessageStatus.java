/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.hibernate.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author nirajcj
 */
@Embeddable
public class MessageStatus implements Serializable {
    public static final int INITIAL = 0;
    public static final int QUEUED = 1;
    public static final int SENT = 2;
    public static final int FAIL = 3;
    public static final int SUCCESS = 4;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    private int sentAfter;

    private int ackdAfter;

    private int messageCounter;

    private int messageRetries;

    private int status;

    public MessageStatus() {
//        created = new Date();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getSentAfter() {
        return sentAfter;
    }

    public void setSentAfter(int sentAfter) {
        this.sentAfter = sentAfter;
    }

    public int getAckdAfter() {
        return ackdAfter;
    }

    public void setAckdAfter(int ackdAfter) {
        this.ackdAfter = ackdAfter;
    }

    public int getMessageRetries() {
        return messageRetries;
    }

    public void setMessageRetries(int messageRetries) {
        this.messageRetries = messageRetries;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMessageCounter() {
        return messageCounter;
    }

    public void setMessageCounter(int messageCounter) {
        this.messageCounter = messageCounter;
    }

}
