/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.hibernate.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author sarthak
 */
@Entity
@Table(name = "walker_scorecard")
public class WalkerScorecard implements Serializable {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    
    @Column(name = "cart_number")
    private Integer cartNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "player_names")
    private String playerNames;

    @Column(name = "scores")
    private String scores;
    
    @Column(name = "putts")
    private String putts;
    
    @Column(name = "drives")
    private String drives;
    
    @Column(name = "clubs")
    private String clubs;
    
    @Column(name = "chips")
    private String chips;
    
    @Column(name = "sand_shots")
    private String sandShots;
    
    @Column(name = "saves")
    private String saves;
    
    @Column(name = "penalties")
    private String penalties;

    @Column(name = "holes_played")
    private String holesPlayed;

    @Column(name = "par_values")
    private String parValues;

    @Column(name = "round_id")
    private int roundId;

    @Embedded
    private MessageStatus sendStatus;
    
    public WalkerScorecard() {
        this.sendStatus = new MessageStatus();
    }
    
    public WalkerScorecard(String playerNames, String scores) {
        sendStatus = new MessageStatus();
        this.playerNames = playerNames;
        this.scores = scores;
    }

    public WalkerScorecard(String playerNames, String scores, Integer cartNumber, String email, Date created) {
        sendStatus = new MessageStatus();
        this.playerNames = playerNames;
        this.scores = scores;
        this.cartNumber = cartNumber;
        this.email = email;
        sendStatus.setCreated(created);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(String playerNames) {
        this.playerNames = playerNames;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public Integer getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(Integer cartNumber) {
        this.cartNumber = cartNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHolesPlayed() {
        return holesPlayed;
    }

    public void setHolesPlayed(String holesPlayed) {
        this.holesPlayed = holesPlayed;
    }

    public String getParValues() {
        return parValues;
    }

    public void setParValues(String parValues) {
        this.parValues = parValues;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public MessageStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(MessageStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getPutts() {
        return putts;
    }

    public void setPutts(String putts) {
        this.putts = putts;
    }

    public String getDrives() {
        return drives;
    }

    public void setDrives(String drives) {
        this.drives = drives;
    }

    public String getClubs() {
        return clubs;
    }

    public void setClubs(String clubs) {
        this.clubs = clubs;
    }

    public String getChips() {
        return chips;
    }

    public void setChips(String chips) {
        this.chips = chips;
    }

    public String getSandShots() {
        return sandShots;
    }

    public void setSandShots(String sandShots) {
        this.sandShots = sandShots;
    }

    public String getSaves() {
        return saves;
    }

    public void setSaves(String saves) {
        this.saves = saves;
    }

    public String getPenalties() {
        return penalties;
    }

    public void setPenalties(String penalties) {
        this.penalties = penalties;
    }
    
}
