/*
 * 
 */
package com.stayprime.tournament.golfgenius;

import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class GgPlayer {

    private int playerId;

    private String playerName;

    private boolean postScoresOnFront9;

    private boolean postScoresOnBack9;

    public GgPlayer() {
    }

    public GgPlayer(int playerId, String playerName, boolean postScoresOnFront9, boolean postScoresOnBack9) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.postScoresOnFront9 = postScoresOnFront9;
        this.postScoresOnBack9 = postScoresOnBack9;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isPostScoresOnFront9() {
        return postScoresOnFront9;
    }

    public void setPostScoresOnFront9(boolean postScoresOnFront9) {
        this.postScoresOnFront9 = postScoresOnFront9;
    }

    public boolean isPostScoresOnBack9() {
        return postScoresOnBack9;
    }

    public void setPostScoresOnBack9(boolean postScoresOnBack9) {
        this.postScoresOnBack9 = postScoresOnBack9;
    }

    @Override
    public String toString() {
        return "GgPlayer " + playerId + "-" + playerName;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static String listToString(List<GgPlayer> playerList) {
        StringBuilder b = new StringBuilder();
        if (playerList != null) {
            for (GgPlayer p : playerList) {
                b.append(p.getPlayerId()).append(',');
                b.append(p.getPlayerName()).append(',');
                b.append(p.isPostScoresOnFront9()).append(',');
                b.append(p.isPostScoresOnBack9()).append(';');
            }
        }
        return b.toString();
    }

    public static List<GgPlayer> addToListFromString(List<GgPlayer> playerList, String players) {
        if (players != null) {
            String[] playersArr = players.split(";");
            for (String plStr : playersArr) {
                GgPlayer p = fromString(plStr);
                if (p != null) {
                    playerList.add(p);
                }
            }
        }
        return playerList;
    }

    public static GgPlayer fromString(String plStr) {
        String[] f = plStr.split(",");
        if (f.length >= 4) {
            return new GgPlayer(NumberUtils.toInt(f[0]), f[1],
                    Boolean.valueOf(f[2]), Boolean.valueOf(f[3]));
        }
        return null;
    }

}
