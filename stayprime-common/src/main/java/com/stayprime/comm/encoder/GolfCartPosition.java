/*
 * 
 */
package com.stayprime.comm.encoder;

import com.stayprime.golf.message.Payload;
import com.stayprime.model.golf.Position;

/**
 *
 * @author benjamin
 */
public class GolfCartPosition implements Payload {

    private Integer siteId;

    private Integer cartNumber;

    private Position position;

    private double heading;

    private double speed;

    public GolfCartPosition(Integer siteId, Integer cartNumber, Position position, double heading) {
        this.siteId = siteId;
        this.cartNumber = cartNumber;
        this.position = position;
        this.heading = heading;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(Integer cartNumber) {
        this.cartNumber = cartNumber;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.LOCATION;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        return 0;
    }

}
