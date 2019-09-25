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
import java.util.Date;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.rendersnake.HtmlCanvas;

/**
 *
 * @author benjamin
 */
public class PinUpdateReport extends EmailReport {
    private static final Logger log = LoggerFactory.getLogger(PinUpdateReport.class);

    private CartService cartService;
    private CourseService courseService;

    public PinUpdateReport(CartService cartService, CourseService courseService) {
        this.cartService = cartService;
        this.courseService = courseService;
    }

    public void config(PropertiesConfiguration config) {
        String emails = config.getString("sendPinReport", "ben@stayprime.com");
        setReportEmails(emails.split(","));
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void sendStatusReport() {
        try {
            CourseInfo courseInfo = courseService.getCourseInfo();
            String title = courseInfo.getName() + " pin update report";

            Date pinsUpdated = courseService.getPinsLastUpdated();

            HtmlCanvas html = new HtmlCanvas();
            html.html().body().h1().content(title);

            html.p().content("Last pin update: " + dateFormat(pinsUpdated));

            createPinsNotUpdatedTable(html, pinsUpdated, "Carts with pins not updated");

//            html.p().content("Next report: " + nextReport);
            html.br()._body()._html();

            String output = html.toHtml();
            System.out.println();

            sendReport(title, output);
        }
        catch (Exception ex) {
            log.error(ex.toString());
            log.debug(ex.toString(), ex);
        }
    }

    private void createPinsNotUpdatedTable(HtmlCanvas html, Date date, String title) throws IOException {
        List<CartInfo> carts = cartService.listCartsAndUnits();

        html.html().body().h2().content(title);
        html.table().attributes().style("border: 2px solid black;");
        html.tr();
        html.th().content("Cart");
        html.th().content("GPS Unit");
        html.th().content("Pins Date");
        html.th().content("WiFi last connected");
        html._tr();

        int count = 0;
        for (CartInfo cart : carts) {
            CartUnit unit = cart.getCartUnit();
            Date pinsUpdated = null;

            String mac = StringUtils.EMPTY;
            String pins = StringUtils.EMPTY;
            String lastWifi = StringUtils.EMPTY;

            if (unit != null) {
                mac = StringUtils.stripToEmpty(cart.getMacAddress());

                pinsUpdated = unit.getPinlocationUpdated();
                pins = dateFormat(pinsUpdated);

                Date ipUpdated = unit.getIpUpdated();
                lastWifi = dateFormat(ipUpdated);
            }
            else {
                mac = "No GPS";
            }

            boolean notUpdated = pinsUpdated == null || pinsUpdated.before(date);
            boolean include = unit != null && notUpdated;

            if (include) {
                count++;
                html.tr();
                html.td().content(cart.getCartNumber());
                html.td().content(mac);
                html.td().content(pins);
                html.td().content(lastWifi);
                html._tr();
            }
        }

        html.p().b().content("Total: ").div().content(count)._p();
        html._table();
    }

}
