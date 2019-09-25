/*
 * 
 */
package com.stayprime.golf.message;

import com.stayprime.comm.encoder.GolfObjectEncoder;
import com.stayprime.comm.encoder.PacketType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class FnbOrderPayload implements Payload {
    private int holeNumber;
    private int hutNumber;
    private List<FnbOrderItem> items;

    public FnbOrderPayload() {
    }

    public FnbOrderPayload(int holeNumber, int hutNumber, List<FnbOrderItem> orderItems) {
        this.holeNumber = holeNumber;
        this.hutNumber = hutNumber;

        items = new ArrayList<FnbOrderItem>(orderItems.size());
        for (FnbOrderItem item : orderItems) {
            items.add(item.clone());
        }
    }

    public int getHoleNumber() {
        return holeNumber;
    }

    public void setHoleNumber(int holeNumber) {
        this.holeNumber = holeNumber;
    }

    public int getHutNumber() {
        return hutNumber;
    }

    public void setHutNumber(int hutNumber) {
        this.hutNumber = hutNumber;
    }

    public List<FnbOrderItem> getItems() {
        return items;
    }

    public void setItems(List<FnbOrderItem> items) {
        this.items = new ArrayList<FnbOrderItem>(items);
    }

    public String getOrderString() {
        return GolfObjectEncoder.buildOrderString(items);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.FB_ORDER;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        return GolfObjectEncoder.encodeFBOrder(pack, offset, holeNumber, hutNumber, items);
    }

}
