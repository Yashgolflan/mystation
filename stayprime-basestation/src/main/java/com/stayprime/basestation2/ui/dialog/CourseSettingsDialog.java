/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PaceOfPlaySettings.java
 *
 * Created on 16/06/2010, 11:30:46 AM
 */

package com.stayprime.basestation2.ui.dialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.ui.custom.JXTitledSeparator;
import com.stayprime.basestation2.util.NotificationPopup;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationAction;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 *
 * @author benjamin
 */
public class CourseSettingsDialog extends javax.swing.JDialog {
    private static final Logger log = LoggerFactory.getLogger(CourseSettingsDialog.class);

    private static final String applyAction = "applyAction";
    private static final String cancelAction = "cancelAction";
    private static final String loadSettings = "loadSettings";
    
//    private String popStartOnHoles;

    /** Creates new form PaceOfPlaySettings */
    public CourseSettingsDialog() {
        this(null, true);
    }

    public CourseSettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initActions();
//        setupHolesTable();
    }

    private void initActions() {
        ApplicationContext context = BaseStation2App.getApplication().getContext();
        ActionMap actionMap = context.getActionMap(CourseSettingsDialog.class, this);

//        applyButton.setAction(actionMap.get(applyAction));
//        cancelButton.setAction(actionMap.get(cancelAction));
    }
    public void readSettings() {
//        loadGolfClub();
        ApplicationAction settingsAction = (ApplicationAction) Application.getInstance().getContext()
                .getActionMap(this).get(loadSettings);
        settingsAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, loadSettings));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    private void initComponents() {
        ResourceBundle bundle = ResourceBundle.getBundle("com.stayprime.basestation2.ui.dialog.resources.CourseSettingsDialog");
        panel3 = new JPanel();
        paceOfPlayStartPanel = new JPanel();
        holesSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        startOnAllHolesCheckbox = new JCheckBox();
        selectionLabel = new JLabel();
        selectAllButton = new JButton();
        selectNoneButton = new JButton();
        holesScrollPane = new JScrollPane();
        holesTable = new JTable();
        panel1 = new JPanel();
        thresholdSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        disableCheckBox = new JCheckBox();
        addTimeLabel = new JLabel();
        addTimeField = new JSpinner();
        holeZoneDelayLabel = new JLabel();
        holeZoneDelayField = new JSpinner();

        //======== this ========
        setTitle("Pace of Play Settings");
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        setName("this");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel3 ========
        {
            panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel3.setName("panel3");
            panel3.setLayout(new BorderLayout());
        }
        contentPane.add(panel3, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());

        //======== paceOfPlayStartPanel ========
        {
            paceOfPlayStartPanel.setName("paceOfPlayStartPanel");
            paceOfPlayStartPanel.setLayout(new FormLayout(
                "default, $glue, default, 2*($lcgap, $button)",
                "fill:default, $lgap, default, $lgap, fill:default:grow"));

            //---- holesSeparator ----
            holesSeparator.setHorizontalAlignment(SwingConstants.CENTER);
            holesSeparator.setHorizontalTextPosition(SwingConstants.CENTER);
            holesSeparator.setName("holesSeparator");
            holesSeparator.setTitle(bundle.getString("holesSeparator.title"));
            paceOfPlayStartPanel.add(holesSeparator, CC.xywh(1, 1, 7, 1));

            //---- startOnAllHolesCheckbox ----
            startOnAllHolesCheckbox.setText(bundle.getString("startOnAllHolesCheckbox.text"));
            startOnAllHolesCheckbox.setName("startOnAllHolesCheckbox");
            paceOfPlayStartPanel.add(startOnAllHolesCheckbox, CC.xy(1, 3));

            //---- selectionLabel ----
            selectionLabel.setText(bundle.getString("selectionLabel.text"));
            selectionLabel.setName("selectionLabel");
            paceOfPlayStartPanel.add(selectionLabel, CC.xy(3, 3));

            //---- selectAllButton ----
            selectAllButton.setText(bundle.getString("selectAllButton.text"));
            selectAllButton.setName("selectAllButton");
            selectAllButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectAllButtonActionPerformed(e);
                }
            });
            paceOfPlayStartPanel.add(selectAllButton, CC.xy(5, 3));

            //---- selectNoneButton ----
            selectNoneButton.setText(bundle.getString("selectNoneButton.text"));
            selectNoneButton.setName("selectNoneButton");
            selectNoneButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectNoneButtonActionPerformed(e);
                }
            });
            paceOfPlayStartPanel.add(selectNoneButton, CC.xy(7, 3));

            //======== holesScrollPane ========
            {
                holesScrollPane.setName("holesScrollPane");
                holesScrollPane.setPreferredSize(new Dimension(300, 150));

                //---- holesTable ----
                holesTable.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "Course", "Hole", "Starts pace of play"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Object.class, Object.class, Boolean.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, true
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                holesTable.setName("holesTable");
                holesScrollPane.setViewportView(holesTable);
            }
            paceOfPlayStartPanel.add(holesScrollPane, CC.xywh(1, 5, 7, 1));
        }

        //======== panel1 ========
        {
            panel1.setName("panel1");
            panel1.setLayout(new FormLayout(
                "right:default, $lcgap, left:default:grow",
                "3*(fill:default, $lgap), fill:default"));

            //---- thresholdSeparator ----
            thresholdSeparator.setHorizontalAlignment(SwingConstants.CENTER);
            thresholdSeparator.setHorizontalTextPosition(SwingConstants.CENTER);
            thresholdSeparator.setName("thresholdSeparator");
            thresholdSeparator.setTitle(bundle.getString("CourseSettingsDialog.thresholdSeparator.title"));
            panel1.add(thresholdSeparator, CC.xywh(1, 1, 3, 1));

            //---- disableCheckBox ----
            disableCheckBox.setText("Disable pace of play");
            disableCheckBox.setName("disableCheckBox");
            panel1.add(disableCheckBox, CC.xywh(1, 3, 3, 1));

            //---- addTimeLabel ----
            addTimeLabel.setText(bundle.getString("addTimeLabel.text"));
            addTimeLabel.setName("addTimeLabel");
            panel1.add(addTimeLabel, CC.xy(1, 7));

            //---- addTimeField ----
            addTimeField.setModel(new SpinnerDateModel(new java.util.Date(-55855843200000L), null, null, java.util.Calendar.SECOND));
            addTimeField.setName("addTimeField");
            panel1.add(addTimeField, CC.xy(3, 7));

            //---- holeZoneDelayLabel ----
            holeZoneDelayLabel.setText(bundle.getString("holeZoneDelayLabel.text"));
            holeZoneDelayLabel.setName("holeZoneDelayLabel");
            panel1.add(holeZoneDelayLabel, CC.xy(1, 5));

            //---- holeZoneDelayField ----
            holeZoneDelayField.setModel(new SpinnerDateModel(new java.util.Date(-55855843200000L), null, null, java.util.Calendar.SECOND));
            holeZoneDelayField.setName("holeZoneDelayField");
            panel1.add(holeZoneDelayField, CC.xy(3, 5));
        }

        //---- bindings ----
        bindingGroup = new BindingGroup();
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            startOnAllHolesCheckbox, ELProperty.create("${!selected}"),
            holesTable, BeanProperty.create("enabled")));
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            startOnAllHolesCheckbox, ELProperty.create("${!selected}"),
            selectNoneButton, BeanProperty.create("enabled")));
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            startOnAllHolesCheckbox, ELProperty.create("${!selected}"),
            selectAllButton, BeanProperty.create("enabled")));
        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void cancelAction() {
        setVisible(false);
    }

    @Action
    public Task applyAction() {
        return new ApplyActionTask(org.jdesktop.application.Application.getInstance());
    }

    private class ApplyActionTask extends org.jdesktop.application.Task<Object, Void> {
        boolean popDisabled, startOnAllHoles;
        int addSeconds, holeZoneDelay;
        String startOnHoles, popThresholds;
        ApplyActionTask(org.jdesktop.application.Application app) {
            super(app);
//            loadDisabledValues();
//            Integer caution = (Integer) cautionField.getValue();
//            Integer warning = (Integer) warningField.getValue();
//            popThresholds = (caution * -60) + "," + (warning * -60);
        }
        @Override protected Object doInBackground() {
            try {
//                saveDisabledSettings();
                CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();
                courseSettingsManager.setPaceOfPlayThreshold(popThresholds);
                return null;
            }
            catch(Exception ex) {
                return new RuntimeException("Error saving settings", ex);
            }
        }
        @Override protected void succeeded(Object result) {
            if(result instanceof Exception) {
//                TaskDialogs.showException((Throwable) result);
                NotificationPopup.showErrorDialog("Error saving settings");
                log.error(result.toString());
            }
            else {
                setVisible(false);
            }
        }
    }

    @Action
    public Task loadSettings() {
        return new LoadSettingsTask(Application.getInstance());
    }

    private class LoadSettingsTask extends org.jdesktop.application.Task<Object, Void> {
//        boolean popDisabled, startOnAllHoles;
//        int addSeconds, holeZoneDelay;
//        String startOnHoles;
        String popThresholds;
        LoadSettingsTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @Override protected Object doInBackground() {
            try {
                CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();
//                popDisabled = courseSettingsManager.getGlobalPaceOfPlayDisable();
//                startOnAllHoles = courseSettingsManager.getPaceOfPlayStartOnAllHoles();
//                addSeconds = courseSettingsManager.getGlobalPaceOfPlayAddSeconds();
//                holeZoneDelay = courseSettingsManager.getHoleZoneDelay();
//                startOnHoles = courseSettingsManager.getPaceOfPlayStartOnHoles();
                popThresholds = courseSettingsManager.getPaceOfPlayThreshold();
                return null;
            }
            catch(Exception ex) {
                return new RuntimeException("Error loading settings", ex);
            }
        }
        @Override protected void succeeded(Object result) {
            if(result instanceof Exception) {
//                TaskDialogs.showException((Throwable) result);
                NotificationPopup.showErrorDialog("Error loading settings");
                log.error(result.toString());
            }
            else {
//                String thresholds[] = popThresholds.split(",");
//                if(thresholds.length  == 2) {
//                    int threshold = Integer.parseInt(thresholds[0]) * -1;
//                    int minutes = threshold / 60;
//                    cautionField.setValue(minutes);
//
//                    threshold = Integer.parseInt(thresholds[1]) * -1;
//                    minutes = threshold / 60;
//                    warningField.setValue(minutes);
//                }

//                int minutes = addSeconds / 60, seconds = addSeconds % 60;
//                Calendar cal = new GregorianCalendar(0,0,0,0,minutes,seconds);
//                addTimeField.setValue(cal.getTime());
//                disableCheckBox.setSelected(popDisabled);
//                startOnAllHolesCheckbox.setSelected(startOnAllHoles);
//                fillPopStartOnHolesTable(startOnHoles);
//                minutes = holeZoneDelay / 60;
//                seconds = holeZoneDelay % 60;
//                Calendar cal = new GregorianCalendar(0,0,0,0,minutes,seconds);
//                holeZoneDelayField.setValue(cal.getTime());
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel3;
    private JPanel paceOfPlayStartPanel;
    private JXTitledSeparator holesSeparator;
    private JCheckBox startOnAllHolesCheckbox;
    private JLabel selectionLabel;
    private JButton selectAllButton;
    private JButton selectNoneButton;
    private JScrollPane holesScrollPane;
    private JTable holesTable;
    private JPanel panel1;
    private JXTitledSeparator thresholdSeparator;
    private JCheckBox disableCheckBox;
    private JLabel addTimeLabel;
    private JSpinner addTimeField;
    private JLabel holeZoneDelayLabel;
    private JSpinner holeZoneDelayField;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

//    private void loadDisabledValues() {
//        Calendar addSecondsCal = new GregorianCalendar();
//        addSecondsCal.setTime((Date)addTimeField.getValue());
//        addSeconds = addSecondsCal.get(Calendar.MINUTE)*60 + addSecondsCal.get(Calendar.SECOND);
//
//        Calendar holeZoneCal = new GregorianCalendar();
//        holeZoneCal.setTime((Date)holeZoneDelayField.getValue());
//        holeZoneDelay = holeZoneCal.get(Calendar.MINUTE)*60 + holeZoneCal.get(Calendar.SECOND);
//
//        popDisabled = disableCheckBox.isSelected();
//
//        startOnAllHoles = startOnAllHolesCheckbox.isSelected();
//        startOnHoles = getPopStartOnHolesString();
//    }
//
//    private void saveDisabledSettings() {
//        courseSettingsManager.setGlobalPaceOfPlayDisable(popDisabled);
//        courseSettingsManager.setGlobalPaceOfPlayAddSeconds(addSeconds);
//        courseSettingsManager.setHoleZoneDelay(holeZoneDelay);
//        courseSettingsManager.setPaceOfPlayStartOnHoles(startOnHoles);
//        courseSettingsManager.setPaceOfPlayStartOnAllHoles(startOnAllHoles);
//    }
//    
//    private void setupHolesTable() {
//        UIUtil.calcColumnWidths(holesTable);
//        holesTable.getModel().addTableModelListener(new TableModelListener() {
//            public void tableChanged(TableModelEvent e) {
//                popStartOnHoles = getPopStartOnHolesString();
//            }
//        });
//    }
//
//    public void loadGolfClub() {
//        setGolfClub(BaseStation2App.getApplication().getManager().getGolfClub());
//    }
//
//    public void setGolfClub(GolfClub golfClub) {
//        String holes = popStartOnHoles;
//        DefaultTableModel model = (DefaultTableModel) holesTable.getModel();
//        model.setRowCount(0);
//        for(CourseDefinition course: golfClub.getCourses()) {
//            for(int n = 1; n <= course.getHoleCount(); n++) {
//                model.addRow(new Object[] {course, n, false});
//            }
//        }
//        fillPopStartOnHolesTable(holes);
//    }
//
//    private void fillPopStartOnHolesTable(String startOnHoles) {
//        for(int row = 0; row < holesTable.getRowCount(); row++)
//            holesTable.setValueAt(false, row, 2);
//
//        if(startOnHoles != null) {
//            String startCourses[] = startOnHoles.split(";");
//
//            for(String startCourse: startCourses) {
//                if(startCourse.indexOf(":") > 0) {
//                    String startHoles[] = startCourse.substring(1+startCourse.indexOf(":")).split(",");
//                    for(String startHole: startHoles) {
//                        for(int row = 0; row < holesTable.getRowCount(); row++) {
//                            CourseDefinition course = (CourseDefinition) holesTable.getValueAt(row, 0);
//                            int hole = (Integer) holesTable.getValueAt(row, 1);
//
//                            if(Integer.parseInt(startCourse.substring(0, startCourse.indexOf(":"))) == course.getCourseNumber()
//                                    && hole == Integer.parseInt(startHole)) {
//                                holesTable.setValueAt(true, row, 2);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        popStartOnHoles = startOnHoles;
//    }
//
//    private String getPopStartOnHolesString() {
//        StringBuilder string = new StringBuilder();
//        int currentCourse = -1, currentHole = -1;
//        DefaultTableModel model = (DefaultTableModel) holesTable.getModel();
//        for(int row = 0; row < model.getRowCount(); row++) {
//            if((Boolean) model.getValueAt(row, 2)) {
//                CourseDefinition course = (CourseDefinition) holesTable.getValueAt(row, 0);
//                if(currentCourse != course.getCourseNumber()) {
//                    if(currentCourse != -1)
//                        string.append(";");
//                    currentCourse = course.getCourseNumber();
//                    string.append(currentCourse);
//                    string.append(":");
//                    currentHole = -1;
//                }
//
//                if(currentHole != -1)
//                    string.append(",");
//                currentHole = (Integer) model.getValueAt(row, 1);
//                string.append(currentHole);
//            }
//        }
//
//        return string.toString();
//    }
    private void selectNoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectNoneButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) holesTable.getModel();
        for(int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(false, row, 2);
        }
    }

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) holesTable.getModel();
        for(int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(true, row, 2);
        }
    }
}