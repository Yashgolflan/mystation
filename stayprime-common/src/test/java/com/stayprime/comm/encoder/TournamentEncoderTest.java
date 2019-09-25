/*
 *
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.tournament.comm.CartRoundInfo;
import com.stayprime.tournament.comm.PlayerScoresList;
import com.stayprime.tournament.comm.TournamentInfo;
import com.stayprime.tournament.model.Player;
import com.stayprime.tournament.model.PlayerScores;
import com.stayprime.tournament.model.TournamentRound;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class TournamentEncoderTest {

    public TournamentEncoderTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEncodePlayerScores() {
        System.out.println("encodePlayerScores");
        BytePacket packet = new BytePacket();
        Packet<PlayerScoresList> p1 = new Packet<PlayerScoresList>();
        p1.setSiteId(1);
        p1.setCartNumber(10);
        p1.setMessageCounter(3);

        PlayerScoresList list1 = new PlayerScoresList();
        list1.setExtId("StayPrime");
        list1.setRoundNo(1);
        p1.setPayload(list1);

        String name = null;
        list1.add(createScores(1, new Player(0, "12345", name)));
        list1.add(createScores(2, new Player(0, "234567", name)));
        list1.add(createScores(3, new Player(0, "345", name)));
        list1.add(createScores(4, new Player(0, "232312", name)));

        p1.encode(packet);

        Packet<PlayerScoresList> p2 = TournamentEncoder.decodePlayerScores(packet, 18);
        PlayerScoresList list2 = p2.getPayload();

        assertEquals(p1.getSiteId(), p2.getSiteId());
        assertEquals(p1.getCartNumber(), p2.getCartNumber());
        assertEquals(p1.getMessageCounter(), p2.getMessageCounter());
        assertEquals(list1.getExtId(), list2.getExtId());
        assertEquals(list1.getRoundNo(), list2.getRoundNo());

        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i), list2.get(i));
        }
    }

    private PlayerScores createScores(int add, Player player) {
        PlayerScores scores = new PlayerScores(18, player);
        for (int i = 0; i < scores.getCount(); i++) {
            scores.setScore(i, (add + i) % 6);
        }
        return scores;
    }

    private Player createPlayer(int i, Integer cart) {
        String name = StringUtils.repeat("2" + i, 6);
        String extId = StringUtils.repeat("1" + i, 4);
        Player player = new Player(0, extId, name);
        player.setCart(cart);
        return player;
    }

    @Test
    public void testEncodeRoundAndPlayers() {
        System.out.println("encodeRoundAndPlayers");

        BytePacket bp = new BytePacket();
        int siteId = 1;
        int cartNumber = 2;
        int messageCounter = 3;

        TournamentRound r1 = new TournamentRound(1);
        r1.setExtId("r1");
        r1.setTournamentName("Tournament");

        List<Player> pl = new ArrayList<Player>();
        pl.add(createPlayer(1, cartNumber));
        pl.add(createPlayer(2, cartNumber));

        boolean result = TournamentEncoder.encodeRoundAndPlayers(bp, siteId, cartNumber, messageCounter, r1, pl);
        assertTrue(result);

        Packet<CartRoundInfo> pk = TournamentEncoder.decodeRoundAndPlayers(bp);
        CartRoundInfo decoded = pk.getPayload();
        assertEquals(r1, decoded.getRound());
        assertArrayEquals(pl.toArray(), decoded.getPlayers().toArray());
    }

    /**
     * Test of encodeTournamentRequest method, of class TournamentEncoder.
     */
    @Test
    public void testEncodeTournamentRequest() {
        System.out.println("encodeTournamentRequest");
        BytePacket packet = new BytePacket();
        int siteId = 1;
        int cartNumber = 101;
        int messageCounter = 0;
        boolean result = TournamentEncoder.encodeTournamentRequest(packet, siteId, cartNumber, messageCounter);
        assertTrue(result);

        Packet<TournamentInfo> p2 = TournamentEncoder.decodeTournamentRequest(packet);
        assertEquals(siteId, p2.getSiteId());
        assertEquals(cartNumber, p2.getCartNumber());
        assertEquals(messageCounter, p2.getMessageCounter());
        TournamentInfo ti = p2.getPayload();
        assertNull(ti.getExtId());
    }

}
