/*
 *
 */
package com.stayprime.basestation2.reporting;

import com.stayprime.basestation2.services.CartService;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.hibernate.entities.CartUnit;
import com.stayprime.hibernate.entities.CourseInfo;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.rendersnake.HtmlCanvas;

/**
 *
 * @author benjamin
 */
public class CartConnectivityReport extends EmailReport {
    private static final Logger log = LoggerFactory.getLogger(CartConnectivityReport.class);

    private CartService cartService;
    private CourseService courseService;

    private Date nextReport;
    private String subtitle ;

    public CartConnectivityReport(CartService cartService, CourseService courseService) {
        this.cartService = cartService;
        this.courseService = courseService;
        this.nextReport = nextReport;
        this.subtitle = subtitle;
    }

    public void config(PropertiesConfiguration config) {
        String emails = config.getString("reportEmail", "ben@stayprime.com");
        setReportEmails(emails.split(","));
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setNextReport(Date nextReport) {
        this.nextReport = nextReport;
    }

    public void sendStatusReport() {
        try {
            Date today = getTodayCalendar().getTime();
            CourseInfo courseInfo = courseService.getCourseInfo();
            if (courseInfo == null) return;

            HtmlCanvas html = new HtmlCanvas();
            html.html().body().h1().content(courseInfo.getName());
            html.p().content(subtitle);

            List<CartInfo> carts = cartService.listCartsAndUnits();
            List<CartUnit> units = cartService.listUnits();
            createNoWiFiGPRSTable(units, html, today, "No WiFi or GPRS connection today", true);
            createNoWiFiGPRSTable(units, html, today, "No WiFi connection today", false);
            createNoGPRSTable(carts, html, today);

            html.p().content("Next report: " + nextReport);
            html.br()._body()._html();

            String output = html.toHtml();
            System.out.println();
            String subject = courseInfo.getName() + " report";

            sendReport(subject, output);
        }
        catch (Exception ex) {
            log.error(ex.toString());
            log.debug(ex.toString(), ex);
        }
    }

    private Calendar getTodayCalendar() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }

    private void createNoWiFiGPRSTable(List<CartUnit> units, HtmlCanvas html, Date date, String title, boolean reportCombined) throws IOException {
        html.html().body().h2().content(title);
        if (CollectionUtils.isEmpty(units)) {
            html.html().body().h3().content("Unit list empty.");
            return;
        }

        html.table().attributes().style("border: 2px solid black;");
        html.tr();
        html.th().content("Cart");
        html.th().content("MAC");
        html.th().content("ip_updated");
        html.th().content("location_updated");
        html.th().content("pins_updated");
        html.th().content("ads_updated");
        html._tr();
        int count = 0;
        for (CartUnit unit : units) {
            CartInfo cart = cartService.getCart(unit.getMacAddress());
            Date ip = unit.getIpUpdated();
            Date location = cart == null ? null : cart.getLocationLastUpdated();
            boolean noWifi = ip == null || ip.before(date);
            boolean noGprs = location == null || location.before(date);
            boolean report = false;
            if (reportCombined) {
                report = noWifi && noGprs;
            }
            else {
                report = noWifi && !noGprs;
            }
            if (report) {
                count++;
                html.tr();
                html.td().content(String.valueOf(cart == null ? "" : cart.getCartNumber()));
                html.td().content(String.valueOf(unit.getMacAddress()));
                html.td().content(dateFormat(unit.getIpUpdated()));
                html.td().content(dateFormat(location));
                html.td().content(dateFormat(unit.getPinlocationUpdated()));
                html.td().content(dateFormat(unit.getAdsUpdated()));
                html._tr();
            }
        }
        html.p().b().content("Total: ").div().content(count)._p();
        html._table();
    }

    private void createNoGPRSTable(List<CartInfo> carts, HtmlCanvas html, Date date) throws IOException {
        html.html().body().h2().content("No GPRS connection today");
        if (CollectionUtils.isEmpty(carts)) {
            html.html().body().h3().content("Cart list empty.");
            return;
        }

        html.table().attributes().style("border: 2px solid black;");
        html.tr();
        html.th().content("Cart");
        html.th().content("MAC");
        html.th().content("location_updated");
        html._tr();
        int count = 0;
        for (CartInfo cart : carts) {
            Date location = cart.getLocationLastUpdated();
            boolean noGprs = location == null || location.before(date);

            CartUnit unit = cart.getCartUnit();
            Date ip = unit == null ? null : unit.getIpUpdated();
            boolean wifiOk = ip != null && ip.after(date);
            //Report only if WiFi is okay for the unit or the unit wasn't found
            if (noGprs && (unit == null || wifiOk)) {
                count++;
                html.tr();
                html.td().content(String.valueOf(cart.getCartNumber()));
                html.td().content(String.valueOf(cart.getMacAddress()));
                html.td().content(dateFormat(cart.getLocationLastUpdated()));
                html._tr();
            }
        }
        html.p().b().content("Total: ").div().content(count)._p();
        html._table();
    }

}
