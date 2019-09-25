/*
 * 
 */
package com.stayprime.model.golf;

/**
 *
 * @author benjamin
 */
public class ScoreCardRow {
    private String playerName;
    private final int score[];

    public ScoreCardRow() {
        this(null, 18);
    }

    public ScoreCardRow(String playerName, int scoreLength) {
        this.playerName = playerName;
        this.score = new int[scoreLength];
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int[] getScore() {
        return score;
    }

}
