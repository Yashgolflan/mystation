/*
 * 
 */
package com.stayprime.tournament.golfgenius;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

/**
 *
 * @author benjamin
 */
@Entity
public class GgEvent {

    @Id
    private int eventId;

    private String name;

    private Date updated;

    @Transient
    private List<GgTournament> tournaments;

    @Lob
    private String checkRequest;

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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<GgTournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<GgTournament> tournaments) {
        this.tournaments = tournaments;
    }

    public boolean isInProgress() {
        if (tournaments != null) {
            for (GgTournament t : tournaments) {
                if (t.isInProgress()) {
                    return true;
                }
            }
        }
        return false;
    }

}
