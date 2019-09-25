/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.tournament.model;

import com.stayprime.tournament.util.JpaScoringFormatConverter;
import com.stayprime.tournament.util.TournamentFormat;
import com.stayprime.tournament.util.XmlDateTimeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author Omer
 */
//@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"extSiteName", "extId"}))
@XmlAccessorType(XmlAccessType.FIELD)
public class Tournament {

    @Id @GeneratedValue
    @XmlAttribute
    private Integer id;

    @XmlAttribute
    private Integer siteId;

    // Properties of the tournament system peer

    @XmlAttribute
    private String sysType;

    @XmlAttribute
    private String extId;

    @XmlAttribute
    private String extSiteName;

    // Tournament listing properties

    private String name;

    @XmlJavaTypeAdapter(XmlDateTimeAdapter.class)
    private Date startDate;

    @XmlJavaTypeAdapter(XmlDateTimeAdapter.class)
    private Date endDate;

    // Updated and synchronized time

    @XmlJavaTypeAdapter(XmlDateTimeAdapter.class)
    private Date updated;

    @Transient
    @XmlTransient
    private long synced;

    // Tournament details

    private String logo;

    @Convert(converter = JpaScoringFormatConverter.class)
    private ScoringFormat defaultFormat;

    @Transient
    @XmlTransient
    private final List<ScoringFormat> formats;

    @Transient
    @XmlTransient
    private List<ScoringTeam> teams;

    //Properties of the round

    private int currentRoundNo;

    private int numberOfRounds;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tournamentId") @OrderBy("number")
    @XmlElementWrapper
    @XmlElement(name = "round")
    private List<TournamentRound> rounds;

    private static final String[] excludeFields = new String[] {
        "sinced", "teams", "rounds"
    };

    public Tournament() {
        teams = new ArrayList<ScoringTeam>();
        formats = new ArrayList<ScoringFormat>();
        rounds = new ArrayList<TournamentRound>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getExtSiteName() {
        return extSiteName;
    }

    public void setExtSiteName(String extSiteName) {
        this.extSiteName = extSiteName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public long getSynced() {
        return synced;
    }

    public void setSynced(long synced) {
        this.synced = synced;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public ScoringFormat getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(ScoringFormat defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    public List<ScoringFormat> getFormats() {
        return formats;
    }

    public void setFormats(List<ScoringFormat> formats) {
        this.formats.clear();
        this.formats.addAll(formats);
    }

    /*
     * Round information
     */

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public int getCurrentRoundNo() {
        return currentRoundNo;
    }

    public void setCurrentRoundNo(int currentRoundNo) {
        this.currentRoundNo = currentRoundNo;
    }

    public TournamentRound getCurrentRound() {
        return getRoundNo(currentRoundNo);
    }

    public List<TournamentRound> getRounds() {
        return rounds;
    }

    void setRounds(List<TournamentRound> rounds) {
        this.rounds = rounds;
        setNumberOfRounds();
    }

    private void setNumberOfRounds() {
        if (numberOfRounds < rounds.size()) {
            setNumberOfRounds(rounds.size());
        }
    }

    public void putRound(TournamentRound round) {
        if (round.getNumber() > 0) {
            int i = getRoundIndex(round.getNumber());
            if (i >= 0) {
                rounds.set(i, round);
            }
            else {
                rounds.add(round);
                Collections.sort(rounds);
                setNumberOfRounds();
            }
        }
        else {
            throw new IllegalArgumentException("Round number must be > 0");
        }
    }

    public TournamentRound getFirstRound() {
        if (rounds.isEmpty() == false) {
            return rounds.get(0);
        }
        return null;
    }

    public TournamentRound getRoundNo(int roundNo) {
        int i = getRoundIndex(roundNo);
        if (i >= 0) {
            return rounds.get(i);
        }
        return null;
    }

    private int getRoundIndex(int roundNo) {
        for (int i = 0; i < rounds.size(); i++) {
            TournamentRound r = rounds.get(i);
            if (r.getNumber() == roundNo) {
                return i;
            }
        }
        return -1;
    }

    /*
     * Teams
     */

    public List<ScoringTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<ScoringTeam> teams) {
        this.teams = teams;
    }

    /*
     * Functional methods
     */

    public boolean isActive(Date date) {
        if (startDate == null || date == null) {
            return false;
        }

        long time = date.getTime();
        long start = startDate.getTime();

        //If endDate is not defined, use startDate
        Date validEndDate = endDate != null ? endDate : startDate;

        //Add 24h to the end date to account for the entire day of end date
        long end = validEndDate.getTime() + TimeUnit.DAYS.toMillis(1);

        return time >= start && time < end;
    }

    /*
     * Copy properties methods
     */

    public void setDbInfo(Tournament t) {
        setId(t.getId());
        setSiteId(t.getSiteId());
        setDbRoundsInfo(t);
    }

    private void setDbRoundsInfo(Tournament dbTournament) {
        //setNumberOfRounds(t.getNumberOfRounds());
        //setCurrentRoundNo(t.getCurrentRoundNo());
        for (TournamentRound dbRound : dbTournament.getRounds()) {
            TournamentRound round = getRoundNo(dbRound.getNumber());

            if (round != null) {
                round.setDbInfo(dbRound);
            }
        }
    }

    public void setPeerSystemInfo(Tournament t) {
        setSysType(t.getSysType());
        setExtSiteName(t.getExtSiteName());
        setExtId(t.getExtId());
    }

    public void setListingInfo(Tournament t) {
        setName(t.getName());
        setStartDate(t.getStartDate());
        setEndDate(t.getEndDate());
        setUpdated(t.getUpdated());
    }

    /*
     * Comparison and override methods
     */

    public boolean listingInfoEquals(Tournament t) {
        return extIdEquals(t) && nameEquals(t) && datesEquals(t);
    }

    public boolean extIdEquals(Tournament t) {
        return t != null && ObjectUtils.equals(extId, t.getExtId());
    }

    public boolean nameEquals(Tournament t) {
        return t != null && ObjectUtils.equals(name, t.getName());
    }

    public boolean datesEquals(Tournament t) {
        return t != null
                && ObjectUtils.equals(startDate, t.getStartDate())
                && ObjectUtils.equals(endDate, t.getEndDate());
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, excludeFields);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, excludeFields);
    }

    @Override
    public String toString() {
        return "Tournament [extId:" + extId
                + ", name:" + name
                + ", startDate:" + TournamentFormat.formatDateTime(startDate)
                + ", endDate:" + TournamentFormat.formatDateTime(endDate)
                + ", updated:" + TournamentFormat.formatDateTime(updated)
                + ", currentRoundNo:" + currentRoundNo
//                + ", currentRoundDate:" + TournamentFormat.formatDateTime(currentRoundDate)
                + "]";
    }

    public void setParents() {
        if (rounds != null) {
            for (TournamentRound r : rounds) {
                r.setTournament(this);
            }
        }
    }

}
