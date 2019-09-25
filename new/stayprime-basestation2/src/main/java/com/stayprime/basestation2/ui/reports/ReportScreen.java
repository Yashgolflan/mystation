/*
 * Created by JFormDesigner on Thu Apr 23 16:45:01 GST 2015
 */
package com.stayprime.basestation2.ui.reports;

import com.ezware.dialog.task.TaskDialogs;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.mysql.jdbc.Connection;
import com.stayprime.legacy.screen.Screen;
import com.stayprime.legacy.screen.ScreenParent;
import com.stayprime.storage.util.PersistenceUtil;
import com.stayprime.util.ConfigUtil;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import net.sf.jasperreports.swing.JRViewer;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jdesktop.swingx.JXDatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nirajcj
 */
public class ReportScreen extends JPanel implements Screen {
    private static final Logger log = LoggerFactory.getLogger(ReportScreen.class);

    private JFileChooser directoryChooser;
    private ScreenParent screenParent;
    private ReportWorker reportWorker = null;
    private JRViewer jrViewer;
    private Component componentArray[];
    private Timestamp dateParameter1;
    private Timestamp dateParameter2;

    public ReportScreen() {
        initComponents();
    }

    private void generateReportButtonActionPerformed(ActionEvent e) {

        loadReport();

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        if (reportWorker != null) {
            reportWorker.cancel(true);
        }
        progressBar1.setVisible(false);
    }

    private void webButtonActionPerformed(ActionEvent e) {

        directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int status = directoryChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = directoryChooser.getSelectedFile();
//            webButton.setIcon(FileUtils.getFileIcon(selectedFile));
//            webButton.setText(FileUtils.getDisplayFileName(selectedFile));
        } else if (status == JFileChooser.CANCEL_OPTION) {
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        contentPanel = new JPanel();
        label3 = new JLabel();
        label2 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        panel1 = new JPanel();
        cmbReportType = new JComboBox();
        xDatePicker1 = new JXDatePicker(new Date());
        xDatePicker2 = new JXDatePicker(new Date());
        chkEmail = new JCheckBox();
        generateReportButton = new JButton();
        cancelButton = new JButton();
        progressBar1 = new JProgressBar();
        progressBar1.setVisible(false);

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== contentPanel ========
        {
            contentPanel.setLayout(new GridBagLayout());
            ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- label3 ----
            label3.setText("Create Reports ");
            label3.setFont(new Font("Tahoma", Font.BOLD, 16));
            contentPanel.add(label3, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- label2 ----
            label2.setText("Report Type");
            contentPanel.add(label2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- label4 ----
            label4.setText("From");
            contentPanel.add(label4, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- label5 ----
            label5.setText("To");
            contentPanel.add(label5, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //======== panel1 ========
            {
                panel1.setLayout(new FormLayout(
                    "left:[140px,pref]:grow",
                    "fill:pref"));
                panel1.add(cmbReportType, CC.xy(1, 1));
            }
            contentPanel.add(panel1, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
            contentPanel.add(xDatePicker1, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
            contentPanel.add(xDatePicker2, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- chkEmail ----
            chkEmail.setText("Attach to Email");
            contentPanel.add(chkEmail, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- generateReportButton ----
            generateReportButton.setText("Generate Report");
            generateReportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateReportButtonActionPerformed(e);
                }
            });
            contentPanel.add(generateReportButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- cancelButton ----
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelButtonActionPerformed(e);
                }
            });
            contentPanel.add(cancelButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
            contentPanel.add(progressBar1, new GridBagConstraints(1, 4, 18, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        }
        add(contentPanel);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel contentPanel;
    private JLabel label3;
    private JLabel label2;
    private JLabel label4;
    private JLabel label5;
    private JPanel panel1;
    private JComboBox cmbReportType;
    private JXDatePicker xDatePicker1;
    private JXDatePicker xDatePicker2;
    private JCheckBox chkEmail;
    private JButton generateReportButton;
    private JButton cancelButton;
    private JProgressBar progressBar1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override
    public void enterScreen(ScreenParent screen) {
        this.screenParent = screen;
        init();
    }

    @Override
    public boolean exitScreen() {
        this.screenParent = null;
        return true;
    }

    @Override
    public Component getToolbarComponent() {
        return null;
    }

    private void notifyError(Throwable error) {
        TaskDialogs.showException(error);
    }

    private void removeJRViewer() {
        componentArray = contentPanel.getComponents();
        for (Component com : componentArray) {
            if (com.getClass().equals(JRViewer.class)) {
                contentPanel.remove(com);
            }
        }
    }

    private void removeProgressBar() {
        componentArray = contentPanel.getComponents();
        for (Component com : componentArray) {
            if (com.getClass().equals(JProgressBar.class)) {
                contentPanel.remove(com);
            }
        }
    }

    private void init() {
        cmbReportType.removeAllItems();
        for (ReportType rt : ReportType.values()) {
            cmbReportType.addItem(rt);
        }

    }

    private void loadReport() {
        jrViewer = null;
        generateReportButton.setEnabled(false);
        ReportType reportType = getReportTypeList();
        String reportLocation = "/com/stayprime/basestation2/resources/" + reportType.toString().toLowerCase() + ".jrxml";
        dateParameter1 = new Timestamp(xDatePicker1.getDate().getTime());
        dateParameter2 = new Timestamp(xDatePicker2.getDate().getTime());
        Map parameters = new HashMap();
        parameters.put("Title", reportType.toString() + " " + "REPORT");
        parameters.put("DateParameter1", dateParameter1);
        parameters.put("DateParameter2", dateParameter2);
        reportWorker = new ReportWorker(parameters, reportLocation, jrViewer, getJdbcConnection());
        reportWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case "progress":
                        progressBar1.setIndeterminate(false);
                        progressBar1.setValue((Integer) evt.getNewValue());
                        break;
                    case "state":
                        switch ((StateValue) evt.getNewValue()) {
                            case DONE:
                                progressBar1.setVisible(false);
                                generateReportButton.setEnabled(true);
                                try {
                                    jrViewer = reportWorker.get();
                                    removeJRViewer();

                                    contentPanel.add(jrViewer, new GridBagConstraints(1, 5, 20, GridBagConstraints.PAGE_END, 0.0, 0.0,
                                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                            new Insets(0, 0, 5, 5), 0, 0));

                                } catch (final CancellationException e) {
                                    //contentPanel.
                                    generateReportButton.setEnabled(true);

                                } catch (final Exception e) {
                                    generateReportButton.setEnabled(true);

                                }

                                reportWorker = null;
                                break;
                            case STARTED:
                            case PENDING:
                                progressBar1.setVisible(true);
                                progressBar1.setIndeterminate(true);
                                break;
                        }
                        break;
                }

            }

        });
        reportWorker.execute();
    }

    private Connection getJdbcConnection() {

        Connection con = null;
        try {
            PropertiesConfiguration config = ConfigUtil.load(new File("/opt/StayPrime/BaseStation/app.properties"), log);
            String dbName = config.getString(PersistenceUtil.config_dbName, "stayprime");
            String hostName = config.getString(PersistenceUtil.config_dbHostName, "192.168.0.7");
            String port = config.getString(PersistenceUtil.config_dbPort, "3306");
            String user = config.getString(PersistenceUtil.config_dbUser, "root");
            String password = config.getString(PersistenceUtil.config_dbPassword, "golfcart");
            String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName;

            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    private ReportType getReportTypeList() {

        ReportType type = (ReportType) cmbReportType.getSelectedItem();
        return type;
    }
}


