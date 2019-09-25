/*
 * 
 */
package com.stayprime.basestation2.reporting;

import com.stayprime.util.EmailUtil;
import com.stayprime.util.FormatUtils;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class EmailReport {
    private static final Logger log = LoggerFactory.getLogger(CartConnectivityReport.class);

    private String reportEmails[];

    public void setReportEmails(String ... reportEmails) {
        this.reportEmails = reportEmails;
    }

    protected void sendReport(String subject, String output) {
        for (String email : reportEmails) {
            String response = EmailUtil.sendEmail(email, subject, output, true);
            log.info(response);
        }
    }

    public static String dateFormat(Date d) {
        if (d == null) {
            return StringUtils.EMPTY;
        }
        else {
            return FormatUtils.dateTimeFormat.format(d);
        }
    }

}
