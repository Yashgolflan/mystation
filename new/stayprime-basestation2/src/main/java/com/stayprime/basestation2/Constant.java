/*
 *
 */

package com.stayprime.basestation2;

/**
 *
 * @author benjamin
 */
public class Constant {

    //System
    public static final String CONFIGURATION_FILENAME = "app.properties";
    public static final String DEFAULT_CURRENCY = "USD";

    public static final String DEMO = "demo";

    public static final String ACK_REQUEST = "Sending request to Clubhouse";
    public static final String PATHTOTOMCAT6 = "/var/lib/tomcat6/TournamentListing.xml";
    public static final String TournamentListingXml = "TournamentListing.xml";
    public static final String BASESTATION_PATH = "/opt/StayPrime/BaseStation/";
    public static final String BASESTATION_EXPORT = "/opt/StayPrime/BaseStation/export";

    public static final String CONFIG_PINFLAGIMAGE = "pinFlagImage";
    public static final String CONFIG_PINFLAGSIZE = "pinFlagSize";
    public static final String CONFIG_PINFLAGPIXELSIZE = "pinFlagPixelSize";
    public static final String CONFIG_PINFLAGCENTER = "pinFlagCenter";
    public static final String CONFIG_SINGLEINSTANCE = "singleInstance";
    public static final String CONFIG_SINGLEINSTANCEPORT = "singleInstancePort";

    public static final String PROP_FONTINCREASE = "FontIncrease";
    public static final String PROP_LOOKANDFEEL = "LookAndFeel";

    public static final String ARG_LOGLEVEL = "-loglevel=";
    public static final String DEFAULT_FLAG_IMAGE = "com/stayprime/basestation2/resources/flag.png";

    //Application
    public static final String CONFIG_LOCALE = "locale";
    public static final String CONFIG_CURRENCY = "currency";
    public static final String CONFIG_SHOWCARTASSIGNMENT = "showCartAssignment";


    //UI Related
    public static String HUT_TAB_TITLE = "Hut Information";
    public static String MENU_TAB_TITLE= "Food & Beverage";

    public static final String CONFIG_RENDERCARTPATHWIDTH = "renderCartPathWidth";
    public static final String CONFIG_CARTPATHWIDTH = "cartPathWidth";

    //Settings dialog
    public static final  String showWebAppSettings = "showWebAppSettings";
    public static final  String showATACSettings = "showATACSettings";
    //Drawing tool
    public static final String showDrawingToolUtils = "showDrawingToolUtils";

    //SMS API settings
    public static final  String APIKey = "APIKey";
    public static final  String APISecret = "APISecret";

    // Messages
    public static final String SUCCESS = "Successfully done!";
    public static final String ERROR = "There is an error please try again later";

    //file paths
    public static String  WIN_FILE = "/com/stayprime/basestation2/resources/demo_win.sql";
    public static String  LIN_FILE = "/com/stayprime/basestation2/resources/demo_linux.sql";
    public static String  MAC_FILE = "/com/stayprime/basestation2/resources/demo_mac.sql";

}
