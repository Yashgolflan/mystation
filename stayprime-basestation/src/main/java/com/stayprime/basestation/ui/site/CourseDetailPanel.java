/*
 * 
 */
package com.stayprime.basestation.ui.site;

import java.awt.*;
import com.stayprime.basestation.ui.FileChooser;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.stayprime.ui.editor.EditorPanel;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.hibernate.entities.Courses;
import com.stayprime.hibernate.entities.Holes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.ObjectUtils;

/**
 *
 * @author benjamin
 */
public class CourseDetailPanel extends EditorPanel<Courses> {

    private static final String HOLE_NUM_CHAR = "#";

    private static final String DOT_CHAR = ".";

    private FileChooser fileChooser;

    /**
     * Creates new form HoleDetailPanel
     */
    public CourseDetailPanel() {
        initComponents();
        addListeners();
    }

    private void addListeners() {
    }

    public void setFileChooser(FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    @Override
    public void refresh() {
        Courses course = getEditingObject();
        boolean enabled = course != null;
        selectMapButton.setEnabled(enabled);
        clearMapButton.setEnabled(enabled);
        selectFlybyButton.setEnabled(enabled);
        clearFlybyButton.setEnabled(enabled);

        updateField(mapField);
        updateField(flybyField);
    }

    private void updateField(JTextField field) {
        Courses course = getEditingObject();
        boolean allNull = true;
        boolean allEqual = true;
        if (course != null && CollectionUtils.isNotEmpty(course.getHoleses())) {
            File first = getFileForField(course.getHoleses().iterator().next(), field);
            String pattern = getFilePattern(first);

            for (Holes hole : course.getHoleses()) {
                File holeFile = getFileForField(hole, field);
                if (holeFile != null) {
                    allNull = false;
                    boolean sameParent = first != null && ObjectUtils.equals(holeFile.getParent(), first.getParent());
                    boolean samePattern = ObjectUtils.equals(pattern, getFilePattern(holeFile));
                    if (sameParent == false || samePattern == false) {
                        allEqual = false;
                        break;
                    }
                }
            }

            if (allNull == false) {
                if (allEqual) {
                    field.setText(getParents(first) + pattern);
                }
                else {
                    field.setText("Different values");
                }
            }
        }

        if (allNull) {
            field.setText(null);
        }
    }

    private File getFileForField(Holes hole, JTextField field) {
        if (hole != null) {
            BasicMapImage map = hole.getMap();
            if (field == mapField && map != null && map.getImageAddress() != null) {
                return new File(map.getImageAddress());
            }
            else if (field == flybyField && hole.getFlyoverImage() != null) {
                return new File(hole.getFlyoverImage());
            }
        }

        return null;
    }

    private void selectMapButtonActionPerformed() {
        File file = fileChooser.selectMap(this);
        if (file != null) {
            setHolesProperty(mapField, file);
        }
    }

    private void clearMapButtonActionPerformed() {
            clearHolesProperty(mapField);
    }

    private void selectFlybyButtonActionPerformed() {
        File file = fileChooser.selectFlyby(this);
        if (file != null) {
            setHolesProperty(flybyField, file);
        }
    }

    private void clearFlybyButtonActionPerformed() {
        clearHolesProperty(flybyField);
    }

    /**
     * Sets mapImage or flyover property on all the holes based on one file.
     * @param field either mapField or flybyField
     * @param firstFile the base file to set all the holes
     */
    private void setHolesProperty(JTextField field, File firstFile) {
        Courses course = getEditingObject();
        File parent = firstFile.getParentFile();
        String pattern = getFilePattern(firstFile);
        if (course != null && pattern != null) {
            for (Holes hole : course.getHoleses()) {
                String holeNumber = Integer.toString(hole.getId().getHole());
                String fileName = pattern.replace(HOLE_NUM_CHAR, holeNumber);
                File file = new File(parent, fileName);

                field.setText(getParents(file) + pattern);

                if (field == mapField) {
                    BasicMapImage map = fileChooser.loadMap(file);
                    if (map != null) {
                        hole.setMap(map);
                    }
                }
                else if (field == flybyField) {
                    hole.setFlyoverImage(file.getAbsolutePath());
                }
                getParentEditorPanel().applyChanges();
            }
        }
        setModified(true);
    }

    /**
     * Clear mapImage or flyover property on all the holes
     * @param field either mapField or flybyField
     */
    private void clearHolesProperty(JTextField field) {
        Courses course = getEditingObject();
        if (course != null) {
            for (Holes hole: course.getHoleses()) {
                field.setText(null);

                if (field == mapField) {
                    hole.setMapImage(null);
                }
                else if (field == flybyField) {
                    hole.setFlyoverImage(null);
                }
            }
        }
        setModified(true);
    }

    @Override
    public boolean applyChanges() {
        if (getEditingObject() != null) {
        }
        return super.applyChanges();
    }

    private String getFilePattern(File file) {
        if (file != null) {
            String name = file.getName();
            int dot = name.lastIndexOf(DOT_CHAR);
            if (dot > 0 && CharUtils.isAsciiNumeric(name.charAt(dot - 1))) {
                int numberStart = dot - 1;
                while (numberStart > 0 && CharUtils.isAsciiNumeric(name.charAt(numberStart - 1))) {
                    numberStart--;
                }
                return new StringBuilder(name).replace(numberStart, dot, HOLE_NUM_CHAR).toString();
            }
        }

        return null;
    }

    private String getParents(File file) {
        File parent = file.getParentFile();
        return parent.getAbsolutePath() + "/";
//        File parent2 = parent.getParentFile();
//        String parent2Name = parent2 == null? "" : parent2.getName();
//        return parent2Name + "/" + parent.getName() + "/";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    private void initComponents() {
        separator1 = new JLabel();
        mapLabel = new JLabel();
        mapField = new JTextField();
        selectMapButton = new JButton();
        clearMapButton = new JButton();
        flybyLabel = new JLabel();
        flybyField = new JTextField();
        selectFlybyButton = new JButton();
        clearFlybyButton = new JButton();

        //======== this ========
        setName("Form");
        setLayout(new FormLayout(
            "right:pref, $lcgap, default:grow, 2*($lcgap, default)",
            "default, $lgap, default, $rgap, default"));

        //---- separator1 ----
        separator1.setText("Course details");
        separator1.setFont(separator1.getFont().deriveFont(separator1.getFont().getStyle() | Font.BOLD));
        separator1.setName("separator1");
        add(separator1, CC.xywh(1, 1, 7, 1));

        //---- mapLabel ----
        mapLabel.setText("Maps:");
        mapLabel.setName("mapLabel");
        add(mapLabel, CC.xy(1, 3));

        //---- mapField ----
        mapField.setEditable(false);
        mapField.setName("mapField");
        add(mapField, CC.xy(3, 3));

        //---- selectMapButton ----
        selectMapButton.setText("Load");
        selectMapButton.setName("selectMapButton");
        selectMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMapButtonActionPerformed();
            }
        });
        add(selectMapButton, CC.xy(5, 3));

        //---- clearMapButton ----
        clearMapButton.setText("<html>&nbsp;X&nbsp;");
        clearMapButton.setName("clearMapButton");
        clearMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMapButtonActionPerformed();
            }
        });
        add(clearMapButton, CC.xy(7, 3));

        //---- flybyLabel ----
        flybyLabel.setText("Flybys:");
        flybyLabel.setName("flybyLabel");
        add(flybyLabel, CC.xy(1, 5));

        //---- flybyField ----
        flybyField.setEditable(false);
        flybyField.setName("flybyField");
        add(flybyField, CC.xy(3, 5));

        //---- selectFlybyButton ----
        selectFlybyButton.setText("Load");
        selectFlybyButton.setName("selectFlybyButton");
        selectFlybyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFlybyButtonActionPerformed();
            }
        });
        add(selectFlybyButton, CC.xy(5, 5));

        //---- clearFlybyButton ----
        clearFlybyButton.setText("<html>&nbsp;X&nbsp;");
        clearFlybyButton.setName("clearFlybyButton");
        clearFlybyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFlybyButtonActionPerformed();
            }
        });
        add(clearFlybyButton, CC.xy(7, 5));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel separator1;
    private JLabel mapLabel;
    private JTextField mapField;
    private JButton selectMapButton;
    private JButton clearMapButton;
    private JLabel flybyLabel;
    private JTextField flybyField;
    private JButton selectFlybyButton;
    private JButton clearFlybyButton;
    // End of variables declaration//GEN-END:variables

}
