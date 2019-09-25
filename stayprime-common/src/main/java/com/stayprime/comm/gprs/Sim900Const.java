/*
 * 
 */

package com.stayprime.comm.gprs;

/**
 *
 * @author benjamin
 */
public class Sim900Const {
    //Sim900 specific commands
    public static final String AT_CCID = "AT+CCID"; //Request ICCID header

    public static final String CIPHEAD_SET1 = "+CIPHEAD=1"; //Request IP header
    public static final String CIPSPRT_SET1 = "+CIPSPRT=1"; //Prompt > and SEND OK
    public static final String CIPSRIP_SET1 = "+CIPSRIP=1"; //RECV FROM:<ip>:<port>
    public static final String CLPORT_SET_UDP = "+CLPORT=\"UDP\","; //Fix local UDP port
    public static final String CIPUDPMODE_WR = "+CIPUDPMODE="; //UDP Extended mode
    
    public static final String IP_HEAD = "+IPD,";

    public static final String AT_CLPORT_RD = "AT+CLPORT?"; //Read local UDP port
    public static final String CLPORT_RE_UDP = "UDP: "; //Read local UDP port

    public static final String AT_CSTT_WR = "AT+CSTT=";//"apn.com","user","pass"
    public static final String AT_CSTT_RD = "AT+CSTT?";
    public static final String CSTT_RE = "+CSTT: ";//"apn.com","user","pass"
    
    public static final String AT_CIICR = "AT+CIICR";//Bring Up Wireless Connection with GPRS or CSD
    public static final String AT_CIFSR = "AT+CIFSR";//Get ip address
    
    public static final String AT_CIPSTART_WR = "AT+CIPSTART=";//"UDP","ip","port"
    public static final String UDP = "UDP";

    public static final String AT_CIPSEND = "AT+CIPSEND";
    public static final String SEND_OK = "SEND OK";
    public static final String AT_CIPCLOSE = "AT+CIPCLOSE";
    public static final String CLOSE_OK = "CLOSE OK";
    
    public static final String AT_CIPSHUT = "AT+CIPSHUT";
    public static final String SHUT_OK = "SHUT OK";
    
    public static final String AT_CIPSTATUS = "AT+CIPSTATUS";
    public static final String CIPSTATUS_RE = "STATE: ";
    
    public static final String IP_INITIAL =	"IP INITIAL";
    public static final String IP_START =	"IP START";
    public static final String IP_CONFIG =	"IP CONFIG";
    public static final String IP_GPRSACT =	"IP GPRSACT";
    public static final String IP_STATUS =	"IP STATUS";
    public static final String TCP_CONNECTING = "TCP CONNECTING";
    public static final String UDP_CONNECTING = "UDP CONNECTING";
    public static final String SERVER_LISTENING="SERVER LISTENING";
    public static final String CONNECT_OK =	"CONNECT OK";
    public static final String TCP_CLOSING =	"TCP CLOSING";
    public static final String UDP_CLOSING =	"UDP CLOSING";
    public static final String TCP_CLOSED =	"TCP CLOSED";
    public static final String UDP_CLOSED =	"UDP CLOSED";
    public static final String PDP_DEACT =	"PDP DEACT";

}
