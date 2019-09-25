/*
 *
 */
package com.stayprime.comm.encoder;

/**
 *
 * @author benjamin
 */
public enum PacketType {
    //All messages start with Site ID (1b), Message type (1b)
    //Cart messages follow with cart ID (1b)

    //Cart location and status
    NULL(0),
    LOCATION(0x10, 0xF0), //Short location (2 bytes per axis) (relative to the course coordinates) + bearing (1b)
    GAMESTAT(0x01), //Playing hole number 0 to 255, if hole > 0 pace of play -127 to 128 minutes (1b)
    CARTAHEAD(0x02), //Request cart ahead for hole number 1 to 255
    STATUS(0x04), //Bit based cart status report (2b)
    PRE_MSG(0x08), //Predefined message code (1b)

    FB_ORDER(0x20), //Food and beverage order (format to be defined)
    ROUNDS_STAT(0x21),

    //Request to send SMS: 1 byte number length, 1 byte per number digit,
    //1 byte message length (max 140), max 140 bytes SMS payload.
    COMM_SMS(0x30),

    SCORECARD_STAT(0x40),
    WALKER_SCORECARD_STAT(0x41),

    TMT_SCORES(0x51),           //tournament_id(string), round_number (1b), scores_count(1b), scores:[playerId(string),revision(2b),scoreLength(1b),scores(0-18b)]
    TMT_LEADERBOARDFORMAT(0x54),//tournament_id(string), column_definitions
    TMT_LEADERBOARD(0x55),      //tournament_id(string), leaderboard_list
    TMT_REQUEST(0x56),          //tournament_id(string), Request tournment updated information
    TMT_ROUND_PLAYERS(0x57),     //
    //Cart ahead list: cart number (1b), list_id (1b), current/total (1b, max 16/16), list expiry (1b*5sec)
    //Plus max 50? cart per packet short location (2 bytes per axis + 1b heading)
    //[SITE] [TYPE] [CART] [LIST_ID] [INDEX/TOTAL] [HOLE?] [TIMEOUT?] [CART1]..[CART50]
    CART_AHEAD_LIST(0xf0),
    CART_LIST(0xfa),
    //Text message: cart number (1b), message_id (1b), current/total (1b, max 16/16), options (1b), timeout(1b*5sec),
    //Plus text max 249 bytes
    //[SITE] [TYPE] [CART] [MSG_ID] [INDEX/TOTAL] [OPTIONS?] [TIMEOUT?] [TEXT] (NO LENGHT???)
    TEXT_MESSAGE(0xf1),
    //Acknowledgement message, cart number (1b), acknowledged message_id (1b) + specific ack information
    ACK(0xf2),
    
    //Calibrate command, cart number, message id
    COMMAND(0xf3),
    
    WIFI(0xf4),
    
    // pinLocations type, packet will contain new pinLocations
    PIN_LOCATIONS(0xf5);

    //This header is common for all the packets, starts at offset 0
    public static final int headerOffset = 0;
    //A site id not implemented yet
    public static final int siteIdOffset = 0;
    //Packet type
    public static final int typeOffset = 1;
    //Destination cart number
    public static final int cartNumberOffset = 3;
    //Message identifier to avoid double acknowledgements
    public static final int messageIdOffset = 4;
    //The header ends here
    public static final int headerLength = 4;

    public static final int maxPacketLength = 512;

    //Message types:

    //1. Messages wich can be split into several packets:
    //packetCount is half byte the current packet index, half byte the total packets
    public static final int packetCountOffset = headerLength + 0; 

    //1.1. Cart ahead list
    //Hole for the list
    public static final int cartAheadHoleOffset = headerLength + 1;
    //Validity time of the list
    public static final int cartAheadTimeOffset = headerLength + 2;
    //The list of carts starts
    public static final int cartAheadListOffset = headerLength + 3;
    //One entry is 4 bytes position + 1 byte heading (not implemented)
    public static final int cartAheadEntryLength = 5;
    
    //1.2. Carts list:
    //The list of carts starts
    public static final int cartListOffset = headerLength + 1;
    //One entry is 1 byte id, 1 byte type, 4 bytes position, 1 byte hole, 1 byte pace
    public static final int cartListEntryLength = 8;

    //2. Predefined messages and text messages
    public static final int messageCodeOffset = headerLength + 0;

    public static final int textMessageOptionsOffset = headerLength + 1;
    public static final int textMessageTimeoutOffset = headerLength + 2;
    public static final int textMessageTextOffset = headerLength + 3;

    //  Weather warning

//    public static final int headerOffset = 0;
//    public static final int headerLength = 2;
    public static final int idOffset = 0;
//    public static final int typeOffset = 1;
//
//    public static final int payloadOffset = 2;
    public static final int locationOffset = 2;
//    public static final int locationLength = 5;
    public static final int locationLength = 5;
    public static final int walkerLocationLength = 7;
    public static final int bearingOffset = 6;
    public static final int bearingLength = 1;
    public static final int statusOffset = 7;
    public static final int statusLength = 1;

    //Predefined messages to base station
    public static final int RANGER_REQUEST = 0x01;
    public static final int EMERGENCY_CALL = 0x02;
    public static final int AMBULANCE_CALL = 0x03;
    public static final int REQUEST_ACK = 0x04;
    public static final int TMT_REQUEST_MSG = 0x05;

    public static final int TMT_REQUEST_LEADERBOARD = 0x02;

    public static final int STATUS_APPMODE_GOLF = 0x01;
    public static final int STATUS_APPMODE_RANGER = 0x02;
    public static final int STATUS_APPMODE_GOLF_HANDICAP = 0x03;
    public static final int STATUS_APPMODE_RESORT = 0x04;
    public static final int STATUS_APPMODE_ATAC = 0x05;

    public static final int STATUS_ACTIVE = 0x01;
    public static final int STATUS_WEATHERALERT = 0x02;
    public static final int STATUS_CARTPATHONLY = 0x04;
    public static final int STATUS_RESTRICTEDZONE = 0x08;
    public static final int STATUS_OUTOFCARTPATH = 0x10;
    public static final int STATUS_ACCESSIBLE = 0x20;

    //Predefined messages from base station
    //[CartNumber] [PRE_MSG] [MSG_CODE] [TXT_LENGTH] [TXT]
    //02 08 04 FF "Please go back to the parking area"
    public static final int WEATHER_ALERT_ENABLE = 0x04;
    public static final int WEATHER_ALERT_DISABLE = 0x05;
    public static final int CARTPATH_ONLY_ENABLE = 0x06;
    public static final int CARTPATH_ONLY_DISABLE = 0x07;

    //command from base station to cart unit
    public static final int CALIBRATE_COMMAND = 0x08;
    public static final int WIFI_ENABLE = 0x09;
    public static final int RESTART_COMMAND = 0x10;
    public static final int ENABLE_CARTKILL = 0x11;
    public static final int DISABLE_CARTKILL = 0x12;

    public static PacketType get(int type) {
        for (PacketType t : values()) {
            if (t.id == type) {
                return t;
            }
        }
        return NULL;
    }
    
    public final byte mask;
    public final byte id;

    private PacketType(int id) {
        this(id, 0xFF);
    }

    private PacketType(int id, int mask) {
        this.id = (byte) id;
        this.mask = (byte) mask;
    }

    public boolean test(int i) {
        return (i & mask) == id;
    }

    public boolean testIncluded(int i) {
        return (i & id) == id;
    }
}
