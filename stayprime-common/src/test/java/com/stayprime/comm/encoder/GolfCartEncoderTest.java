///*
// *
// */
//package com.stayprime.comm.encoder;
//
//import com.stayprime.cartapp.CartAppConst;
//import com.stayprime.comm.BytePacket;
//import com.stayprime.model.golf.CartAppMode;
//import com.stayprime.model.golf.GolfCart;
//import com.stayprime.model.golf.GolfRound;
//import com.stayprime.model.golf.Position;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
///**
// *
// * @author benjamin
// */
//public class GolfCartEncoderTest {
//
//    public GolfCartEncoderTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() {
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of encodeGolfCartPosition method, of class GolfCartEncoder.
//     */
//    @Test
//    public void testEncodeGolfCartPosition() {
//        System.out.println("encodeGolfCartPosition");
//        BytePacket packet = new BytePacket();
//
//        Position origin = new Position(-5.23481689766756,36.40614184460752);
//        Position position = new Position(-5.227304,36.403763);
//        GolfCart cart = new GolfCart();
//        cart.setSiteId(100);
//        cart.setPosition(position);
//        cart.setHeading(24);
//        cart.setBatteryLevel(15);
//        cart.setDeviceType(CartAppConst.WALKER_SPE3);
//        
//        testEncodePosition(packet, origin, cart, false);
//        cart.setNumber(-1);
//        testEncodePosition(packet, origin, cart, false);
//        cart.setNumber(0);
//        testEncodePosition(packet, origin, cart, false);
//        cart.setNumber(1);
//        testEncodePosition(packet, origin, cart, true);
//        cart.setNumber(255);
//        testEncodePosition(packet, origin, cart, true);
//        cart.setNumber(256);
//        testEncodePosition(packet, origin, cart, false);
//
//        testEncodePositionAndStatus();
//    }
//
//    private void testEncodePosition(BytePacket packet, Position origin,
//            GolfCart cart, boolean shouldSucceed) {
//        boolean result = GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        assertEquals(shouldSucceed, result);
//
//        if (shouldSucceed) {
//            assertEquals(GolfCartEncoder.positionEnd, packet.getLength());
//
//            Position originPos = new Position(origin.getX(), origin.getY());
//            GolfCart cart1 = GolfCartEncoder.decodeGolfCartPosition(packet, originPos, (GolfCart) null, false);
//
//            Position position = cart.getPosition();
//            assertEquals(position.getX(), cart1.getPosition().getX(), 0.00001);
//            assertEquals(position.getY(), cart1.getPosition().getY(), 0.00001);
//            cart1.setPosition(position);
//
//            assertEquals(cart, cart1);
//        }
//    }
//
//    private void testEncodePositionAndStatus() {
//        int siteId = 74;
//        int cartNo = 80;
//        int appMode = PacketType.STATUS_APPMODE_GOLF;
//        Position position = new Position(-118.69587, 34.289177);
//        Position origin = new Position(-118.70008023823608, 34.30079447492958);
//        double heading = 0;
//        int currentHole = 0;
//        int viewHole = 0;
//        int paceOfPlay = 0;
//        int ckMode = 10; //Disabled
//
//        GolfCart golfCart = new GolfCart();
//        golfCart.setSiteId(siteId);
//        golfCart.setNumber(cartNo);
//        golfCart.setPosition(position);
//        golfCart.setHeading(heading);
//        BytePacket packet = new BytePacket();
//
//        boolean encoded = GolfCartEncoder.encodeGolfCartPosition(packet, 1, origin, golfCart);
//
//        assertTrue(encoded);
//        if (appMode == CartAppMode.GOLF.id) {
//            encoded &= GolfCartEncoder.encodeGameStatus(packet, currentHole, paceOfPlay);
//            encoded &= GolfCartEncoder.encodeCartAheadRequest(packet, viewHole);
//
//            if (ckMode == 11) { //ACCESSIBLE
//                appMode = PacketType.STATUS_APPMODE_GOLF_HANDICAP;
//            }
//        }
//        else if (appMode == CartAppMode.RANGER.id) {
//            appMode = PacketType.STATUS_APPMODE_RANGER;
//        }
//
//        int status = PacketType.STATUS_ACTIVE;
//
//        encoded &= GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//
//        GolfCart cart1 = new GolfCart();
//        Position originPos = new Position(origin.getX(), origin.getY());
//        GolfCartEncoder.decodeGolfCartPosition(packet, originPos, cart1, false);
//        assertEquals(position.getX(), cart1.getPosition().getX(), 0.00001);
//        assertEquals(position.getY(), cart1.getPosition().getY(), 0.00001);
//    }
//
//    /**
//     * Test of encodeGameStatus method, of class GolfCartEncoder.
//     */
//    @Test
//    public void testEncodeGameStatus() {
//        System.out.println("encodeGameStatus");
//        BytePacket packet = new BytePacket();
//
//        Position origin = new Position();
//        Position position = new Position(0.12, 0.5);
//        GolfCart cart = new GolfCart(100, null, 1);
//        cart.setPosition(position);
//        GolfRound round = new GolfRound();
//
//        testEncodeGameStatus(packet, round, false, false);
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        testEncodeGameStatus(packet, round, true, true);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        round = new GolfRound(0, 18, -127*60);
//        testEncodeGameStatus(packet, round, true, true);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        round = new GolfRound(0, 18, 0);
//        testEncodeGameStatus(packet, round, true, true);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        round = new GolfRound(0, 18, 127*60);
//        testEncodeGameStatus(packet, round, true, true);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        round = new GolfRound(0, 18, -128*60);
//        testEncodeGameStatus(packet, round, true, false);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        round = new GolfRound(0, 18, 128*60);
//        testEncodeGameStatus(packet, round, true, false);
//    }
//
//    private void testEncodeGameStatus(BytePacket packet, GolfRound round,
//            boolean shouldSucceed, boolean shouldEqual) {
//
//        boolean result = GolfCartEncoder.encodeGameStatus(packet,
//                round.getCurrentHole(), round.getPaceOfPlay());
//        assertEquals(shouldSucceed, result);
//
//        if (shouldSucceed) {
//            int len = GolfCartEncoder.positionEnd;
//            len += GolfCartEncoder.gameStatusLength;
//            assertEquals(len, packet.getLength());
//            GolfRound round1 = GolfCartEncoder.decodeGameStatus(packet, null, false);
//            if (shouldEqual) {
//                assertEquals(round, round1);
//            } else {
//                assertNotSame(round, round1);
//            }
//        }
//    }
//
//    /**
//     * Test of encodeCartAheadRequest method, of class GolfCartEncoder.
//     */
//    @Test
//    public void testEncodeCartAheadRequest() {
//        System.out.println("encodeCartAheadRequest");
//        BytePacket packet = new BytePacket();
//
//        boolean result = GolfCartEncoder.encodeCartAheadRequest(packet, 0);
//        assertFalse(result);
//
//        testEncodeCartAheadRequest(packet, 0, -1, false);
//        testEncodeCartAheadRequest(packet, 0, 0, true);
//        testEncodeCartAheadRequest(packet, 0, 1, true);
//        testEncodeCartAheadRequest(packet, 0, 255, true);
//        testEncodeCartAheadRequest(packet, 0, 256, false);
//
//        testEncodeCartAheadRequest(packet, 1, -1, false);
//        testEncodeCartAheadRequest(packet, 1, 0, true);
//        testEncodeCartAheadRequest(packet, 1, 1, true);
//        testEncodeCartAheadRequest(packet, 1, 255, true);
//        testEncodeCartAheadRequest(packet, 1, 256, false);
//    }
//
//    private void testEncodeCartAheadRequest(BytePacket packet, int currentHole, int holeNumber, boolean equals) {
//        Position origin = new Position();
//        Position position = new Position(0.12, 0.5);
//        GolfCart cart = new GolfCart(100, null, 1);
//        cart.setPosition(position);
//        cart.setHeading(24);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        GolfCartEncoder.encodeGameStatus(packet, currentHole, 0);
//        boolean result = GolfCartEncoder.encodeCartAheadRequest(packet, holeNumber);
//        assertEquals(true, result);
//        int holeNumber1 = GolfCartEncoder.decodeCartAheadRequest(packet, false);
//        if (equals) {
//            assertEquals(holeNumber, holeNumber1);
//        } else {
//            assertNotSame(holeNumber, holeNumber1);
//        }
//    }
//
//    /**
//     * Test of encodeCartStatus method, of class GolfCartEncoder.
//     */
//    @Test
//    public void testEncodeCartStatus() {
//        System.out.println("encodeCartStatus");
//        BytePacket packet = new BytePacket();
//
//        int appMode = 0;
//        int status = 0;
//
//        boolean success = GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//        assertFalse(success);
//
//        Position origin = new Position();
//        Position position = new Position(0.12, 0.5);
//        GolfCart cart = new GolfCart(100, null, 1);
//        cart.setPosition(position);
//        cart.setHeading(24);
//
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//
//        //Encode status when it was already encoded
//        success = GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//        assertTrue(success);
//
//        appMode = CartAppMode.GOLF.id;
//        status = 0;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//
//        status = PacketType.STATUS_ACTIVE;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//        status += PacketType.STATUS_CARTPATHONLY;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//        status += PacketType.STATUS_WEATHERALERT;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//        status += PacketType.STATUS_RESTRICTEDZONE;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//        status += PacketType.STATUS_OUTOFCARTPATH;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//
//        appMode = CartAppMode.RANGER.id;
//        status = 0;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//
//        status = PacketType.STATUS_ACTIVE;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//        status += PacketType.STATUS_CARTPATHONLY;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//        status += PacketType.STATUS_WEATHERALERT;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//        status += PacketType.STATUS_RESTRICTEDZONE;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//        status += PacketType.STATUS_OUTOFCARTPATH;
//        testEncodePositionAndStatus(packet, origin, cart, appMode, status);
//    }
//
//    private void testEncodePositionAndStatus(BytePacket packet, Position origin,
//            GolfCart cart, int appMode, int status) {
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        GolfCartEncoder.encodeGameStatus(packet, 0, 0);
//
//        testEncodeStatus(packet, appMode, status);
//    }
//
//    private void testEncodeStatus(BytePacket packet, int appMode, int status) {
//        boolean success = GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//        assertTrue(success);
//
//        int decodedAppMode = GolfCartEncoder.decodeCartMode(packet, false);
//        assertEquals(appMode, decodedAppMode);
//
//        int decodedStatus = GolfCartEncoder.decodeCartStatus(packet, false);
//        assertEquals(status, decodedStatus);
//    }
//
//    /**
//     * Test of encodeCartStatus method, of class GolfCartEncoder.
//     */
//    @Test
//    public void testEncodePreMsg() {
//        System.out.println("encodeCartStatus");
//        BytePacket packet = new BytePacket();
//
//        int appMode = 0;
//        int status = 0;
//        int preMsg = 7;
//        boolean success = GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//        assertFalse(success);
//
//        Position origin = new Position();
//        Position position = new Position(0.12, 0.5);
//        GolfCart cart = new GolfCart(100, null, 1);
//        cart.setPosition(position);
//        cart.setHeading(24);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        GolfCartEncoder.encodeGameStatus(packet, 0, 0);
//        GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//        success = GolfCartEncoder.encodePreMsg(packet, preMsg);
//        assertTrue(success);
//        int msg = GolfCartEncoder.decodePreMsg(packet);
//        assertEquals(preMsg, msg);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//        success = GolfCartEncoder.encodePreMsg(packet, preMsg);
//        assertTrue(success);
//        msg = GolfCartEncoder.decodePreMsg(packet);
//        assertEquals(preMsg, msg);
//
//        //Encode status when it was already encoded
//        success = GolfCartEncoder.encodePreMsg(packet, preMsg);
//        assertTrue(success);
//        msg = GolfCartEncoder.decodePreMsg(packet);
//        assertEquals(preMsg, msg);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//        success = GolfCartEncoder.encodePreMsg(packet, preMsg);
//        assertTrue(success);
//        msg = GolfCartEncoder.decodePreMsg(packet);
//        assertEquals(preMsg, msg);
//
//        GolfCartEncoder.encodeGolfCartPosition(packet, 0, origin, cart);
//        GolfCartEncoder.encodeCartStatus(packet, appMode, status);
//        success = GolfCartEncoder.encodePreMsg(packet, preMsg);
//        assertTrue(success);
//        msg = GolfCartEncoder.decodePreMsg(packet);
//        assertEquals(preMsg, msg);
//    }
//
//    @Test
//    public void testGetOffset() {
//        System.out.println("getOffset");
//        assertEquals(0, GolfCartEncoder.getOffset(0, PacketType.LOCATION, false));
//        assertEquals(0, GolfCartEncoder.getOffset(0, PacketType.GAMESTAT, false));
//        assertEquals(0, GolfCartEncoder.getOffset(0, PacketType.CARTAHEAD, false));
//        assertEquals(0, GolfCartEncoder.getOffset(0, PacketType.STATUS, false));
//        assertEquals(0, GolfCartEncoder.getOffset(0, PacketType.PRE_MSG, false));
//
//        int offset = 0;
//        assertEquals(offset, GolfCartEncoder.getOffset(0x1f, PacketType.LOCATION, false));
//        offset += GolfCartEncoder.positionEnd;
//        assertEquals(offset, GolfCartEncoder.getOffset(0x1f, PacketType.GAMESTAT, false));
//        offset += GolfCartEncoder.gameStatusLength;
//        assertEquals(offset, GolfCartEncoder.getOffset(0x1f, PacketType.CARTAHEAD, false));
//        offset += 1;
//        assertEquals(offset, GolfCartEncoder.getOffset(0x1f, PacketType.STATUS, false));
//        offset += 2;
//        assertEquals(offset, GolfCartEncoder.getOffset(0x1f, PacketType.PRE_MSG, false));
//    }
//
//}
