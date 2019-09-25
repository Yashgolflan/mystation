/*
 * Created by JFormDesigner on Tue Oct 13 13:55:12 GST 2015
 */
package com.stayprime.basestation2.ui.dialog;

import java.awt.event.*;
import com.ezware.dialog.task.TaskDialogs;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.basestation2.ui.custom.JXTitledSeparator;
import com.stayprime.basestation2.util.NotificationPopup;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

/**
 * @author Benjamin Baron
 */
public class PopThresholdSettingsPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(CourseSettingsDialog.class);

    public PopThresholdSettingsPanel() {
        initComponents();
        final JTextField cautionFieldtxt = ((JSpinner.DefaultEditor) cautionField.getEditor()).getTextField();
        cautionFieldtxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

                try {
                    char c = e.getKeyChar();
                    String a = cautionFieldtxt.getText();
                    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                        e.consume();
                        //getToolkit().beep();

                    }
                    // System.out.println(a);
//                    Robot robot = new Robot();
//                    robot.keyRelease(e.getKeyCode());
                } catch (Exception ex) { //Not a number in text field -> do nothing 
                    e.consume();
                }
            }

        });
        final JTextField warningFieldtxt =((JSpinner.DefaultEditor) warningField.getEditor()).getTextField();
        cautionFieldtxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

                try {
                    char c = e.getKeyChar();
                    String a = warningFieldtxt.getText();
                    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                        e.consume();
                        //getToolkit().beep();

                    }
                    // System.out.println(a);
//                    Robot robot = new Robot();
//                    robot.keyRelease(e.getKeyCode());
                } catch (Exception ex) { //Not a number in text field -> do nothing 
                    e.consume();
                }
            }

        });
    }

    public void init() {
    }

    public void loadSettings() {
        LoadSettingsTask task = new LoadSettingsTask(Application.getInstance());
        BaseStation2App.getApplication().runTask(task);
    }

    private void applySettings() {
        ApplyActionTask task = new ApplyActionTask(Application.getInstance(BaseStation2App.class));
        ApplicationContext context = BaseStation2App.getApplication().getContext();
        context.getTaskService().execute(task);
    }

    private class ApplyActionTask extends org.jdesktop.application.Task<Object, Void> {

        String popThresholds;

        ApplyActionTask(org.jdesktop.application.Application app) {
            super(app);
            Integer caution = (Integer) cautionField.getValue();
            Integer warning = (Integer) warningField.getValue();
            popThresholds = (caution * -60) + "," + (warning * -60);
        }

        @Override
        protected Object doInBackground() {
            try {
                CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();
                courseSettingsManager.setPaceOfPlayThreshold(popThresholds);
                return null;
            } catch (Exception ex) {
                return new RuntimeException("Error saving settings", ex);
            }
        }

        @Override
        protected void succeeded(Object result) {
            if (result instanceof Exception) {
//                TaskDialogs.showException((Throwable) result);
                NotificationPopup.showErrorDialog("Error saving settings");
                log.error(result.toString());
            }
        }
    }

    private class LoadSettingsTask extends org.jdesktop.application.Task<Object, Void> {

        String popThresholds;

        LoadSettingsTask(org.jdesktop.application.Application app) {
            super(app);
        }

        @Override
        protected Object doInBackground() {
            try {
                CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();
                popThresholds = courseSettingsManager.getPaceOfPlayThreshold();
                return null;
            } catch (Exception ex) {
                return new RuntimeException("Error loading settings", ex);
            }
        }

        @Override
        protected void succeeded(Object result) {
            if (result instanceof Exception) {
//                TaskDialogs.showException((Throwable) result);
                NotificationPopup.showErrorDialog("Error loading settings");
                log.error(result.toString());
            } else {
                String thresholds[] = popThresholds.split(",");
                if (thresholds.length == 2) {

                    int threshold = Integer.parseInt(thresholds[0]) * -1;
                    int minutes = threshold / 60;
                    cautionField.setValue(minutes);

                    threshold = Integer.parseInt(thresholds[1]) * -1;
                    minutes = threshold / 60;
                    warningField.setValue(minutes);
                }

            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - YASH KHANDELWAL
        ResourceBundle bundle = ResourceBundle.getBundle("com.stayprime.basestation2.ui.dialog.resources.PopThresholdSettingsPanel");
        timeBehindLabel = new JXTitledSeparator();
        cautionLabel = new JLabel();
        cautionField = new JSpinner();
        minutesLabel1 = new JLabel();
        warningLabel = new JLabel();
        warningField = new JSpinner();
        minutesLabel2 = new JLabel();
        applyButton = new JButton();
        cancelButton = new JButton();

        //======== this ========

        // JFormDesigner evaluation mark
        setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

        setLayout(new FormLayout(
            "right:default, $lcgap, left:default, $lcgap, default:grow, $lcgap, default, $lcgap, left:default, $lcgap, default:grow",
            "2*(fill:default, 10px), default"));

        //---- timeBehindLabel ----
        timeBehindLabel.setName("timeBehindLabel");
        timeBehindLabel.setTitle("Behind Pace of Play caution and warning ");
        timeBehindLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(timeBehindLabel, CC.xywh(1, 1, 11, 1));

        //---- cautionLabel ----
        cautionLabel.setText(bundle.getString("PopThresholdSettingsPanel.cautionLabel.text"));
        cautionLabel.setName("cautionLabel");
        add(cautionLabel, CC.xy(1, 3));

        //---- cautionField ----
        cautionField.setModel(new SpinnerNumberModel(0, 0, 60, 1));
        cautionField.setName("cautionField");
        add(cautionField, CC.xy(3, 3));

        //---- minutesLabel1 ----
        minutesLabel1.setText("minutes");
        add(minutesLabel1, CC.xy(5, 3));

        //---- warningLabel ----
        warningLabel.setText(bundle.getString("PopThresholdSettingsPanel.warningLabel.text"));
        warningLabel.setName("warningLabel");
        add(warningLabel, CC.xy(7, 3));

        //---- warningField ----
        warningField.setModel(new SpinnerNumberModel(10, 0, 60, 1));
        warningField.setName("warningField");
        add(warningField, CC.xy(9, 3));

        //---- minutesLabel2 ----
        minutesLabel2.setText("minutes");
        add(minutesLabel2, CC.xy(11, 3));

        //---- applyButton ----
        applyButton.setName("applyButton");
        applyButton.setAction(null);
        applyButton.setText(bundle.getString("PopThresholdSettingsPanel.applyButton.text"));
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applySettings();
            }
        });
        add(applyButton, CC.xy(11, 5));

        //---- cancelButton ----
        cancelButton.setText(bundle.getString("PopThresholdSettingsPanel.cancelButton.text"));
        cancelButton.setName("cancelButton");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - YASH KHANDELWAL
    private JXTitledSeparator timeBehindLabel;
    private JLabel cautionLabel;
    private JSpinner cautionField;
    private JLabel minutesLabel1;
    private JLabel warningLabel;
    private JSpinner warningField;
    private JLabel minutesLabel2;
    private JButton applyButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
