/*
 * 
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
 * @author benjamin
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PlayerScores {
    @XmlElement
    private Player player;

    @XmlAttribute
    private int revisionId;

    @XmlValue @XmlJavaTypeAdapter(XmlScoresAdapter.class)
    private Integer scores[];

    private int totalOut;

    private int totalIn;

    private int total;

    PlayerScores() {
        this(18);
    }

    public PlayerScores(int holeCount) {
        this(holeCount, null);
    }

    public PlayerScores(int holeCount, Player player) {
        scores = new Integer[holeCount];
        this.player = player;
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

    public void copyFrom(PlayerScores scores) {
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

    public void clear() {
        Arrays.fill(scores, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PlayerScores other = (PlayerScores) obj;
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
