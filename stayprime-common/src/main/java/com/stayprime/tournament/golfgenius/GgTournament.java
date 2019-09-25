/*
 * 
 */
package com.stayprime.tournament.golfgenius;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 * @author benjamin
 */
@Entity
public class GgTournament {

    @Id
    private int id;

    private int eventId;

//    @ManyToOne
//    @Column(name="eventId")
//    private GgEvent event;

    private String name;

    private boolean adjusted;

    private Date date;

    private Date creationDate;

    private String status;

    private String format;

    @Lob
    private String html;

    private Date updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdjusted() {
        return adjusted;
    }

    public void setAdjusted(boolean adjusted) {
        this.adjusted = adjusted;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isInProgress() {
        return "in progress".equals(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "GgTournament [id:" + id + ", name:" + name
                + ", date:" + date + ", status:" + status + "]";
    }

}
