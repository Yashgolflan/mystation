package com.stayprime.model.golf;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

//@Entity
public class GolfCart {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int siteId;

    private String label;

    private int number;

    //Cart type: golf, golf-hcp, ranger, fnb, transport
    private String cartMode;

    private Position position;

    private double heading;

    private double speed;

    private DateTime positionTime;

    private int batteryLevel;
    
    private int deviceType;

    @ManyToOne
    private GolfRound golfRound;

    public GolfCart() {
    }

    public GolfCart(int siteId, String label, int number) {
        this.siteId = siteId;
        this.label = label;
        this.number = number;
    }
   
    /*
     * Accessors
     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCartMode() {
        return cartMode;
    }

    public void setCartMode(String cartMode) {
        this.cartMode = cartMode;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setPosition(double lon, double lat) {
//        if (position != null) {
//            position.setPosition();
//        }
//        else {
//            position = new Position(lon, lat);
//        }
        this.position = new Position(lon, lat);
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

    public DateTime getPositionTime() {
        return positionTime;
    }

    public void setPositionTime(DateTime positionTime) {
        this.positionTime = positionTime;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public GolfRound getGolfRound() {
        return golfRound;
    }

    public void setGolfRound(GolfRound golfRound) {
        this.golfRound = golfRound;
    }

    /*
     * Overriden methods
     */

    @Override
    public String toString() {
        return "GolfCart[" + siteId
                + "," + label
                + "," + position
                + "," + heading
                + "," + batteryLevel
                + "," + deviceType
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GolfCart)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        GolfCart c = (GolfCart) obj;
        return new EqualsBuilder()
                .append(siteId, c.siteId)
                .append(label, c.label)
                .append(position, c.position)
                .append(heading, c.heading)
//                .append(golfRound, c.golfRound)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 59)
                .append(siteId)
                .append(label)
                .append(position)
                .append(heading)
//                .append(golfRound)
                .toHashCode();
    }

}
