/*
 * 
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.model.golf.ScorecardModel;
import com.stayprime.golf.message.FnbOrderPayload;
import com.stayprime.golf.message.FnbOrderItem;
import com.stayprime.tournament.model.Player;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class GolfObjectEncoderTest {
    
    public GolfObjectEncoderTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testEncodeScoreCard() {
        System.out.println("encodeScoreCard");
//        fail("The test case is a prototype.");
//        BytePacket packet = null;
//        int siteId = 0;
//        int sourceId = 0;
//        int messageId = 0;
//        List<Player> players = null;
//        String emailAddress = "";
//        boolean expResult = false;
//        boolean result = GolfObjectEncoder.encodeScoreCard(packet, siteId, sourceId, messageId, players, emailAddress);
//        assertEquals(expResult, result);
    }

    @Test
    public void testEncodeFBOrder() {
        System.out.println("encodeFBOrder");
//        fail("The test case is a prototype.");
//        byte[] pack = null;
//        int offset = 0;
//        int hole = 0;
//        int hut = 0;
//        List<FnbOrderItem> orderItems = null;
//        int expResult = 0;
//        int result = GolfObjectEncoder.encodeFBOrder(pack, offset, hole, hut, orderItems);
//        assertEquals(expResult, result);
    }

    @Test
    public void testBuildOrderString() {
        System.out.println("buildOrderString");
        List<FnbOrderItem> items = new ArrayList<FnbOrderItem>();
        items.add(new FnbOrderItem(1, "Code1", null, null, 1f));
        items.add(new FnbOrderItem(2, "Code2", null, null, 10f));
        items.add(new FnbOrderItem(3, "Code3", null, null, 100f));
        
        String orderString = GolfObjectEncoder.buildOrderString(items);
        System.out.println(orderString);
        List<FnbOrderItem> result = GolfObjectEncoder.parseOrderString(orderString);

        assertTrue(ListUtils.isEqualList(items, result));
    }

    @Test
    public void testParseOrderString() {
        System.out.println("parseOrderString");
    }
    
}
