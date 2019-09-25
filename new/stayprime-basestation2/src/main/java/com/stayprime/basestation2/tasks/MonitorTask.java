/*
 * 
 */
package com.stayprime.basestation2.tasks;

import com.stayprime.basestation2.reporting.CartConnectivityReport;
import com.stayprime.basestation2.reporting.PinUpdateReport;
import com.stayprime.basestation2.reporting.WarningReport;
import com.stayprime.basestation2.services.CartService;
import com.stayprime.basestation2.services.CourseService;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author benjamin
 */
@Component
public class MonitorTask {
    private PropertiesConfiguration config;

    private boolean sendDailyReport;
    private CartConnectivityReport courseStatusReport;
    private Calendar nextReportCal;

    private boolean sendPinReport;
    private PinUpdateReport pinReport;

    @Autowired
    private WarningReport warningReport;

    @Autowired
    CartService cartService;

    @Autowired
    CourseService courseService;

    @Autowired
    public MonitorTask(PropertiesConfiguration config) {
        this.config = config;
    }

    private void config() {
        sendDailyReport = config.getBoolean("sendDailyReport", Boolean.TRUE);
        if (sendDailyReport) {
            courseStatusReport = new CartConnectivityReport(cartService, courseService);
            courseStatusReport.config(config);
        }

        sendPinReport = config.containsKey("sendPinReport");
        if (sendPinReport) {
            pinReport = new PinUpdateReport(cartService, courseService);
            pinReport.config(config);
        }

        warningReport = new WarningReport();
        warningReport.config(config);
    }

    public void start() {
        config();
        Timer timer = new Timer("monitorTask", true);

        if (sendDailyReport) {
            nextReportCal = getFutureTime(23, 0, 0);
            courseStatusReport.setSubtitle("Startup report");
            timer.schedule(new ConnectivityReportTask(), 5000);
            timer.schedule(new ConnectivityReportTask(), nextReportCal.getTime(), TimeUnit.DAYS.toMillis(1));

            Date pinReportDate = getFutureTime(5, 0, 0).getTime();
            timer.schedule(new PinReportTask(), 5000);
            timer.schedule(new PinReportTask(), pinReportDate, TimeUnit.DAYS.toMillis(1));
        }
    }

    private GregorianCalendar getFutureTime(int hour, int min, int sec) {
        GregorianCalendar time = new GregorianCalendar();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, min);
        time.set(Calendar.SECOND, sec);
        time.set(Calendar.MILLISECOND, 0);

        if (time.getTime().before(new Date())) {
            time.add(Calendar.DATE, 1);
        }

        return time;
    }

    private class ConnectivityReportTask extends TimerTask {
        @Override
        public void run() {
            if (sendDailyReport) {
                nextReportCal.add(Calendar.DATE, 1);
                courseStatusReport.setNextReport(nextReportCal.getTime());
                courseStatusReport.sendStatusReport();
                courseStatusReport.setSubtitle("Daily report");
            }
        }
    }

    private class PinReportTask extends TimerTask {
        @Override
        public void run() {
            if (sendPinReport) {
                pinReport.sendStatusReport();
            }
        }
    }

}
