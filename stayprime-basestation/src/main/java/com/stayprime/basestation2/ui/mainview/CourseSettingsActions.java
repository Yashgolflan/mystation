/*
 *
 */
package com.stayprime.basestation2.ui.mainview;

import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.view.Dashboard;
import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.util.image.ImageRetrieverTask;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationAction;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class CourseSettingsActions {

    private Dashboard dashboard;
    private BaseStation2App app;
    private CourseSettingsManager settingsManager;

    public CourseSettingsActions(BaseStation2App app) {
        this.app = app;
    }
    
    public CourseSettingsManager getSettingsManager() {
        return settingsManager;
    }
    
    public void setSettingsManager(CourseSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Task weatherAlert(ApplicationAction action) {
        String setting = CourseSettingsManager.PROP_WEATHERALERT;
        boolean enable = settingsManager.getWeatherAlert() == false;

        return getSettingTask(action, setting, enable,
                "Please confirm to enable the Weather Alert on all carts",
                "Please confirm to disable the Weather Alert on all carts",
                PacketType.WEATHER_ALERT_ENABLE, PacketType.WEATHER_ALERT_DISABLE);
    }

    public Task cartPathOnly(ApplicationAction action) {
        String setting = CourseSettingsManager.PROP_CARTPATHONLY;
        boolean enable = settingsManager.getCartPathOnly() == false;

        return getSettingTask(action, setting, enable,
                "Please confirm to enable Cart Path Only on all carts",
                "Please confirm to disable Cart Path Only on all carts",
                PacketType.CARTPATH_ONLY_ENABLE, PacketType.CARTPATH_ONLY_DISABLE);
    }

    public Task getSettingTask(ApplicationAction action, String setting, boolean enable,
            String enableMessage, String disableMessage, int enableCommand, int disableCommand) {

        String message = enable? enableMessage : disableMessage;
        int setCommand = enable? enableCommand : disableCommand;
        int clearCommand = enable? disableCommand : enableCommand;

        int confirm = JOptionPane.showConfirmDialog(app.getMainFrame(), message,
                "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            return new SettingAndMessageTask(app, setting, enable,
                    action, setCommand, clearCommand);
        }
        else {
            action.setSelected(Boolean.valueOf(settingsManager.getProperty(setting)));
            return null;
        }
    }

    private class SettingAndMessageTask extends org.jdesktop.application.Task<Object, Void> {

        private final String key;
        private final boolean value;
        private final ApplicationAction action;
        private final int setCommand;
        private final int clearCommand;

        //private final boolean selectAction;
        SettingAndMessageTask(Application app, String setting, boolean value,
                ApplicationAction action, int setCommand, int clearCommand) {
            super(app);
            this.key = setting;
            this.value = value;
            this.action = action;
            this.setCommand = setCommand;
            this.clearCommand = clearCommand;
        }

        @Override
        protected Object doInBackground() {
            try {
                settingsManager.setProperty(key, String.valueOf(value));
                return null;
            }
            catch (Exception ex) {
                return new RuntimeException("Error saving setting: " + key, ex);
            }
        }

        @Override
        protected void succeeded(Object result) {
            if (action != null) {
                action.setSelected(Boolean.valueOf(settingsManager.getProperty(key)));
            }

            if (result instanceof Exception) {
//                TaskDialogs.showException((Throwable) result);
                NotificationPopup.showErrorDialog("Error saving setting");
            }
        }
    }

    public Task loadGolfClubImage() {
        final GolfClub golfClub = BaseStation2App.getApplication().getGolfClub();
        BasicMapImage courseImage = golfClub == null? null : golfClub.getCourseImage();

        if (courseImage != null && courseImage.getImageAddress() != null) {
            File f = new File(courseImage.getImageAddress());

            ImageRetrieverTask.ImageRetrievedCallback callback = new ImageRetrieverTask.ImageRetrievedCallback() {
                @Override
                public void imageRetrieved(BufferedImage image) {
                    dashboard.setGolfClub(golfClub);
                    dashboard.setMinScaleFillsViewport(true);
                    dashboard.setImage(image);
                }

                @Override
                public void retrieveImageFailed(Exception cause) {
                    TaskDialogs.showException(cause);
                    dashboard.setImage(null);
                }

                @Override
                public void retrieveImageCanceled() {
                }
            };

            if (f.canRead())
                return new ImageRetrieverTask(Application.getInstance(BaseStation2App.class), f, callback, true);
        }

        dashboard.setImage(null);
        return null;
    }

//    private void sendWeatherAlert() {
//        sendCommandToAllCarts(PacketType.WEATHER_ALERT_DISABLE,
//                PacketType.WEATHER_ALERT_ENABLE,
//                "Weather Alert Enable");
//    }
//
//    private void removeWeatherAlert() {
//        sendCommandToAllCarts(PacketType.WEATHER_ALERT_ENABLE,
//                PacketType.WEATHER_ALERT_DISABLE,
//                "Weather Alert Disable");
//    }
//
//    private void sendCartPathOnly() {
//        sendCommandToAllCarts(PacketType.CARTPATH_ONLY_DISABLE,
//                PacketType.CARTPATH_ONLY_ENABLE,
//                "Cart Path Only Enable");
//    }
//
//    private void removeCartPathOnly() {
//        sendCommandToAllCarts(PacketType.CARTPATH_ONLY_ENABLE,
//                PacketType.CARTPATH_ONLY_DISABLE,
//                "Cart Path Only Disable");
//    }

}
