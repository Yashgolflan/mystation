/*
 * 
 */
package com.stayprime.basestation2;

import com.stayprime.basestation2.comm.NotificationsManager;
import com.stayprime.basestation2.services.CartService;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.localservice.Constants;
import com.stayprime.storage.services.AssetSyncService;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author benjamin
 */
@Component
public class BaseStationTasks {
    private final Logger log = LoggerFactory.getLogger(getClass());

    BaseStation2App app;

    CartService cartService;

    CourseService courseService;

    AssetSyncService assetSyncService;
    
    NotificationsManager notificationsManager;

    @Autowired
    public BaseStationTasks(BaseStation2App app) {
        this.app = app;
        cartService = app.getServices().getCartService();
        courseService = app.getServices().getCourseService();
        assetSyncService = app.getServices().getAssetSyncService();
        notificationsManager = app.getNotificationsManager();
    }

    @Async
    public void writeNexmoConfigToDatabase() {
        PropertiesConfiguration config = app.getConfig();
        saveNonBlankSetting(Constant.APIKey, config.getString(Constant.APIKey));
        saveNonBlankSetting(Constant.APISecret, config.getString(Constant.APISecret));
    }

    private void saveNonBlankSetting(String key, String value) {
        value = StringUtils.stripToNull(value);
        if (value != null) {
            courseService.saveSetting(key, value);
        }
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 20000)
    public void syncGolfClubTask() {
        try {
            assetSyncService.syncAssets();
            syncWeatherAlertAndCartPathOnly();
            syncPinsGrid();
        }
        catch (Throwable t) {
            log.warn("{} in syncGolfClubTask()", t);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void updateCartsTask() {
        try {
            cartService.syncCarts();
            app.loadCarts();
        }
        catch (Throwable t) {
            log.error(t.toString());
            log.debug(t.toString(), t);
        }
    }
    
    public void syncWeatherAlertAndCartPathOnly() {
        try {
            Boolean cartPathOnly = Boolean.valueOf(courseService.getSettingValue(CourseSettingsManager.PROP_CARTPATHONLY));
            Boolean weatherAlert = Boolean.valueOf(courseService.getSettingValue(CourseSettingsManager.PROP_WEATHERALERT));
            app.getMainView().getMainPanel().weatherAlertAndCartPathOnlyButtonsStateChanged(cartPathOnly, weatherAlert);
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }
    
    public void syncPinsGrid() {
        try {
            Integer currentGrid = Integer.valueOf(courseService.getSettingValue(CourseSettingsManager.PROP_GRIDSYSTEMCURRENTGRID));
            app.getMainView().getMainPanel().pinsGridNumberStateChanged(currentGrid);
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }
    
    @Scheduled(fixedDelay = 10000)
    public void syncServiceRequests() {
        try {
            if (notificationsManager != null) {
                log.info("Updating service requests");
                notificationsManager.updateServiceRequests();
            }
        }
        catch(Throwable ex) {
            log.error(ex.toString());
        }
    }

//    @Scheduled(24h at 00:00) use CronTask
//    public void dbMaintenaceTask() {
//        try {
//            int deleted = cartService.deleteOldCartTracks(180);
//            log.info("Deleted " + deleted + " old cart track records.");
//        }
//        catch (Exception ex) {
//            log.error(ex.toString());
//        }
//    }
}
