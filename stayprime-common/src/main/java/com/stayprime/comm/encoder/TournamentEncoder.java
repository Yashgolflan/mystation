/*
 *
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.tournament.comm.CartRoundInfo;
import com.stayprime.tournament.comm.PlayerScoresList;
import com.stayprime.tournament.comm.TournamentInfo;
import com.stayprime.tournament.model.Leaderboard;
import com.stayprime.tournament.model.Player;
import com.stayprime.tournament.model.PlayerScores;
import com.stayprime.tournament.model.TournamentRound;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class TournamentEncoder {

    private static int maxListSize = 64;

    /*
     * Scores
     */

    public static int encodePlayerScores(byte[] pack, int offset, String tmtExtId, int roundNo, List<PlayerScores> playerScores) {
        int off = offset;
        //Encode tournament id (empty for now)
        off = EncodeUtil.encodeString(pack, off, tmtExtId, false);

        pack[off] = (byte) roundNo;
        off++;

        int playerCount = EncodeUtil.encodeLength(pack, off, playerScores.size(), 6);
        off++;

        for (int i = 0; i < playerCount; i++) {
            PlayerScores scores = playerScores.get(i);
            off = encodePlayerScore(pack, off, scores);
        }

        return off;
    }

    static int encodePlayerScore(byte[] pack, int offset, PlayerScores scores) {
        Player player = scores.getPlayer();
        int off = offset;
        off = EncodeUtil.encodeString(pack, off, player.getExtId(), false);

        //Encode scores revision
        EncodeUtil.encodeShort(pack, off, scores.getRevisionId(), 0);
        off += 2;

        int scoresLength = EncodeUtil.encodeLength(pack, off, scores.getCount(), 18);
        off++;

        for (int j = 0; j < scoresLength; j++) {
            pack[off] = (byte) scores.getScore(j);
            off++;
        }

        return off;
    }

    public static Packet<PlayerScoresList> decodePlayerScores(BytePacket packet, int holeCount) {
        byte[] pack = packet.getPacket();

        Packet<PlayerScoresList> re = new Packet<PlayerScoresList>();
        int off = EncodeUtil.decodeHeader(pack, 0, re);

        //Check if the packet is the right type
        if (PacketType.TMT_SCORES.test(re.getPacketType()) == false) {
            return null;
        }

        PlayerScoresList list = new PlayerScoresList();

        //Decode tournament id (empty for now)
        String tId = EncodeUtil.decodeString(pack, off, false);
        off += tId.length() + 1;
        list.setExtId(tId);

        int roundNo = EncodeUtil.decodeUnsigned(pack, off);
        list.setRoundNo(roundNo);
        off++;

        int playerCount = EncodeUtil.decodeUnsigned(pack, off);
        off++;

        for (int i = 0; i < playerCount; i++) {
            PlayerScores scores = new PlayerScores(holeCount);
            off = decodePlayerScore(pack, off, scores);
            list.add(scores);
        }

        re.setPayload(list);
        return re;
    }

    static int decodePlayerScore(byte[] pack, int offset, PlayerScores scores) {
        Player player = new Player();
        int off = offset;
        String str = EncodeUtil.decodeString(pack, off, false);
        off += str.length() + 1;
        player.setExtId(str);
        scores.setPlayer(player);

        //Decode scores revision
        int revisionId = EncodeUtil.decodeShort(pack, off);
        scores.setRevisionId(revisionId);
        off += 2;

        int scoresLength = EncodeUtil.decodeUnsigned(pack, off);
        off++;

        for (int si = 0; si < scoresLength; si++) {
            scores.setScore(si, pack[off]);
            off++;
        }

        return off;
    }

    /*
     * Tournament/cart assignment request
     */

    public static boolean encodeTournamentRequest(BytePacket packet, int siteId,
            int cartNumber,int messageCounter) {

        if (EncodeUtil.isValidCartNumber(cartNumber) == false) {
            return false; //Fail if the cart number is not valid for encoding
        }

        byte[] pack = packet.getPacket();
        int off = EncodeUtil.encodeHeader(pack, 0, siteId, PacketType.TMT_REQUEST.id, cartNumber, messageCounter);

        packet.setLength(off);
        return off > 0;
    }

    public static Packet<TournamentInfo> decodeTournamentRequest(BytePacket packet) {
        byte[] pack = packet.getPacket();

        Packet<TournamentInfo> re = new Packet<TournamentInfo>();
        int off = EncodeUtil.decodeHeader(pack, 0, re);

        //Check if the packet is the right type
        if (PacketType.TMT_REQUEST.test(re.getPacketType()) == false) {
            return null;
        }

        //Decode tournament id (empty for now)
//        String tId = EncodeUtil.decodeString(pack, off);
//        off += EncodeUtil.encodedLength(tId);

        // Only setting tournament GUID at this moment.
        TournamentInfo t = new TournamentInfo();
//        t.setExtId(tId);

        re.setPayload(t);
        return re;
    }

    /*
     * Round information
     */

    public static boolean encodeRoundAndPlayers(BytePacket packet, int siteId,
            int cartNumber, int messageCounter, TournamentRound round,
            List<Player> assigned) {

        PacketType type = PacketType.TMT_ROUND_PLAYERS;

        Encoder e = new Encoder(packet.getPacket(), 0);
        e.encodeHeader(siteId, type.id, cartNumber, messageCounter);
        encodeRoundAndPlayers(e, round, assigned);

        packet.setLength(e.getOffset());
        return e.isSuccess();
    }

    public static void encodeRoundAndPlayers(Encoder e,
            TournamentRound r, List<Player> assigned) {
        encodeRoundInfo(e, r);
        encodePlayerList(e, assigned);
        encodeLeaderboards(e, r);
        e.encodeUnsigned(r == null? 0 : r.getCourseId(), 0);
    }

    static void encodeRoundInfo(Encoder e, TournamentRound r) {
        if (r == null) {
            e.encodeString(null, false).encodeByte(0).encodeString(null, false);
        }
        else {
            e.encodeString(r.getExtId(), false); //roundId
            e.encodeByte(r.getNumber()); //roundNumber
            e.encodeString(r.getTournamentName(), false); //event name
            //e.encodeByte(r.getRevision());
        }
    }

    static void encodePlayerList(Encoder e, List<Player> players) {
        int playerCount = e.encodeListLength(players, maxListSize);
        for (int i = 0; i < playerCount; i++) {
            Player p = players.get(i);
            e.encodeString(p.getExtId(), false);
            e.encodeString(p.getName(), false);
            e.encodeUnsigned(p.getCart(), 0);
        }
    }

    static void encodeLeaderboards(Encoder e, TournamentRound r) {
        List<Leaderboard> boards = r == null? null : r.getLeaderboards();
        int boardCount = e.encodeListLength(boards, maxListSize);
        for (int i = 0; i < boardCount; i++) {
            Leaderboard l = boards.get(i);
            e.encodeString(l.getExtId(), false);
            e.encodeString(l.getName(), false);
        }
    }

    public static Packet<CartRoundInfo> decodeRoundAndPlayers(BytePacket packet) {
        Packet<CartRoundInfo> re = new Packet<CartRoundInfo>();
        Encoder e = new Encoder(packet.getPacket(), 0);
        e.decodeHeader(re, PacketType.TMT_ROUND_PLAYERS);

        //Check if the packet is the right type
        if (e.isSuccess() == false) {
            return null;
        }

        TournamentRound round = decodeRoundInfo(e);
        List<Player> assigned = decodePlayerList(e);
        List<Leaderboard> boards = decodeLeaderboards(e);
        round.setLeaderboards(boards);
        round.setCourseId(e.decodeUnsigned());

        CartRoundInfo roundInfo = new CartRoundInfo();
        roundInfo.setRound(round);
        roundInfo.setPlayers(assigned);
        re.setPayload(roundInfo);

        return re;
    }

    static TournamentRound decodeRoundInfo(Encoder e) {
        TournamentRound round = new TournamentRound(0);
        round.setExtId(e.decodeString(false));
        round.setNumber(e.decodeUnsigned());
        round.setTournamentName(e.decodeString(false));
        //round.setRevision(e.decodeUnsigned());
        return round;
    }

    static List<Player> decodePlayerList(Encoder e) {
        List<Player> playerList = new ArrayList<Player>();
        int playerCount = e.decodeUnsigned();

        for (int i = 0; i < playerCount; i++) {
            Player p = new Player();
            p.setExtId(e.decodeString(false));
            p.setName(e.decodeString(false));
            p.setCart(e.decodeUnsigned());
            playerList.add(p);
        }

        return playerList;
    }

    static List<Leaderboard> decodeLeaderboards(Encoder e) {
        List<Leaderboard> boards = new ArrayList<Leaderboard>();
        int boardCount = e.decodeUnsigned();

        for (int i = 0; i < boardCount; i++) {
            Leaderboard l = new Leaderboard();
            l.setExtId(e.decodeString(false));
            l.setName(e.decodeString(false));
            boards.add(l);
        }

        return boards;
    }

}
