/*
 *
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.model.golf.CartAppMode;
import com.stayprime.model.golf.GolfCart;
import com.stayprime.model.golf.HoleCartList;
import com.stayprime.model.golf.GolfRound;
import com.stayprime.golf.message.GolfCartListMessage;
import com.stayprime.model.golf.Position;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class GolfCartListEncoder {

    public static final int maxCartListSize = 10;
    public static final int maxRangerListSize = 180;

    public static boolean encodeCartAheadList(BytePacket packet, int siteId, Position origin, List<GolfCart> carts, int skipCartNumber) {
        return encodeCartList(packet, siteId, origin, carts, PacketType.CART_AHEAD_LIST, skipCartNumber);
    }

    public static boolean encodeCartList(BytePacket packet, int siteId, Position origin, List<GolfCart> carts, int skipCartNumber) {
        return encodeCartList(packet, siteId, origin, carts, PacketType.CART_LIST, skipCartNumber);
    }

    public static boolean encodeCartList(BytePacket packet, int siteId, Position origin, List<GolfCart> carts, PacketType packetType, int skipCartNumber) {
        //Maximum packet size is 512 bytes. Each GolfCart information is (-bytes):
        //cartNumber-1, type-1, position-4, playing hole-1, pace of play-1 = 8 bytes
        //Total possible 63: 8*63 = 504, 8 bytes free for the header, set to 50 for now

        boolean cartAheadList = packetType == PacketType.CART_AHEAD_LIST;
        boolean rangerList = packetType == PacketType.CART_LIST;

        if ((rangerList || cartAheadList) == false) {
            return false;
        }

        byte[] pack = packet.getPacket();
        int offset = 0;

        pack[offset] = (byte) siteId;
        offset++;

        pack[offset] = (byte) packetType.id;
        offset++;

        int cartCount = carts.size();
        int skipCount = 0;
        int cartCountOffset = offset + 1;

        //Old list: 2:cart number, 3:messageid, 4:packetcount, 5:hole, 6:timeout
        if (cartAheadList) {
            pack[offset++] = 0; //Cart number
            pack[offset++] = 0; //Message id

            EncodeUtil.encodePacketCount(packet.getPacket(), offset, 1, 1);
            offset++; //Packet count
            pack[offset++] = 0; //Hole
            pack[offset++] = 120/5; //Timeout

            cartCount = Math.min(cartCount, maxCartListSize);
        }
        else {
            EncodeUtil.encodePacketCount(packet.getPacket(), offset, 1, 1);
            offset++;

            cartCountOffset = offset;
            cartCount = EncodeUtil.encodeLength(pack, offset, carts.size(), maxRangerListSize);
            offset++;
        }

        for (int i = 0; i < cartCount; i++) {
            GolfCart cart = carts.get(i);

            Integer num = cart.getNumber();
            if (skipCartNumber > 0 && num != null && num == skipCartNumber) {
                skipCount++;
                continue;
            }
            if (rangerList) {
                //Cart number
                pack[offset] = num == null? 0 : num.byteValue();
                offset++;
                //Cart mode
                pack[offset] = (byte) CartAppMode.GOLF.id;
                offset++;
            }
            //Position
            EncodeUtil.encodeRelativePosition(pack, offset, origin, cart.getPosition());
            offset += 4;

            if (cartAheadList) {
                //Old cart ahead list heading
                EncodeUtil.encodeBearing(cart.getHeading(), pack, offset);
                offset++;
            }
            else {
                GolfRound round = cart.getGolfRound();
                EncodeUtil.encodeHoleNumber(pack, offset, round == null? 0 : round.getCurrentHole());
                offset++;

                EncodeUtil.encodePaceOfPlaySeconds(pack, offset, round == null? 0 : round.getPaceOfPlay());
                offset++;
            }
        }

        if (packetType == PacketType.CART_AHEAD_LIST) {
        }
        else if (packetType == PacketType.CART_LIST) {
            EncodeUtil.encodeLength(pack, cartCountOffset, cartCount - skipCount, maxRangerListSize);
        }

        packet.setLength(offset);
        return true;
    }

    public static GolfCartListMessage decodeCartList(BytePacket packet, Position origin) {
        //Maximum packet size is 512 bytes. Scores for 4 players for 18 holes = 72 bytes
        //Maximum e-mail address length is 254! Let's set it to 100 on this layer
        //The limit for the names is 40, longer will be trimmed
        //This leaves enough space to encode 6 rows of scores

        byte[] pack = packet.getPacket();
        int offset = 0;

        int siteId = pack[offset];
        offset++;

        int packetType = (byte) pack[offset];
        offset++;

        boolean cartAheadList = PacketType.CART_AHEAD_LIST.test(packetType);
        boolean rangerList = PacketType.CART_LIST.test(packetType);

        if ((rangerList || cartAheadList) == false) {
            return null;
        }

        GolfCartListMessage msg = new GolfCartListMessage();
        HoleCartList list = new HoleCartList();
        list.setTimestamp(System.currentTimeMillis());
        msg.setGolfCartList(list);

        int cartCount = 0;

        if (cartAheadList) {
            offset++; //Cart number
            offset++; //Message id

//            EncodeUtil.decodePacketCount(packet.getPacket(), offset, msg);
            offset++; //Packet count
            offset++; //Hole

            list.setTimeout(EncodeUtil.decodeUnsigned(pack, offset)*5);
            offset++; //Timeout
            cartCount = (packet.getLength() - offset) / 5;
        }
        else {
//            EncodeUtil.decodePacketCount(packet.getPacket(), offset, msg);
            offset++;

            cartCount = EncodeUtil.decodeUnsigned(pack, offset);
            offset++;
        }


        List<GolfCart> carts = list.getCarts();
        int cartLength = rangerList? 8 : 5;

        for (int i = 0; i < cartCount && offset + cartLength <= packet.getLength(); i++) {
            int cartNumber = 0;
            int cartType = 0;
            float heading = 0;
            int currentHole = 0;
            int paceOfPlay = 0;

            if (rangerList) {
                //Cart number
                cartNumber = EncodeUtil.decodeUnsigned(pack, offset);
                offset++;
                //Cart mode
                cartType = EncodeUtil.decodeUnsigned(pack, offset);
                offset++;
            }

            Position position = EncodeUtil.decodeRelativePosition(pack, offset, origin, null);
            offset += 4;

            GolfCart cart = new GolfCart();
            cart.setNumber(cartNumber);
            cart.setPosition(position);

            if (cartAheadList) {
                //Old cart ahead list heading
                cart.setHeading(EncodeUtil.decodeBearing(pack, offset));
                offset++;
            }
            else {
                currentHole = EncodeUtil.decodeHoleNumber(pack, offset);
                offset++;

                paceOfPlay = EncodeUtil.decodePaceOfPlaySeconds(pack, offset);
                offset++;
            }

            if (currentHole > 0) {
                GolfRound round = new GolfRound();
                round.setPaceOfPlay(paceOfPlay);
                round.setCurrentHole(currentHole);
                cart.setGolfRound(round);
            }

            carts.add(cart);
        }

        return msg;
    }
}
