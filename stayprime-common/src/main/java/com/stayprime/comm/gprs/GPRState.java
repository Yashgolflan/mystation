/*
 * 
 */
package com.stayprime.comm.gprs;

/**
 *
 * @author benjamin
 */
public enum GPRState {
    //Generic state:
    UNKNOWN(0), 
    CFUN0(10), 
    RFDISABLED(15), 
    FUNCTIONAL(20), 
    GPRS_NOTREADY(25), 
    GPRS_READY(30), 

    //IP Connection related:
    IP_INITIAL(40), //IP_INITIAL
    IP_START(50), //IP_START
    IP_GPRSACT(60), //IP_GPRSACT
    IP_STATUS(70), //IP_STATUS

    //Single connection related:
    CONNECT_OK(80), //CONNECT_OK
    // Can receive data from server directly after connection?
    //   Unlikely since there is no real connection in UDP.
    // OR do we need to send a packet first?
    UDP_CLOSED(90),

    //IP Connection related:
    IP_SHUT(95),
    PDP_DEACT(100);
    
    public final int id;

    private GPRState(int id) {
	this.id = id;
    }
    
}
