/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.tournament.model;

import com.stayprime.tournament.util.XmlScoresAdapter;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author sarthak
 */
@XmlAccessorType(XmlAccessType.NONE)
public class WalkerPlayerScores {
    @XmlElement
    private Player player;

    @XmlAttribute
    private int revisionId;

    @XmlValue @XmlJavaTypeAdapter(XmlScoresAdapter.class)
    private Integer scores[];

    private Integer[] putts;

    private Integer[] drives;

    private Integer[] clubs;

    private Integer[] chips;

    private Integer[] sandShots;

    private Integer[] saves;

    private Integer[] penalties;

    private int totalOut;

    private int totalIn;

    private int total;

    public int getPutts(int i) {
        return putts[i] == null? 0 : putts[i];
    }

    public void setPutts(int i, int putt) {
        putts[i] = putt;
//        updateTotals();
    }

    public int getDrive(int i) {
        return drives[i] == null? 0 : drives[i];
    }

    public void setDrive(int i, int drive) {
        drives[i] = drive;
    }

    public int getClub(int i) {
        return clubs[i] == null? 0 : clubs[i];
    }

    public void setClub(int i, int club) {
        clubs[i] = club;
    }

    public int getChips(int i) {
        return chips[i] == null? 0 : chips[i];
    }

    public void setChips(int i, int chip) {
        chips[i] = chip;
    }

    public int getSandShots(int i) {
        return sandShots[i] == null? 0 : sandShots[i];
    }

    public void setSandShots(int i, int sandShot) {
        sandShots[i] = sandShot;
    }

    public int getSaves(int i) {
        return saves[i] == null? 0 : saves[i];
    }

    public void setSaves(int i, int save) {
        saves[i] = save;
    }

    public int getPenalties(int i) {
        return penalties[i] == null? 0 : penalties[i];
    }

    public void setPenalties(int i, int penalty) {
        penalties[i] = penalty;
    }

    public WalkerPlayerScores(int holeCount) {
        scores = new Integer[holeCount];
        putts = new Integer[holeCount];
        drives = new Integer[holeCount];
        clubs = new Integer[holeCount];
        chips = new Integer[holeCount];
        sandShots = new Integer[holeCount];
        saves = new Integer[holeCount];
        penalties = new Integer[holeCount];
    }

    public WalkerPlayerScores() {
        this(0);
    }

    public WalkerPlayerScores(int holeCount, Player p) {
        scores = new Integer[holeCount];
        putts = new Integer[holeCount];
        drives = new Integer[holeCount];
        clubs = new Integer[holeCount];
        chips = new Integer[holeCount];
        sandShots = new Integer[holeCount];
        saves = new Integer[holeCount];
        penalties = new Integer[holeCount];
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(int revisionId) {
        this.revisionId = revisionId;
    }

//    public int[] getScores() {
//        return scores;
//    }
//
//    public void setScores(int[] scores) {
//        this.scores = scores;
//    }

    public void setScores(Integer[] scores) {
        System.arraycopy(scores, 0, this.scores, 0, this.scores.length);
        updateTotals();
    }

    public void setScore(int i, int score) {
//        if (scores != null && i > 0 && i < scores.length) {
        scores[i] = score;
        updateTotals();
//        }
    }

    public int getScore(int i) {
//        if (scores != null && i > 0 && i < scores.length) {
        return scores[i] == null? 0 : scores[i];
//        }
//        return 0;
    }

    public int getCount() {
        return scores.length;
    }

    public int getTotalOut() {
        return totalOut;
    }

    public int getTotalIn() {
        return totalIn;
    }

    public int getTotal() {
        return total;
    }

    private void updateTotals() {
        total = totalOut = totalIn = 0;

        for (int i = 0; i < scores.length; i++) {
            int score =  scores[i] == null? 0 : scores[i];
            if (i < 9) {
                totalOut += score;
            }
            else {
                totalIn += score;
            }
            total += score;
        }
    }

    public void copyFrom(WalkerPlayerScores scores) {
        int min = Math.min(scores.getCount(), getCount());

        for (int i = 0; i < min; i++) {
            setScore(i, scores.getScore(i));
        }
    }

    public int[] copyTo(int[] array) {
        int min = Math.min(array.length, getCount());

        for (int i = 0; i < min; i++) {
            array[i] = getScore(i);
        }

        return array;
    }

    public String scoresToString() {
        return Arrays.toString(scores);
    }
    
    public String puttsToString() {
        return Arrays.toString(putts);
    }
     
    public String drivesToString() {
        return Arrays.toString(drives);
    }
        
    public String clubsToString() {
        return Arrays.toString(clubs);
    }
            
    public String chipsToString() {
        return Arrays.toString(chips);
    }
                
    public String sandShotsToString() {
        return Arrays.toString(sandShots);
    }
                    
    public String savesToString() {
        return Arrays.toString(saves);
    }
                        
    public String penaltiesToString() {
        return Arrays.toString(penalties);
    }

    public void clear() {
        Arrays.fill(scores, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WalkerPlayerScores other = (WalkerPlayerScores) obj;
        if (this.revisionId != other.revisionId)
            return false;
        if (!Arrays.equals(this.scores, other.scores))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.revisionId;
        hash = 53 * hash + Arrays.hashCode(this.scores);
        return hash;
    }
}
