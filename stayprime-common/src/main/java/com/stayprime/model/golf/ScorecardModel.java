/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.model.golf;

import com.stayprime.comm.encoder.GolfObjectEncoder;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.golf.message.Payload;
import com.stayprime.tournament.model.Player;
import com.stayprime.tournament.model.PlayerScores;

import java.util.List;

/**
 *
 * @author Omer
 */
public class ScorecardModel implements Payload {

    private List<PlayerScores> playerScores;
    private String email;

    /**
     *
     * @param email
     * @param playerScores
     */
    public ScorecardModel(List<PlayerScores> playerScores, String email) {
        this.playerScores = playerScores;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PlayerScores> getScoreCardRowList() {
        return playerScores;
    }

    public void setScoreCardRowList(List<PlayerScores> scores) {
        this.playerScores = scores;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.SCORECARD_STAT;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        return GolfObjectEncoder.encodeScoreCard(pack, offset, playerScores, email);
    }

}
