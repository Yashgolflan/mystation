/*
 * 
 */
package com.stayprime.cartapp;

import com.stayprime.cartapp.menu.Menu;
import com.stayprime.application.AppConfig;
import com.stayprime.golf.course.Site;
import com.stayprime.model.golf.CartAppMode;
import com.stayprime.oncourseads.AdList;
import com.stayprime.util.ConfigUtil;
import com.stayprime.util.file.BasePathLocator;
import com.stayprime.util.file.FileLocator;
import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public abstract class CartAppConfig extends AppConfig {
    static final Logger log = LoggerFactory.getLogger(CartAppConfig.class);

    //Paths
    protected int basePath;
    protected int configPath;

    //Paths for asset loading 
    protected int golfcoursePath;
    protected int stagingPath;
    protected int tempPath;

    //Old app and UI settings
    private int appConfig;
    //Unit specific properties (id, current mode, hardware, etc)
    private int unitConfig;

    //FileLocators
    private int golfcourseFl;

    private int configSiteId = 0; //siteId loaded from config

    private int siteId = 0; //siteId loaded from golf course

    //Assets
    private Site site;

    private AdList ads;

    private Menu menu;

    private CartAppMode mode;

    public CartAppConfig() {
    }

    public void init() {
        initBasePath();
        initPaths();
        initConfig();
        initMode();
    }

    protected abstract void initBasePath();
//    protected abstract void initBasePath() {
//        File basePathFile = new File("");
//        basePath = addPath(basePathFile);
//    }

    protected void initPaths() {
        File basePathFile = getPath(basePath);
        log.debug("basePath: " + basePathFile);

        //Define the application paths
        configPath = addPath(new File(basePathFile, "config"));
        golfcoursePath = addPath(new File(basePathFile, "golfcourse"));
        stagingPath = addPath(new File(basePathFile, "staging"));

        String tempDir = System.getProperty("java.io.tmpdir");
        File temp = tempDir != null ? new File(tempDir) : new File(basePathFile, "tmp");
        tempPath = addPath(temp);
        log.debug("tempPath: " + getPath(tempPath));

        //Initialize file locators
        golfcourseFl = addFileLocator(new BasePathLocator(getPath(golfcoursePath)));
    }

    protected void initConfig() {
        log.info("Loading configuration...");
        try {
            File configPathFile = getConfigPath();

            File appConfigFile = new File(configPathFile, "app.properties");
            PropertiesConfiguration app = ConfigUtil.load(appConfigFile, log);
            appConfig = addConfig(app);

            File unitFile = new File(configPathFile, "unit.properties");
            PropertiesConfiguration unit = ConfigUtil.load(unitFile, log);
            unit.setAutoSave(true);
            unitConfig = addConfig(unit);

            overrideAppConfigs(app, unit);
            configSiteId = app.getInt(CartAppConst.CONFIG_SITEID, 0);

            log.debug("Configuration loaded");
        }
        catch (Exception ex) {
            log.error("Error loading properties files: " + ex.getMessage());
            log.debug(ex.getMessage(), ex);
        }
        notifyLoaded();
    }

    protected void overrideAppConfigs(PropertiesConfiguration appConfig, PropertiesConfiguration unitConfig) {
        Iterator keys = unitConfig.getKeys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            appConfig.setProperty(key, unitConfig.getProperty(key));
        }
    }

    protected void initMode() {
        String appMode = getAppConfig().getString(CartAppConst.CONFIG_APPMODE);
        mode = CartAppMode.GOLF;
        if (appMode != null) {
            try {
                mode = CartAppMode.valueOf(appMode);
            }
            catch (Exception ex) {
                log.warn("Wrong appMode configuration: " + appMode);
            }
        }
    }

    public void siteConfigLoaded(Properties siteConfig) {
        PropertiesConfiguration config = getAppConfig();
        for (String key : siteConfig.stringPropertyNames()) {
            config.setProperty(key, siteConfig.getProperty(key));
        }
        notifyLoaded();
    }

    public FileLocator getGolfcourseFileLocator() {
        return getFileLocator(golfcourseFl);
    }

    public File getBasePath() {
        return getPath(basePath);
    }

    public File getConfigPath() {
        return getPath(configPath);
    }

    public File getGolfcoursePath() {
        return getPath(golfcoursePath);
    }

    public File getStagingPath() {
        return getPath(stagingPath);
    }

    public File getTempPath() {
        return getPath(tempPath);
    }

    public PropertiesConfiguration getAppConfig() {
        return getConfig(appConfig);
    }

    public PropertiesConfiguration getUnitConfig() {
        return getConfig(unitConfig);
    }

    public int getCartNumber() {
        return getUnitConfig().getInt(CartAppConst.CONFIG_UNITID, 0);
    }

    public int getSiteId() {
        return configSiteId > 0 ? configSiteId : siteId;
    }

    public String getTimeZone() {
        return getAppConfig().getString(Site.timeZone);
    }

    public String getThankyouImage() {
        return getAppConfig().getString(Site.thankyouImage);
    }

    public String getWelcomeImage() {
        return getAppConfig().getString(Site.welcomeImage);
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
        if (site != null) {
            if (configSiteId <= 0) {
                this.siteId = site.getSiteId();
            }
            siteConfigLoaded(site.getSiteConfig());
        }
    }

    public AdList getAds() {
        return ads;
    }

    public void setAds(AdList ads) {
        this.ads = ads;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public CartAppMode getMode() {
        return mode;
    }

    public void setMode(CartAppMode mode) {
        this.mode = mode;
    }

}
