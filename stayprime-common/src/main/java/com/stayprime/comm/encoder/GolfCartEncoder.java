/*
 *
 */
package com.stayprime.comm.encoder;

import com.stayprime.cartapp.CartAppConst;
import com.stayprime.comm.BytePacket;
import com.stayprime.model.golf.GolfCart;
import com.stayprime.model.golf.GolfRound;
import com.stayprime.model.golf.Position;

/**
 *
 * @author benjamin
 */
public class GolfCartEncoder {
    public static final int positionEnd = PacketType.headerLength + PacketType.locationLength;
    public static final int walkerPositionEnd = PacketType.headerLength + PacketType.walkerLocationLength;
    public static final int gameStatusLength = 2;

    /**
     * Encode a GolfCart's position relative to origin and heading.
     * This is the packet that will send the tracking information to the server.
     * @param packet buffer for the encoded packet.
     * @param messageCounter sequential message counter.
     * @param origin the origin for the relative position.
     * @param cart golf cart to encode, including siteId, position and heading.
     * @return true if the parameters were valid and the encoding was successful.
     */
    public static boolean encodeGolfCartPosition(BytePacket packet, int messageCounter, Position origin, GolfCart cart) {
        //Fail if golfCart is null or the number is not valid for encoding
        if (cart == null) {
            return false;
        }
        int cartNumber = cart.getNumber();
        if (cartNumber == 0 || EncodeUtil.isValidUnsignedByte(cartNumber) == false) {
            return false;
        }

        byte[] pack = packet.getPacket();
        int offset = 0;

        int siteId = cart.getSiteId();
        int packetType = PacketType.LOCATION.id;

        offset = EncodeUtil.encodeHeader(pack, offset, siteId, packetType, cartNumber, messageCounter);

        offset = encodeGolfCartPosition(pack, offset, origin, cart);
        assert offset == positionEnd;
        packet.setLength(offset);

        return true;
    }

    private static int encodeGolfCartPosition(byte[] pack, int offset, Position origin, GolfCart cart) {
        //Encode the position in 4 bytes
        EncodeUtil.encodeRelativePosition(pack, offset, origin, cart.getPosition());
        offset += 4;

        //Encode the bearing in 1 byte
        EncodeUtil.encodeBearing(cart.getHeading(), pack, offset);
        offset++;
        return offset;
    }

    /**
     * Decode a GolfCart's position relative to origin and heading.
     * @param packet buffer packet to decode.
     * @param origin the origin for the relative position.
     * @param golfCart the destination GolfCart object, or create a new object if null.
     * @param isWalkerPacket If packet is walker of walker device site.
     * @return the decoded GolfCart if decoding was successful, null otherwise.
     */
    public static GolfCart decodeGolfCartPosition(BytePacket packet, Position origin, GolfCart golfCart) {
        GolfCart cart = golfCart;
        byte[] pack = packet.getPacket();
        int offset = 0;

        int siteId = EncodeUtil.decodeUnsigned(pack, offset);
        offset++;

        //Check if the packet is the right type
        if (PacketType.LOCATION.test(pack[offset]) == false) {
            return null;
        }
        offset++;

        int cartNumber = EncodeUtil.decodeUnsigned(pack, offset);
        offset++;

        if (cart == null) {
            //Create an new GolfCart object if null
            cart = new GolfCart();
            cart.setNumber(cartNumber);
            cart.setSiteId(siteId);
        }
        else {
            //Set the cart number if not null
            cart.setNumber(cartNumber);
            cart.setSiteId(siteId);
        }

        int messageCounter = pack[offset];
        offset++;

        //Decode the 4 byte position and set it on the GolfCart
        Position pos = cart.getPosition();
        pos = EncodeUtil.decodeRelativePosition(pack, offset, origin, pos);
        cart.setPosition(pos);
        offset += 4;

        //Decode 1 byte bearing and set it on the GolfCart
        float bearing = EncodeUtil.decodeBearing(pack, offset);
        cart.setHeading(bearing);
        offset++;    

        return cart;
    }
    
    /**
     * Encode the game status: current hole number and current pace of play.
     * This must be called after encodeCartPosition.
     * @param packet buffer for the encoded packet.
     * @param playingHoleNumber current hole number.
     * @param paceOfPlaySeconds current pace of play.
     * @return true if the parameters were valid and the encoding was successful.
     */
    public static boolean encodeGameStatus(BytePacket packet, int playingHoleNumber, int paceOfPlaySeconds) {
        byte[] pack = packet.getPacket();
        int offset = 1;

        //LOCATION packet needs to be encoded first.
        if (PacketType.LOCATION.test(pack[offset]) == false) {
            return false;
        }

        //Add GAMESTAT to the packet type and skip to the end of the LOCATION
        pack[offset] |= PacketType.GAMESTAT.id;
        offset = positionEnd;

        EncodeUtil.encodeHoleNumber(pack, offset, playingHoleNumber);
        offset++;

        if (playingHoleNumber > 0) {
            //Only encode pace of play if playing hole is not zero (null)
            EncodeUtil.encodePaceOfPlaySeconds(pack, offset, paceOfPlaySeconds);
        }
        offset++;

        packet.setLength(offset);
        return true;
    }

    /**
     * Decode the game status: current hole number and current pace of play.
     * This method assumes that the packet type has already been checked to be
     * of type <code>PacketType.LOCATION</code>
     * @param packet buffer for the encoded packet.
     * @param golfRound the destination GolfRound object, will create a new object if null.
     * @param isWalkerPacket  whether the packet is from a walker site
     * @return the decoded GolfRound if decoding was successful, null otherwise.
     */
    public static GolfRound decodeGameStatus(BytePacket packet, GolfRound golfRound) {
        GolfRound round = golfRound;
        byte[] pack = packet.getPacket();
        int offset = 1;

        //Check if the packet is the right type
        if (PacketType.GAMESTAT.testIncluded(pack[offset]) == false) {
            return null;
        }

        if (round == null) {
            //Create an new GolfRound object if null
            round = new GolfRound();
        }

        offset = positionEnd;

        round.setCurrentHole(EncodeUtil.decodeHoleNumber(pack, offset));
        offset++;

        if (round.getCurrentHole() > 0) {
            //Only decode pace of play if playing hole is not zero (null)
//            round.setStarted(true);
            round.setPaceOfPlay(EncodeUtil.decodePaceOfPlaySeconds(pack, offset));
        }
        else {
//            round.setStarted(false);
            round.setPaceOfPlay(0);
        }
        offset++;

        return round;
    }

    public static boolean encodeCartAheadRequest(BytePacket packet, int holeNumber) {
        byte[] pack = packet.getPacket();
        int offset = 1;

        //Check that GAMESTAT has been already encoded
        if (PacketType.GAMESTAT.testIncluded(pack[offset]) == false) {
            return false;
        }

        //Add CARTAHEAD to the packet and skip to the end of GAMESTAT
        pack[offset] |= PacketType.CARTAHEAD.id;
//        offset = positionEnd + gameStatusLength;

//        //Check the hole number to see if the pace of play was encoded
//        int currentHole = EncodeUtil.decodeHoleNumber(pack, offset);
//        boolean paceOfPlayEncoded = currentHole != 0;
//        offset += paceOfPlayEncoded? 2 : 1;
        offset = getOffset(pack[offset], PacketType.CARTAHEAD);

        //Encode the requested hole number's cart ahead list
        EncodeUtil.encodeHoleNumber(pack, offset, holeNumber);
        offset++;

        packet.setLength(offset);
        return true;
    }

    public static int decodeCartAheadRequest(BytePacket packet) {
        byte[] pack = packet.getPacket();
        int offset = 1;

        //Check if the packet is the right type
        if (PacketType.CARTAHEAD.testIncluded(pack[offset]) == false) {
            return 0;
        }

        //Skip to the end of the LOCATION+GAMESTAT
        offset = getOffset(pack[offset], PacketType.CARTAHEAD);

//        //Check the hole number to see if the pace of play was encoded
//        int currentHole = EncodeUtil.decodeHoleNumber(pack, offset);
//        boolean paceOfPlayEncoded = currentHole != 0;
//        offset += paceOfPlayEncoded? 2 : 1;

        //Decode the requested hole number
        return EncodeUtil.decodeHoleNumber(pack, offset);
    }

    public static boolean encodeCartStatus(BytePacket packet, int appMode, int status) {
        byte[] pack = packet.getPacket();
        int offset = 1;

        //Check that the location has been encoded
        if (PacketType.LOCATION.test(pack[offset]) == false) {
            return false;
        }

        pack[offset] |= PacketType.STATUS.id;

        offset = getOffset(pack[offset], PacketType.STATUS);

        pack[offset] = (byte) appMode;
        offset++;
        pack[offset] = (byte) status;
        offset++;

        packet.setLength(offset);
        return true;
    }

    public static int decodeCartMode(BytePacket packet) {
        byte[] pack = packet.getPacket();
        int offset = 1;

        //Check that the status has been encoded
        if (PacketType.STATUS.testIncluded(pack[offset]) == false) {
            return 0;
        }

        offset = getOffset(pack[offset], PacketType.STATUS);
        return pack[offset];
    }

    public static int decodeCartStatus(BytePacket packet) {
        byte[] pack = packet.getPacket();
        int offset = 1;

        //Check that the status has been encoded
        if (PacketType.STATUS.testIncluded(pack[offset]) == false) {
            return 0;
        }

        offset = getOffset(pack[offset], PacketType.STATUS) + 1;
        return pack[offset];
    }
    
    public static void decodeBatteryLevelAndDeviceType(BytePacket packet, GolfCart gc) {
        byte[] pack = packet.getPacket();
        int offset = 1, batteryLevel = -1, deviceType = CartAppConst.DEVICE_TYPE_NOT_SHARED;
        offset = getOffset(pack[offset], PacketType.STATUS) + 1 + 1;
        try {
            if (packet.getLength() > offset) {
                batteryLevel = (int) pack[offset];
                offset++;
                deviceType = (int) pack[offset];  
                System.out.println("BatteryLevel - " + batteryLevel + " and DeviceType - " + deviceType);
            }
        } catch(Exception ex) {
            System.out.println("Exception while decoding Battery Level and Device Type, packet length is " + 
                                packet.getLength() + " and offset is + " + offset);
            System.out.println(ex.toString());
            batteryLevel = -1;
        }        
        gc.setBatteryLevel(batteryLevel);
        gc.setDeviceType(deviceType);
    }

//    public static int encodeAppMode(int appMode) {
//        int status = appMode & PacketType.STATUS_APPMODE_MASK;
//        return status;
//    }

//    public static int decodeAppMode(int status) {
//        return status & PacketType.STATUS_APPMODE_MASK;
//    }

    public static boolean encodePreMsg(BytePacket packet, int messageCode) {
        byte[] pack = packet.getPacket();
        int offset = 1;

        //Check that the location has been encoded
        if (PacketType.LOCATION.test(pack[offset]) == false) {
            return false;
        }

        pack[offset] |= PacketType.PRE_MSG.id;

        offset = getOffset(pack[offset], PacketType.PRE_MSG);

        pack[offset] = (byte) messageCode;

        boolean success = true;
        offset++;

        if (success) {
            packet.setLength(offset);
        }

        return success;
    }

    public static int decodePreMsg(BytePacket packet) {
        byte[] pack = packet.getPacket();
        int offset = 1;

        //Check that the status has been encoded
        if (PacketType.PRE_MSG.testIncluded(pack[offset]) == false) {
            return 0;
        }

        offset = getOffset(pack[offset], PacketType.PRE_MSG);
        return pack[offset];
    }

    public static boolean isGolfCartPositionType(byte t) {
        return PacketType.LOCATION.test(t);
    }

    public static int getOffset(int t, PacketType p) {
        if (PacketType.LOCATION.test(t) == false || p == PacketType.LOCATION) {
            return 0;
        }

        int offset = positionEnd;        
        
        offset += PacketType.GAMESTAT.testIncluded(t) == false || p.id <= PacketType.GAMESTAT.id? 0 : gameStatusLength;
        offset += PacketType.CARTAHEAD.testIncluded(t) == false || p.id <= PacketType.CARTAHEAD.id? 0 : 1;
        offset += PacketType.STATUS.testIncluded(t) == false || p.id <= PacketType.STATUS.id? 0 : 2;
        offset += PacketType.PRE_MSG.testIncluded(t) == false || p.id <= PacketType.PRE_MSG.id? 0 : 1;
        return offset;
    }

    public static boolean encodeGolfCartPosition(BytePacket packet, int messageCounter, Position origin, GolfCartPosition cart) {
        //Fail if golfCart is null or the number is not valid for encoding
        if (cart == null || cart.getCartNumber() == null) {
            return false;
        }
        int cartNumber = cart.getCartNumber();
        if (cartNumber == 0 || EncodeUtil.isValidUnsignedByte(cartNumber) == false) {
            return false;
        }

        byte[] pack = packet.getPacket();
        int offset = 0;

        int siteId = cart.getSiteId() == null? 0 : cart.getSiteId().byteValue();
        int packetType = PacketType.LOCATION.id;

        offset = EncodeUtil.encodeHeader(pack, offset, siteId, packetType, cartNumber, messageCounter);

        offset = encodeGolfCartPosition(pack, offset, origin, cart);
        assert offset == positionEnd;
        packet.setLength(offset);

        return true;
    }

    private static int encodeGolfCartPosition(byte[] pack, int offset, Position origin, GolfCartPosition cart) {
        //Encode the position in 4 bytes
        EncodeUtil.encodeRelativePosition(pack, offset, origin, cart.getPosition());
        offset += 4;

        //Encode the bearing in 1 byte
        EncodeUtil.encodeBearing(cart.getHeading(), pack, offset);
        offset++;
        return offset;
    }

}