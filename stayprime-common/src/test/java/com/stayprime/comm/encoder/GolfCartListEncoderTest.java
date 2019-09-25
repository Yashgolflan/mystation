/*
 * 
 */

package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.model.golf.GolfCart;
import com.stayprime.golf.message.GolfCartListMessage;
import com.stayprime.model.golf.Position;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class GolfCartListEncoderTest {
    
    public GolfCartListEncoderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of encodeCartAheadList method, of class GolfCartListEncoder.
     */
    @Test
    public void testEncodeCartAheadList() {
        System.out.println("encodeCartAheadList");
        BytePacket packet = new BytePacket();
        int siteId = 0;
        Position origin = new Position();

        List<GolfCart> carts = createSampleList(GolfCartListEncoder.maxCartListSize);

        boolean success = GolfCartListEncoder.encodeCartAheadList(packet, siteId, origin, carts, 0);
        assertTrue(success);

        Position originPos = new Position(origin.getX(), origin.getY());
        GolfCartListMessage decodeCartList = GolfCartListEncoder.decodeCartList(packet, originPos);
        List<GolfCart> carts1 = decodeCartList.getGolfCartList().getCarts();
        for (int i = 0; i < carts.size(); i++) {
            GolfCart cart0 = carts.get(i);
            GolfCart cart1 = carts1.get(i);

            //Cart numbers are not encoded
            //assertEquals(cart0.getCartNumber(), cart1.getCartNumber());

            //Test position encoding
            Position p0 = cart0.getPosition();
            Position p1 = cart1.getPosition();
            assertEquals(p0.getX(), p1.getX(), 0.00001);
            assertEquals(p0.getY(), p1.getY(), 0.00001);

            //Heading is not encoded, should be 0
            assertEquals(cart0.getHeading(), cart1.getHeading(), 2);
        }
    }

    /**
     * Test of encodeCartList method, of class GolfCartListEncoder.
     */
    @Test
    public void testEncodeCartList() {
        System.out.println("encodeCartAheadList");
        BytePacket packet = new BytePacket();
        int siteId = 0;
        Position origin = new Position();

        List<GolfCart> carts = createSampleList(GolfCartListEncoder.maxRangerListSize);

        boolean success = GolfCartListEncoder.encodeCartList(packet, siteId, origin, carts, 0);
        assertTrue(success);

        Position originPos = new Position(origin.getX(), origin.getY());
        GolfCartListMessage decodeCartList = GolfCartListEncoder.decodeCartList(packet, originPos);
        List<GolfCart> carts1 = decodeCartList.getGolfCartList().getCarts();
        for (int i = 0; i < carts.size(); i++) {
            GolfCart cart0 = carts.get(i);
            GolfCart cart1 = carts1.get(i);

            assertEquals(cart0.getNumber(), cart1.getNumber());

            Position p0 = cart0.getPosition();
            Position p1 = cart1.getPosition();
            assertEquals(p0.getX(), p1.getX(), 0.00001);
            assertEquals(p0.getY(), p1.getY(), 0.00001);
            assertEquals(cart0.getHeading(), cart1.getHeading(), 2);
        }
    }

    static List<GolfCart> createSampleList(int size) {
        List<GolfCart> carts = new ArrayList<GolfCart>();
        for (int i = 0; i < size; i++) {
            GolfCart cart = new GolfCart();
            cart.setNumber(i + 1);
            cart.setPosition(new Position(i/500d, -i/600d));
//            System.out.println(cart);
            carts.add(cart);
        }
        return carts;
    }

}
