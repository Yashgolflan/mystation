/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.model.golf;

import com.stayprime.comm.encoder.GolfObjectEncoder;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.golf.message.Payload;
import com.stayprime.tournament.model.WalkerPlayerScores;
import java.util.List;

/**
 *
 * @author sarthak
 */
public class WalkerScorecardModel implements Payload {
    
    private List<WalkerPlayerScores> playerScores;
    private String email;

    public WalkerScorecardModel(List<WalkerPlayerScores> playerScores, String email) {
        this.playerScores = playerScores;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<WalkerPlayerScores> getScoreCardRowList() {
        return playerScores;
    }

    public void setScoreCardRowList(List<WalkerPlayerScores> scores) {
        this.playerScores = scores;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.WALKER_SCORECARD_STAT;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        return GolfObjectEncoder.encodeWalkerScorecard(pack, offset, playerScores, email);
    }

}
