/*
 *
 */
package com.stayprime.comm.gprs;

/**
 *
 * @author benjamin
 */
public class AtConst {
    public static final long DELAY1 = 1000;
    public static final long DELAY5 = 5000;
    public static final long DELAY10 = 10000;
    public static final long DELAY30 = 30000;
    public static final long DELAY60 = 60000;

    public static final char NL = 0x0d;
    public static final char EOF = 0x1a;
    public static final char ESCAPE = 0x1b;
    public static final String OK = "OK";
    public static final String AT = "AT";
    public static final String ERROR = "ERROR";
    public static final String CALL_READY = "Call Ready";
    public static final String ATE0 = "ATE0"; //Echo off
    public static final String ATE1 = "ATE1"; //Echo on

    public static final String AT_CFUN_RESET = "AT+CFUN=1,1";
    public static final String AT_CFUN_RD = "AT+CFUN?";
    public static final String CFUN_RE = "+CFUN: "; //(unsolicited, startup)
    public static final String CFUN_WR = "AT+CFUN=";
    public static final int CFUN_0 = 0;
    public static final int CFUN_1 = 1;
    public static final int CFUN_DISABLERF = 4;

    public static final String AT_CIMI_RD = "AT+CIMI";
    public static final String AT_NWICCID_RD = "AT$NWICCID?";
    public static final String NWICCID_RE = "$NWICCID: ";
    public static final String AT_GSN_RD = "AT+GSN"; //Request IMEI

    public static final String AT_CPIN_RD = "AT+CPIN?";
    public static final String CPIN_RE = "+CPIN: ";//(unsolicited, startup)
    public static final String CPIN_READY = "READY";
    public static final String CPIN_NOT_INSERTED = "NOT INSERTED";

    public static final String AT_COPS_RD = "AT+COPS?"; //Read current operator
    public static final String AT_COPS_WR_AUTO = "AT+COPS=0"; //Auto network selection
    public static final String AT_COPS_WR = "AT+COPS="; //Force network
    public static final String COPS_RE = "+COPS: ";

    public static final String AT_CSQ_RD = "AT+CSQ";
    public static final String CSQ_RE = "+CSQ: ";//<rssi>,<ber> 20,0

    public static final String AT_CREG_RD = "AT+CREG?";//Network registration
    public static final String CREG_RE = "+CREG: ";//0,5

    public static final String AT_CGATT_RD = "AT+CGATT?"; //Attach or Detach from GPRS Service
    public static final String AT_CGATT_WR = "AT+CGATT="; //Attach or Detach from GPRS Service
    public static final String CGATT_RE = "+CGATT: ";//0 ó 1

}
