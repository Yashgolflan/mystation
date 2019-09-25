/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.geo.Coordinates;
import com.stayprime.golf.course.CoursePinLocationsInfoDTO;
import com.stayprime.golf.course.PinLocationDTO;
import com.stayprime.model.golf.Position;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class is used to encode the pinLocations list and send it to the carts.
 * It also contains the decoding logic of the pinLocation from the BytePacket
 * @author sarthak
 */
public class PinLocationListEncoder {
    
    public static boolean encodePinLocationsPacket(Encoder e, String lastUpdateTime, List<PinLocationDTO> pinLocations, Position origin) {
    
        // first, encode the lastUpdateTime, encoding as string temporarily, will fix this
        e.encodeString(lastUpdateTime, false);
        
        // encode the number of pinLocations
        e.encodeByte(pinLocations.size());
        
        for (PinLocationDTO loc: pinLocations) {
            // encode courseId
            e.encodeByte(loc.getCourseId());
            // encode holeId
            e.encodeByte(loc.getHoleId());
            // encode position
            Coordinates c = loc.getCoordinate();
            e.encodePositionRelativeToOrigin(origin, Position.get(c.latitude, c.longitude));            
        } 
        
        
        return true;
    }
    
    
    
    public static Packet<CoursePinLocationsInfoDTO> decodePinLocationsPacket(BytePacket packet, Position origin) {
        Packet<CoursePinLocationsInfoDTO> re = new Packet<CoursePinLocationsInfoDTO>();
        Encoder e = new Encoder(packet.getPacket(), 0);
        e.decodeHeader(re, PacketType.PIN_LOCATIONS);
        
        // decode the lastUpdateTime
        String lastUpdateTime = e.decodeString(false);
        
        // set number of pins
        int numberOfPins = e.decodeUnsigned();
        
        List<PinLocationDTO> pinLocations = new ArrayList<PinLocationDTO>();
        for (int i = 0; i < numberOfPins; i++) {
            int courseId = e.decodeUnsigned();
            int holeId = e.decodeUnsigned();
            Position pos = e.decodePositionRelativeToOrigin(origin);
            
            Coordinates c = pos == null ? null :
                                    new Coordinates(pos.getY(), pos.getX());
            
            pinLocations.add(new PinLocationDTO(holeId, courseId, c));
        }
        
        CoursePinLocationsInfoDTO coursePinLocationsInfoDTO = new CoursePinLocationsInfoDTO();
        coursePinLocationsInfoDTO.setLastUpdateTime(lastUpdateTime);
        coursePinLocationsInfoDTO.setPinLocations(pinLocations);
        re.setPayload(coursePinLocationsInfoDTO);
        
        return re;
    }
    
    /**
     * This method encodes the pinLocations in the byte packet
     * @param packet
     * @param siteId
     * @param packetType
     * @param origin
     * @param coursePinLocationsInfoDTO
     * @param lastUpdatedDateAsString
     * @return 
     */
    public static boolean encodePinLocations(BytePacket packet, int siteId, int packetType, Position origin,
                                            CoursePinLocationsInfoDTO coursePinLocationsInfoDTO, String lastUpdatedDateAsString) {
        
       try {
            // get packet i.e. byte array
            byte[] pack = packet.getPacket();
            // initialize offset variable for byte array
            int offset = 0;

            ////SUGGESTION: use EncodeUtil.encodeHeader, it needs two more parameters:
            ////cartNo (the cart can check the packet is addressed to it)
            ////and msgCt (for stats and indication of packet loss)
            ////and for consistency since every packet has these 4 fields:
            //EncodeUtil.encodeHeader(pack, 0, siteId, packetType, cartNo, msgCtr);
            ////msgCt could be 0 or some more meaningful data in this case.
            ////
            ////Also please look at a simpler and more readable way of encoding
            ////and decoding in TournamentEncoder.encodeRoundAndPlayers and
            ////decodeRoundAndPlayers, using the Encoder class.
            ////
            ////Also CoursePinLocationsInfoDTO should implement Payload so that
            ////we can represent it like Packet<Payload> and use RequestHandler.
            ////Please look at the implementations of Payload and its uses in
            ////RequestHandler and also CoursePinLocationsInfoDTO could be moved
            ////into some packet under com.stayprime.comm.encoder.
            ////For example look into TournamentInfo (implements Payload).
            ////For it to work properly, the Payload.encode method doesn't encode
            ////the header, only the payload, in this case it would be:
            ////pinLocations.size, updatedTime, [pin1, pin2, ...]
            ////
            ////I also suggest updatedTime is encoded as long instead of String
            ////(Java's internal way to represent a time instant for that we need
            ////to create Encoder/EncodeUtil.encodeLong) and that it is the first
            ////item in the packet's payload (as it was initially),
            ////Then count of pin locations then the list of pin locations,
            ////this is a more natural way of encoding an array: length then items.

            // first encode the header
            // set siteId at 0th index
            pack[offset] = (byte) siteId;
            offset++;

            // set packetType at 1st index
            pack[offset] = (byte) packetType;
            offset++;

            // set total number of pins at 2nd index
            pack[offset] = (byte) coursePinLocationsInfoDTO.getPinLocations().size();
            offset++;

            // set the pinUpdatedTime
            offset = EncodeUtil.encodeString(pack, offset, lastUpdatedDateAsString, false);

            // encode the payload
            for (PinLocationDTO loc: coursePinLocationsInfoDTO.getPinLocations()) {
                // encode holeId
                pack[offset] = (byte) loc.getHoleId();
                offset++;

                // encode courseId
                pack[offset] = (byte) loc.getCourseId();
                offset++;

                // use EncodeUtil to encode the position of pin w.r.t the origin of the site i.e. topLeftCorner
                Coordinates c = loc.getCoordinate();
                EncodeUtil.encodeRelativePosition(pack, offset, origin, Position.get(c.latitude, c.longitude));
                offset += 4;
            }

            // set packet length
            packet.setLength(offset);

            // return true, everything OK!
            return true;        

       } catch(Exception e) {
           return false;
       }
    }
   
    
    
    /**
     * This method decodes the packet to get pinLocations
     * @param packet
     * @param origin
     * @return 
     */
    public static CoursePinLocationsInfoDTO decodePinLocations(BytePacket packet, Position origin) {
        
        // get packet i.e. byte array
        byte[] pack = packet.getPacket();
        // initialize offset variable for byte array
        int offset = 0;
        
        // first decode the header
        
        // get siteId from 0th index of byte packet
        int siteId = pack[offset];
        offset++;
      
        // get packetId from 1st index of byte packet
        int packetType = (byte) pack[offset];
        offset++;
        
        // get number of pins from 2nd index of byte packet 
        int pinCount = pack[offset];
        offset++;
                
        // create the pinlocationProfile object
        CoursePinLocationsInfoDTO coursePinLocationsInfoDTO = new CoursePinLocationsInfoDTO();
        
        // get last updated date as string
        coursePinLocationsInfoDTO.setLastUpdateTime(EncodeUtil.decodeString(pack, offset, false));
        offset += coursePinLocationsInfoDTO.getLastUpdateTime().length() + 1;
        
        List<PinLocationDTO> pinLocationsDTO = coursePinLocationsInfoDTO.getPinLocations();
        
        for(int i = 0; i < pinCount; i++) {
            try {
                // get holeId
                int holeId = pack[offset];
                offset++;
                
                // get courseId
                int courseId = pack[offset];
                offset++;
                
                Position position = EncodeUtil.decodeRelativePosition(pack, offset, origin, null);
                offset += 4;
                
                Coordinates c = position == null ? new Coordinates(Double.MIN_VALUE, Double.MIN_VALUE) :
                                                    new Coordinates(position.getY(), position.getX()); 

                // add the pinLocationDTO object to list
                pinLocationsDTO.add(new PinLocationDTO(holeId, courseId, c));
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        
        return coursePinLocationsInfoDTO;
    }
    
    
}
