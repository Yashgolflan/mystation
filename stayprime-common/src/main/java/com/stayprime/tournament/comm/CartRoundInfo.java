/*
 * 
 */
package com.stayprime.tournament.comm;

import com.stayprime.comm.encoder.Encoder;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.comm.encoder.TournamentEncoder;
import com.stayprime.golf.message.Payload;
import com.stayprime.tournament.model.Player;
import com.stayprime.tournament.model.TournamentRound;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class CartRoundInfo implements Payload {
    private TournamentRound round;
    private List<Player> players;

    public CartRoundInfo() {
        this(null, null);
    }

    public CartRoundInfo(TournamentRound round, List<Player> players) {
        this.round = round;
        this.players = players;
    }

    public TournamentRound getRound() {
        return round;
    }

    public void setRound(TournamentRound round) {
        this.round = round;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.TMT_ROUND_PLAYERS;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        Encoder e = new Encoder(pack, offset);
        TournamentEncoder.encodeRoundAndPlayers(e, round, players);
        return e.getOffset();
    }

}
