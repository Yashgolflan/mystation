/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2;

import com.stayprime.basestation2.services.CourseService;
import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.localservice.Constants;
import java.util.Date;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class BaseStationStartupTask extends Task<Void, Void> {
    private static final Logger log = LoggerFactory.getLogger(BaseStationStartupTask.class);

    private final BaseStation2App application;
    private final CourseService courseService;

    public BaseStationStartupTask(BaseStation2App app) {
        super(app);
        this.application = app;
        this.courseService = app.getServices() == null ? null : app.getServices().getCourseService();
        application.loadFromStorage();
    }

    @Override
    protected Void doInBackground() throws Exception {
        setMessage("Loading Golf Club data...");       
        application.syncGolfClub();
        setMessage("Checking settings...");
        writeConfigToDatabase();
        updateCourseUpdated();
        setMessage("Running startup tasks...");
        application.runStartupItems();
        return null;
    }

    @Override
    protected void succeeded(Void result) {
        application.loadGolfClub();
    }

    private void writeConfigToDatabase() {
        try {
            PropertiesConfiguration config = application.getConfig();
            String configValue = StringUtils.stripToNull(config.getString(Constant.APIKey));
            String dbValue = courseService.getSettingValue(Constant.APIKey);
            if (configValue != null && !configValue.equals(dbValue)) {
                courseService.saveSetting(Constant.APIKey, configValue);
            }

            configValue = StringUtils.stripToNull(config.getString(Constant.APISecret));
            dbValue = courseService.getSettingValue(Constant.APISecret);
            if (configValue != null && !configValue.equals(dbValue)) {
                courseService.saveSetting(Constant.APISecret, configValue);
            }
        }
        catch (Exception ex) {
            log.warn("{} in writeConfigToDatabase()", ex.toString());
            log.debug("Exception in writeConfigToDatabase()", ex);
        }
    }

    private void updateCourseUpdated() {
        try {
            CourseInfo courseInfo = courseService.getCourseInfo();
            String courseUpdated = courseService.getSettingValue(Constants.courseUpdated);
            if (courseInfo != null && NumberUtils.toLong(courseUpdated) <= 0) {
                Date updated = courseInfo.getUpdated();
                courseService.saveSetting(Constants.courseUpdated, String.valueOf(updated.getTime()));
            }
        }
        catch (Exception ex) {
            log.warn("{} in updateCourseUpdated()", ex.toString());
            log.debug("Exception in updateCourseUpdated()", ex);
        }
    }
}
