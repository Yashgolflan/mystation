/*
 * 
 */
package com.stayprime.tournament.comm;

import com.stayprime.comm.encoder.PacketType;
import com.stayprime.comm.encoder.TournamentEncoder;
import com.stayprime.golf.message.Payload;
import com.stayprime.tournament.model.PlayerScores;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class PlayerScoresList implements Payload {
    private String extId;

    private int roundNo;

    private List<PlayerScores> playerScores;

    public PlayerScoresList() {
        this.playerScores = new ArrayList<PlayerScores>();
    }

    public PlayerScoresList(String extId, int roundNo, List<PlayerScores> playerScores) {
        this.extId = extId;
        this.roundNo = roundNo;
        this.playerScores = playerScores;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public int getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(int roundNo) {
        this.roundNo = roundNo;
    }

    public List<PlayerScores> getPlayerScores() {
        return playerScores;
    }

    public void setPlayerScores(List<PlayerScores> playerScores) {
        this.playerScores = playerScores;
    }

    public int size() {
        return playerScores.size();
    }

    public PlayerScores get(int i) {
        return playerScores.get(i);
    }

    public void add(PlayerScores p) {
        playerScores.add(p);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.TMT_SCORES;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        return TournamentEncoder.encodePlayerScores(pack, offset, extId, roundNo, playerScores);
    }

}
