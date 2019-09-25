/*
 *
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.model.golf.ScorecardModel;
import com.stayprime.golf.message.FnbOrderItem;
import com.stayprime.golf.message.FnbOrderPayload;
import com.stayprime.model.golf.WalkerScorecardModel;
import com.stayprime.tournament.model.Player;
import com.stayprime.tournament.model.PlayerScores;
import com.stayprime.tournament.model.WalkerPlayerScores;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class GolfObjectEncoder {

    public static boolean encodeScoreCard(BytePacket packet, int siteId, int sourceId, int messageId, List<PlayerScores> players, String emailAddress) {
        byte[] pack = packet.getPacket();
        int offset = 0;

        offset = EncodeUtil.encodeHeader(pack, offset, siteId, PacketType.SCORECARD_STAT.id, sourceId, messageId);
        offset = encodeScoreCard(pack, offset, players, emailAddress);
        packet.setLength(offset);

        return offset > 0;
    }

    public static int encodeScoreCard(byte[] pack, int offset, List<PlayerScores> playerScores, String emailAddress) {
        //Maximum packet size is 512 bytes. Scores for 4 players for 18 holes = 72 bytes
        //Maximum e-mail address length is 254! Let's set it to 100 on this layer
        //The limit for the names is 40, longer will be trimmed
        //This leaves enough space to encode 6 rows of scores

        if (playerScores.size() == 0 || emailAddress.length() > 100) {
            return 0;
        }

        int playerCount = EncodeUtil.encodeLength(pack, offset, playerScores.size(), 6);
        offset++;

        for (int i = 0; i < playerCount; i++) {
            PlayerScores scores = playerScores.get(i);
            Player player = scores.getPlayer();
            offset = EncodeUtil.encodeString(pack, offset, player.getName(), 40);

            int scoreCount = scores.getCount();

            //Maximum 27 holes plus 3 (out, in, total)
            int scoreLength = EncodeUtil.encodeLength(pack, offset, scoreCount, 27);
            offset++;

            //If there were more than 27 holes, trim it to 27
//            scoreCount = scoreLength - 3;
//            boolean shortScores = scoreCount < 9;
            for (int j = 0; j < scoreLength; j++) {
                pack[offset] = (byte) scores.getScore(j);
                offset++;

//                boolean lastScore = j == scoreCount - 1;
//                if (j == 9 || (shortScores && lastScore)) {
//                    pack[offset] = (byte) scores.getTotalOut();
//                    offset++;
//                }
//                if (lastScore) {
//                    pack[offset] = (byte) scores.getTotalIn();
//                    offset++;
//                    pack[offset] = (byte) scores.getTotal();
//                    offset++;
//                }
            }
        }

        pack[offset] = (byte) emailAddress.length();
        offset++;

        for (int i = 0; i < emailAddress.length(); i++) {
            pack[offset++] = (byte) emailAddress.charAt(i);
        }

        return offset;
    }

    public static Packet<ScorecardModel> decodeScoreCard(BytePacket packet) {
        //Maximum packet size is 512 bytes. Scores for 4 players for 18 holes = 72 bytes
        //Maximum e-mail address length is 254! Let's set it to 100 on this layer
        //The limit for the names is 40, longer will be trimmed
        //This leaves enough space to encode 6 rows of scores
        byte pack[] = packet.getPacket();
        int offset = 0;

        Packet<ScorecardModel> re = new Packet();
        offset = EncodeUtil.decodeHeader(pack, offset, re);

        if (!PacketType.SCORECARD_STAT.test(re.getPacketType())) {
            return null;
        }

        int playerCount = pack[offset];
        offset++;

        List<PlayerScores> playerScores = new ArrayList<PlayerScores>();
        for (int i = 0; i < playerCount; i++) {
            String name = EncodeUtil.decodeString(pack, offset, false);
            offset += name.length() + 1;

            int scoreLength = pack[offset];
            offset++;

            Player player = new Player(0, null, name);
            PlayerScores scores = new PlayerScores(scoreLength, player);

            for (int j = 0; j < scoreLength; j++) {
                scores.setScore(j, pack[offset]);
                offset++;
            }

            playerScores.add(scores);
        }

        int emailLength = pack[offset];
        offset++;
        StringBuilder email = new StringBuilder();
        for (int i = 0; i < emailLength; i++) {
            email.append((char) pack[offset]);
            offset++;
        }
//        ScoreCardModel model  = new ScoreCardModel(siteId, sourceId, messageId, rows, email.toString());        
        ScorecardModel model = new ScorecardModel(playerScores, email.toString());
        re.setPayload(model);
        return re;
    }

    public static boolean encodeFBOrder(BytePacket packet, int siteId, int sourceId, int messageId, int hole, int hut, List<FnbOrderItem> orderItems) {
        byte[] pack = packet.getPacket();
        int offset = 0;

        offset = EncodeUtil.encodeHeader(pack, offset, siteId, PacketType.FB_ORDER.id, sourceId, messageId);
        offset = encodeFBOrder(pack, offset, hole, hut, orderItems);
        packet.setLength(offset);

        return offset > 0;
    }

    public static int encodeFBOrder(byte[] pack, int offset, int hole, int hut, List<FnbOrderItem> orderItems) {
        //Menu selected by customer
        String orderString = buildOrderString(orderItems);
        return encodeFBOrder(pack, offset, hole, hut, orderString);
    }

    public static int encodeFBOrder(byte[] pack, int offset, int hole, int hut, String orderDetail) {
        EncodeUtil.encodePacketCount(pack, offset, 1, 1);
        offset++;
        pack[offset] = (byte) hole;
        offset++;
        pack[offset] = (byte) hut;
        offset++;

        offset += EncodeUtil.encodeString(pack, offset, orderDetail, EncodeUtil.maxPacketLength - 4, false);

        return offset;
    }

    public static Packet<FnbOrderPayload> decodeFBOrder(BytePacket packet) {
        byte pack[] = packet.getPacket();
        int offset = 0;

        Packet<FnbOrderPayload> re = new Packet();
        offset = EncodeUtil.decodeHeader(pack, offset, re);

        if (!PacketType.FB_ORDER.test(re.getPacketType())) {
            return null;
        }

        FnbOrderPayload fnbOrder = new FnbOrderPayload();

//        EncodeUtil.decodePacketCount(packet.getPacket(), offset, fnbOrder);
        offset++;

        fnbOrder.setHoleNumber(EncodeUtil.decodeUnsigned(pack, offset));
        offset++;

        fnbOrder.setHutNumber(EncodeUtil.decodeUnsigned(pack, offset));
        offset++;

        //Menu selected by customer
        String orderString = EncodeUtil.decodeString(pack, offset, false);
        offset += orderString.length() + 1;
        fnbOrder.setItems(parseOrderString(orderString));

        re.setPayload(fnbOrder);
        return re;
    }

    public static String buildOrderString(List<FnbOrderItem> orderItems) {
        StringBuilder orderDetail = new StringBuilder();
        if (orderItems != null) {
            for (FnbOrderItem item : orderItems) {
                orderDetail.append(item.getQuantity()).append(';');
                String code = item.getCode() != null && item.getCode().length() > 10?
                        item.getCode().substring(0, 10) : item.getCode();
                orderDetail.append(code).append(';');
                orderDetail.append(item.getPrice()) .append(',');
            }
        }
        return orderDetail.toString();
    }

    public static List<FnbOrderItem> parseOrderString(String orderString) {
        if (StringUtils.isNotEmpty(orderString)) {

            String[] itemLines = StringUtils.split(orderString, ',');
            List<FnbOrderItem> items = new ArrayList<FnbOrderItem>(itemLines.length);

            for (String line : itemLines) {
                String[] fields = StringUtils.split(line, ';');
                if (fields.length >= 2) {
                    int quantity = NumberUtils.toInt(fields[0]);
                    String code = fields[1];
                    float price = NumberUtils.toFloat(fields[2]);

                    FnbOrderItem item = new FnbOrderItem(quantity, code, null, null, price);
                    items.add(item);
                }
            }
            return items;
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }
    
     public static int encodeWalkerScorecard(byte[] pack, int offset, List<WalkerPlayerScores> playerScores, String emailAddress) {
        //Maximum packet size is 512 bytes. Scores for 4 players for 18 holes = 72 bytes
        //Maximum e-mail address length is 254! Let's set it to 100 on this layer
        //The limit for the names is 40, longer will be trimmed
        //This leaves enough space to encode 6 rows of scores

        if (playerScores.isEmpty() || emailAddress.length() > 100) {
            return 0;
        }

        int playerCount = EncodeUtil.encodeLength(pack, offset, playerScores.size(), 4);
        offset++;

        for (int i = 0; i < playerCount; i++) {
            WalkerPlayerScores scores = playerScores.get(i);
            Player player = scores.getPlayer();
            offset = EncodeUtil.encodeString(pack, offset, player.getName(), 40);

            int scoreCount = scores.getCount();

            //Maximum 27 holes plus 3 (out, in, total)
            int scoreLength = EncodeUtil.encodeLength(pack, offset, scoreCount, 27);
            offset++;

            //If there were more than 27 holes, trim it to 27
//            scoreCount = scoreLength - 3;
//            boolean shortScores = scoreCount < 9;
            for (int j = 0; j < scoreLength; j++) {
                pack[offset++] = (byte) scores.getScore(j);
                pack[offset++] = (byte) scores.getPutts(j);
                pack[offset++] = (byte) scores.getDrive(j);
                pack[offset++] = (byte) scores.getClub(j);
                pack[offset++] = (byte) scores.getChips(j);
                pack[offset++] = (byte) scores.getSandShots(j);
                pack[offset++] = (byte) scores.getSaves(j);
                pack[offset++] = (byte) scores.getPenalties(j);
            }
        }

        pack[offset] = (byte) emailAddress.length();
        offset++;

        for (int i = 0; i < emailAddress.length(); i++) {
            pack[offset++] = (byte) emailAddress.charAt(i);
        }

        return offset;
    }
     
    public static Packet<WalkerScorecardModel> decodeWalkerScorecard(BytePacket packet) {
        //Maximum packet size is 512 bytes. Scores for 4 players for 18 holes = 72 bytes
        //Maximum e-mail address length is 254! Let's set it to 100 on this layer
        //The limit for the names is 40, longer will be trimmed
        //This leaves enough space to encode 6 rows of scores
        byte pack[] = packet.getPacket();
        int offset = 0;

        Packet<WalkerScorecardModel> re = new Packet();
        offset = EncodeUtil.decodeHeader(pack, offset, re);

        if (!PacketType.WALKER_SCORECARD_STAT.test(re.getPacketType())) {
            return null;
        }

        int playerCount = pack[offset];
        offset++;

        List<WalkerPlayerScores> playerScores = new ArrayList<WalkerPlayerScores>();
        for (int i = 0; i < playerCount; i++) {
            String name = EncodeUtil.decodeString(pack, offset, false);
            offset += name.length() + 1;

            int scoreLength = pack[offset];
            offset++;

            Player player = new Player(0, null, name);
            WalkerPlayerScores scores = new WalkerPlayerScores(scoreLength, player);

            for (int j = 0; j < scoreLength; j++) {
                scores.setScore(j, pack[offset++]);
                scores.setPutts(j, pack[offset++]);
                scores.setDrive(j, pack[offset++]);
                scores.setClub(j, pack[offset++]);
                scores.setChips(j, pack[offset++]);
                scores.setSandShots(j, pack[offset++]);
                scores.setSaves(j, pack[offset++]);
                scores.setPenalties(j, pack[offset++]);
            }

            playerScores.add(scores);
        }

        int emailLength = pack[offset];
        offset++;
        StringBuilder email = new StringBuilder();
        for (int i = 0; i < emailLength; i++) {
            email.append((char) pack[offset]);
            offset++;
        }
//        ScoreCardModel model  = new ScoreCardModel(siteId, sourceId, messageId, rows, email.toString());        
        WalkerScorecardModel model = new WalkerScorecardModel(playerScores, email.toString());
        re.setPayload(model);
        return re;
    }

}
