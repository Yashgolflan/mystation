/*
 * 
 */
package com.stayprime.comm.gprs;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class GPRSPacketSenderConfig {
    public static final String NETWORK_SPEC = "GPRSNetworkSpec";
    public static final String NETWORK_ID = "GPRSNetworkId";
    public static final String OPERATOR = "GPRSOperator";
    public static final String GPRS_REATTACH = "GPRSReattach";
    public static final String MODEM_RESET_COUNT = "GPRSModemResetCount";
    public static final String APN_SETTINGS = "GPRSAPNSettings";
    public static final String SERVER_SETTINGS = "GPRSServerSettings";

    /**
     * Initial configuration of GsmAtModem.
     * @param modem the modem to set the configuration on
     * @param config the configuration object
     */
    public static void config(GsmAtModem modem, PropertiesConfiguration config) {
        if(config.containsKey(GPRS_REATTACH)) {
	    modem.setGprsReattach(config.getBoolean(GPRS_REATTACH, false));
	}
        updateConfig(modem, config);
    }

    /**
     * These configurations can be changed on the fly.
     * @param modem the modem to set the configuration on
     * @param config the configuration object
     */
    public static void updateConfig(GsmAtModem modem, PropertiesConfiguration config) {
        String operator = config.getString(OPERATOR, null);
	if(operator != null) {
            //If only the operator is set, we define the force network spec
            modem.setForceOperator(operator);
            String spec = "1,0,\"" + operator + "\"";
	    modem.setNetworkSpec(spec);
	}

        String networkId = config.getString(NETWORK_ID, null);
	if(networkId != null) {
            //If the network id is set, we define the force network spec
            String spec = "1,2,\"" + networkId + "\"";
	    modem.setNetworkSpec(spec);
	}

        String networkSpec = config.getString(NETWORK_SPEC);
	if(StringUtils.isNotEmpty(networkSpec)) {
            //If network spec is specified, use it directly
	    modem.setNetworkSpec(networkSpec);
	}

        String[] apn = config.getString(APN_SETTINGS, "").split(",");
	if(apn.length == 3) {
	    modem.setApnConfig(apn[0], apn[1], apn[2]);
	}

	String[] srv = config.getString(SERVER_SETTINGS, "").split(",");
	if(srv.length == 2) {
	    modem.setServerConfig(srv[0], srv[1]);
	}
    }

    public static void config(GPRSPacketComm comm, PropertiesConfiguration config) {
	if(config.containsKey(MODEM_RESET_COUNT)) {
	    comm.setModemResetCount(config.getInt(MODEM_RESET_COUNT, 120));
	}

    }
}
