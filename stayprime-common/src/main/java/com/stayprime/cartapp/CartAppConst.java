/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.cartapp;

/**
 *
 * @author stayprime
 */
public class CartAppConst {

    public static final String CONFIG_CONNECTIONSTRING = "connectionString";
    public static final String defaultConnectionString = "http://192.168.101.10:8080/";

    public static final String CONFIG_HARDWARE = "hardware";
    public static final String SPE1 = "SPE1";
    public static final String SPE2 = "SPE2";
    public static final String DEMO = "demo";

    public static final String microcomm9600 = "microcom -X -s 9600 ";
    public static final String microcomm115200 = "microcom -X -s 115200 ";

    //Network connection and updating
    public static final String CONFIG_UPDATEPERIOD = "updatePeriod";
    public static final String CONFIG_REBOOTONUPDATE = "rebootOnUpdate";
    public static final String CONFIG_DEMOWIFICOMM = "demoWifiComm";

    //Application launching
//    public static final String PARAM_LOGLEVEL = "-loglevel=";

    //App setup
    public static final String CONFIG_APPMODE = "appMode";
    public static final String CONFIG_RANGERMODEENABLED = "rangerModeEnabled";
    public static final String CONFIG_TOURNAMEMTLIST = "showTournamentList";
    public static final String CONFIG_FULLSCREEN = "fullscreen";
    public static final String CONFIG_SHOWCURSOR = "showCursor";
    public static final String CONFIG_FLYOVERCOMMAND = "flyoverCommand";
    public static final String CONFIG_SOUNDCOMMAND = "soundCommand";

    //GPS setup
    public static final String CONFIG_GPSCOMMAND = "GPSCommand";
    public static final String CONFIG_GPSLOWSIGNAL = "GPSLowSignal";
    public static final String CONFIG_GPSGOODSIGNAL = "GPSGoodSignal";
    public static final String CONFIG_DEMOFILE = "demoFile";
    public static final String CONFIG_DEMOPARAMS = "demoParams";
    public static final String CONFIG_DEMOCOMMAND = "demoCommand";
    public static final String DEMOCOMMAND_STDIN = "stdin";

    //Presentation
    public static final String CONFIG_SHOWHOLENAVBUTTONS = "showHoleNavButtons";
    public static final String CONFIG_SELECTUNITSENABLED = "selectUnitsEnabled";
    public static final String CONFIG_MENUENABLED = "menuEnabled";
    public static final String CONFIG_SCORECARDENABLED = "scorecardEnabled";
    public static final String CONFIG_SCORECARDAUTO = "scorecardAuto";
    public static final String CONFIG_SCORECARDEMAIL = "scorecardEmail";
    public static final String CONFIG_SCORECARDPRINTOUT = "scorecardPrintout";
    public static final String CONFIG_ALTLOCALE = "altLocale";

    public static final String CONFIG_PROTIPSENABLED = "proTipsEnabled";
    public static final String CONFIG_RANGERENABLED = "rangerEnabled";
    public static final String CONFIG_EMERGENCYENABLED = "emergencyEnabled";
    public static final String CONFIG_STATUSBARENABLED = "statusBarEnabled";
    public static final String CONFIG_SHOWPINLOCATIONS = "showPinLocations";
    public static final String CONFIG_PACEOFPLAYSTYLE = "paceOfPlayStyle";
    public static final String CONFIG_PINLABELS = "pinLabels";
    public static final String CONFIG_WEATHERALERTTEXT = "weatherAlertText";
    public static final String CONFIG_SHOWDRIVEDISTANCE = "showDriveDistance";
    public static final String CONFIG_SILABEL = "siLabel";
    public static final String CONFIG_CURRENCY = "currency";
    //showWarningAreas = {0|false|true|1|2} true|1 is restricted zones only, 2 includes action zones
    public static final String showWarningAreas = "showWarningAreas";
    public static final String showBranding = "showBranding";

    //Golf round control
    public static final String CONFIG_RESETGAMEDELAY = "resetGameDelay";
    public static final String CONFIG_MINHOLETIME = "minHoleTime";

    //Power saving
    public static final String CONFIG_POWERSAVING = "powerSaving";
    public static final String CONFIG_POWERSAVEDELAY = "powerSaveDelay";
    public static final int DEFAULT_POWERSAVEDELAY = 300;

    //Debug and diagnostic
    public static final String CONFIG_DEBUGCOURSE = "debugCourse";
    public static final String CONFIG_ENABLETRACKINGINTERFACE = "enableTrackingInterface";

    //Advertisement
    public static final String CONFIG_FULLSCREENADSPEED = "fullscreenAdSpeed";
    public static final String CONFIG_IGNOREADZONES = "ignoreAdZones";

    //Cart kill
    public static final String CONFIG_CARTKILLENABLED = "cartKillEnabled";
    public static final String CONFIG_CARTKILLPARAMS = "cartKillParams";
    public static final String CONFIG_CARTPATHKILL = "cartPathKill";
    public static final String CONFIG_CARTPATHDISTANCE = "cartPathDistance";

    //Unit configuration
    public static final String CONFIG_UNITID = "unitId";
    public static final String CONFIG_SITEID = "siteId";

    //GPRS
    public static final String GPRS_INTERFACE = "GPRSInterface";
    public static final String GPRS_INTERFACE_DEFAULT = "default";
    public static final String PACKET_MIN_DELAY = "packetMinDelay";
    public static final String PACKET_MAX_DELAY = "packetMaxDelay";
    public static final String DISTANCE_THRESHOLD = "distanceThreshold";
    public static final String GPRS_LOGEVENTS = "logGprsEvents";
    public static final String GPRS_RECONNECT = "gprsReconnect";
    public static final String modemStartDelay = "modemStartDelay";

    //WiFi monitoring
    public static final String WIFI_MINSIGNAL = "wifiMinSignal";
    public static final String WIFI_SERVICEHOURS = "wifiServiceHours";
    public static final String WIFI_RESET = "wifiReset";
    public static final String WIFI_RESETDELAY = "wifiResetDelay";
    public static final String WIFI_RESETINTERVAL = "wifiResetInterval";
    public static final String WIFI_REBOOT = "wifiReboot";
    public static final String WIFI_REBOOTWITHOUTFIX = "wifiRebootWithoutFix";
    public static final String WIFI_REBOOTDELAY = "wifiRebootDelay";
    public static final String WIFI_RESTARTAFTERCONNECTION = "wifiRestartAfterConnection";

    //WiFi testing
    public static final String WIFITESTPARAMS = "wifiTestParams";

    //Environment variables
    public static final String ENV_WLANDEV = "WLANDEV";

    //SPE2 specifics
    public static final String SPE2_ADC_MV_FILE = "/sys/devices/platform/imx-i2c.0/i2c-0/0-0049/mv";

    public static final String distanceToCenter = "distanceToCenter";
    public static final String yardsToPinLabel = "yardsToPinLabel";
    public static final String metersToPinLabel = "metersToPinLabel";

    public static String diagPin = "diagPin";
    public static String debugScorecard = "debugScorecard";
    
    // Device type
    public static final int WALKER_SPE3 = 1;
    public static final int EIGHT_INCH_SPE3 = 2;
    public static final int TEN_INCH_SPE3 = 3;
    public static final int DEVICE_TYPE_NOT_SHARED = 0;
    
}
