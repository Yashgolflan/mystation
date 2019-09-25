/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.golf.course;

import com.stayprime.comm.encoder.Encoder;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.comm.encoder.PinLocationListEncoder;
import com.stayprime.golf.message.Payload;
import com.stayprime.model.golf.Position;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.math.NumberUtils;

/**
 * This is the course pinLocations data transfer object.
 * It contains all the info for the cartApp to build the 
 * pinLocation xml file.
 * It contains a list of PinLocationDTO objects, lastUpdatedTime.
 * @author sarthak
 */
public class CoursePinLocationsInfoDTO implements Payload {
    
    // last time update timestamp of the pins
    private String lastUpdateTime;
    
    // list of pinLocationDTOs
    private List<PinLocationDTO> pinLocations;
    
    // Origin position, not encoded in packet, used only for sending
    Position origin;
    
    public CoursePinLocationsInfoDTO() {
        pinLocations = new ArrayList();
    }
    
    public CoursePinLocationsInfoDTO(String lastUpdatedTime, List<PinLocationDTO> pinLocations) {
        this.lastUpdateTime = lastUpdatedTime;
        pinLocations = new ArrayList<PinLocationDTO>();
        for (PinLocationDTO p: pinLocations) {
            // making a deep copy
            pinLocations.add(new PinLocationDTO(p));
        }
    }
        
    public CoursePinLocationsInfoDTO(String lastUpdateTime) {
        this.lastUpdateTime = this.lastUpdateTime;
    }
    
    
    // get number of pins of course with id
    public int getNumberOfPinsForCourse(int id) {
        int numberOfPins = 0;
        for (PinLocationDTO p: pinLocations) {
            if (p.getCourseId() == id) {
                numberOfPins++;
            }
        }
        return numberOfPins;
    }

    
    // standard getters and setters
    
    
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<PinLocationDTO> getPinLocations() {
        return pinLocations;
    }

    public void setPinLocations(List<PinLocationDTO> pinLocations) {
        this.pinLocations = pinLocations;
    }
    
    public Position getOrigin() {
        return origin;
    }
    
    public void setOrigin(Position origin) {
        this.origin = origin;
    }
    
    public static Date getDateFromTimestampAsString(String s) {
        long ts = NumberUtils.toLong(s);
        return ts > 0 ? new Date(ts) : null;
    }
    
    // implement methods of Payload

    @Override
    public PacketType getPacketType() {
        return PacketType.PIN_LOCATIONS;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        Encoder e = new Encoder(pack, offset);
        PinLocationListEncoder.encodePinLocationsPacket(e, lastUpdateTime, pinLocations, origin);
        return e.getOffset();
    }
    
    
}
