/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.ui.reports;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author nirajcj
 */
public class ReportWorker extends SwingWorker<JRViewer, Integer> {

    private static void failIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Interrupted while generating report");
        }
    }

    private JRViewer jrViewer;
    private final Map parameters;
    private final String reportLocation;
    private final Connection jdbcConnection;

    public ReportWorker(Map parameters, String reportLocation, JRViewer jrViewer, Connection jdbConnection) {
        this.jrViewer = jrViewer;
        this.parameters = parameters;
        this.reportLocation = reportLocation;
        this.jdbcConnection = jdbConnection;
    }

    @Override
    protected JRViewer doInBackground() throws Exception {

        InputStream reportStream = ReportScreen.class.getResourceAsStream(reportLocation);
        setProgress(10);
        ReportWorker.failIfInterrupted();
        JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
        setProgress(30);
        ReportWorker.failIfInterrupted();
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        setProgress(60);
        ReportWorker.failIfInterrupted();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jdbcConnection);
        setProgress(90);
        ReportWorker.failIfInterrupted();
        jrViewer = new JRViewer(jasperPrint);

        setProgress(100);

        return jrViewer;
    }

}
