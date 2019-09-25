/*
 * Created by JFormDesigner on Sun Feb 08 23:46:27 GST 2015
 */

package com.stayprime.basestation.ui.site;

import java.awt.*;
import com.ezware.dialog.task.TaskDialogs;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.stayprime.basestation.ui.FileChooser;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.hibernate.entities.CourseSettings;
import com.stayprime.hibernate.entities.Courses;
import com.stayprime.legacy.screen.Screen;
import com.stayprime.legacy.screen.ScreenParent;
import com.stayprime.ui.editor.EditorPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingx.*;

/**
 * @author Benjamin Baron
 */
public class GolfSiteSetupScreen extends EditorPanel implements Screen {
    private CourseInfo golfSite;
    private CourseSetupStorageTasks storageTasks;
    private ScreenParent screenParent;
    private List<CourseSettings> settings;

    public GolfSiteSetupScreen() {
        initComponents();
        addNestedEditorPanel(golfSiteEditor);
        addNestedEditorPanel(courseListEditor);
        courseListEditor.addNestedEditorPanel(courseDetailPanel1);
        courseListEditor.addNestedEditorPanel(holeListEditor);
        holeListEditor.addNestedEditorPanel(holeDetailPanel);
    }

    public void setCourseService(CourseService courseService) {
        storageTasks = new CourseSetupStorageTasks(courseService, this);
    }

    public void setFileChooser(FileChooser fileChooser) {
        golfSiteEditor.setFileChooser(fileChooser);
        courseDetailPanel1.setFileChooser(fileChooser);
        holeDetailPanel.setFileChooser(fileChooser);
    }

    public CourseInfo getGolfClub() {
        return golfSite;
    }

    public void setGolfSite(CourseInfo golfSite, List<Courses> courses, List<CourseSettings> settings) {
        setEditingObject(null);
        this.golfSite = golfSite;
        this.settings = settings;
        golfSiteEditor.setCourseSettings(settings);
        golfSiteEditor.setEditingObject(golfSite);
        courseListEditor.setList(courses);
        setEditingObject(golfSite);
    }

    void loadingFailed(Throwable cause) {
        TaskDialogs.showException(cause);
    }

    @Override
    public void saveChanges() {
        applyChanges();
        storageTasks.saveCourseInfo(golfSite, courseListEditor.getSourceList(), golfSiteEditor.getModifiedSettings());
         //putClientProperty(EditorUtil.prop_exitScreen, Boolean.FALSE);
//        screenParent = parent;
    //    storageTasks.loadGolfSite();
        
    }

    void saveTaskDone(Throwable error) {
        if (error == null) {
            setModified(false);
         //   exitThisScreen();
            //boolean exit = (Boolean) getClientProperty(EditorUtil.prop_exitScreen);
            //if (exit && screenParent != null) {
            //screenParent.exitScreen(this);
            //}
        }
        else {
            putClientProperty(EditorUtil.prop_exitScreen, Boolean.FALSE);
            TaskDialogs.showException(error);
        }
    }

    private void exitThisScreen() {
        if (screenParent != null) {
            screenParent.exitScreen(this);
        }
    }

    @Override
    public void enterScreen(ScreenParent parent) {
        putClientProperty(EditorUtil.prop_exitScreen, Boolean.FALSE);
        screenParent = parent;
        storageTasks.loadGolfSite();
    }

    @Override
    public boolean exitScreen() {
	return EditorUtil.askSaveChanges(this);
    }

    @Override
    public Component getToolbarComponent() {
        return null;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - yash
        ResourceBundle bundle = ResourceBundle.getBundle("com.stayprime.basestation.ui.site.Bundle");
        scrollPane1 = new JScrollPane();
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        golfCourseSetupLabel = new JLabel();
        golfSiteEditor = new GolfSiteEditor();
        coursesLabel = new JLabel();
        courseListEditor = new CourseListEditor();
        courseDetailPanel1 = new CourseDetailPanel();
        holesLabel = new JLabel();
        holeListEditor = new HoleListEditor();
        holeDetailPanel = new HoleDetailPanel();
        buttonBar = new JPanel();
        saveButton = new JButton();
        cancelButton = new JButton();

        //======== this ========

        // JFormDesigner evaluation mark
        setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

        setLayout(new BorderLayout());

        //======== scrollPane1 ========
        {
            scrollPane1.setPreferredSize(new Dimension(700, 661));

            //======== dialogPane ========
            {
                dialogPane.setBorder(Borders.createEmptyBorder("7dlu, 7dlu, 7dlu, 7dlu"));
                dialogPane.setLayout(new FormLayout(
                    "default",
                    "fill:min:grow, fill:default"));

                //======== contentPanel ========
                {
                    contentPanel.setLayout(new FormLayout(
                        "182dlu, $lcgap, [177dlu,default,200dlu]",
                        "default, $lgap, default, $ugap, default, $lgap, fill:61dlu:grow(0.1), $ugap, 0dlu, $lgap, fill:default:grow(0.9)"));

                    //---- golfCourseSetupLabel ----
                    golfCourseSetupLabel.setText(bundle.getString("GolfCourseSetupScreen.golfCourseSetupLabel.text"));
                    golfCourseSetupLabel.setFont(golfCourseSetupLabel.getFont().deriveFont(golfCourseSetupLabel.getFont().getStyle() | Font.BOLD, golfCourseSetupLabel.getFont().getSize() + 6f));
                    contentPanel.add(golfCourseSetupLabel, CC.xy(1, 1));
                    contentPanel.add(golfSiteEditor, CC.xywh(1, 3, 3, 1, CC.DEFAULT, CC.TOP));

                    //---- coursesLabel ----
                    coursesLabel.setText(bundle.getString("GolfCourseSetupScreen.coursesLabel.text"));
                    coursesLabel.setFont(coursesLabel.getFont().deriveFont(coursesLabel.getFont().getStyle() | Font.BOLD, coursesLabel.getFont().getSize() + 3f));
                    contentPanel.add(coursesLabel, CC.xy(1, 5));

                    //---- courseListEditor ----
                    courseListEditor.setPreferredSize(new Dimension(130, 120));
                    courseListEditor.setMinimumSize(new Dimension(130, 120));
                    contentPanel.add(courseListEditor, CC.xy(1, 7));
                    contentPanel.add(courseDetailPanel1, CC.xywh(3, 5, 1, 3, CC.DEFAULT, CC.FILL));

                    //---- holesLabel ----
                    holesLabel.setText(bundle.getString("GolfCourseSetupScreen.holesLabel.text"));
                    holesLabel.setFont(holesLabel.getFont().deriveFont(holesLabel.getFont().getStyle() | Font.BOLD, holesLabel.getFont().getSize() + 3f));
                    contentPanel.add(holesLabel, CC.xy(1, 9));

                    //---- holeListEditor ----
                    holeListEditor.setPreferredSize(new Dimension(130, 150));
                    holeListEditor.setMinimumSize(new Dimension(130, 150));
                    contentPanel.add(holeListEditor, CC.xy(1, 11));

                    //---- holeDetailPanel ----
                    holeDetailPanel.setPreferredSize(new Dimension(177, 200));
                    holeDetailPanel.setMaximumSize(new Dimension(176, 600));
                    holeDetailPanel.setRequestFocusEnabled(false);
                    contentPanel.add(holeDetailPanel, CC.xywh(3, 9, 1, 3));
                }
                dialogPane.add(contentPanel, CC.xy(1, 1));

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.createEmptyBorder("5dlu, 0dlu, 0dlu, 0dlu"));
                    buttonBar.setLayout(new FormLayout(
                        "$glue, $button, $rgap, $button",
                        "pref"));

                    //---- saveButton ----
                    saveButton.setText(bundle.getString("GolfCourseSetupScreen.saveButton.text"));
                    saveButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            saveChanges();
                        }
                    });
                    buttonBar.add(saveButton, CC.xy(2, 1));

                    //---- cancelButton ----
                    cancelButton.setText(bundle.getString("GolfCourseSetupScreen.cancelButton.text"));
                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            exitThisScreen();
                        }
                    });
                    buttonBar.add(cancelButton, CC.xy(4, 1));
                }
                dialogPane.add(buttonBar, CC.xy(1, 2));
            }
            scrollPane1.setViewportView(dialogPane);
        }
        add(scrollPane1, BorderLayout.CENTER);

        //---- bindings ----
        bindingGroup = new BindingGroup();
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ,
            this, BeanProperty.create("modified"),
            saveButton, BeanProperty.create("enabled")));
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            courseListEditor, BeanProperty.create("selectedItem"),
            holeListEditor, BeanProperty.create("golfCourse")));
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ,
            holeListEditor, BeanProperty.create("selectedItem"),
            holeDetailPanel, BeanProperty.create("editingObject")));
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ,
            courseListEditor, BeanProperty.create("selectedItem"),
            courseDetailPanel1, BeanProperty.create("editingObject")));
        bindingGroup.bind();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - yash
    private JScrollPane scrollPane1;
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel golfCourseSetupLabel;
    private GolfSiteEditor golfSiteEditor;
    private JLabel coursesLabel;
    private CourseListEditor courseListEditor;
    private CourseDetailPanel courseDetailPanel1;
    private JLabel holesLabel;
    private HoleListEditor holeListEditor;
    private HoleDetailPanel holeDetailPanel;
    private JPanel buttonBar;
    private JButton saveButton;
    private JButton cancelButton;
    private BindingGroup bindingGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

