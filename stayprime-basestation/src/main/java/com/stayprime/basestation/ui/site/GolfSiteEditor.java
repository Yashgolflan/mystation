/*
 * Created by JFormDesigner on Sun Feb 08 21:50:30 GST 2015
 */

package com.stayprime.basestation.ui.site;

import com.stayprime.ui.editor.EditorPanel;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.stayprime.basestation.ui.FileChooser;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.hibernate.entities.CourseSettings;
import com.stayprime.localservice.Constants;
import java.io.File;
import org.apache.commons.lang.StringUtils;

/**
 * @author Benjamin Rodriguez
 */
public class GolfSiteEditor extends EditorPanel<CourseInfo> {

    private List<CourseSettings> settings;
    private CourseSettings[] modifiedSettings = new CourseSettings[0];
    private BasicMapImage mapImage;
    private FileChooser fileChooser;

    public GolfSiteEditor() {
        initComponents();
        addListeners();
    }

    public void setFileChooser(FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    private void addListeners() {
        nameField.getDocument().addDocumentListener(getModificationListener());
    }

    public void setCourseSettings(List<CourseSettings> settings) {
        this.settings = settings;
    }

    @Override
    public void refresh() {
        CourseInfo site = getEditingObject();
        nameField.setText(site == null? null : site.getName());
        setLogo(site == null? null : site.getLogoImage());
        setMap(site == null? null : site.getMap());
        setWelcome(CourseSettings.findValue(settings, Constants.welcomeImage));
        setThank(CourseSettings.findValue(settings, Constants.thankyouImage));
        modifiedSettings = new CourseSettings[0];
    }

    private void setMap(BasicMapImage mapImage) {
        this.mapImage = mapImage;
        mapField.setText(mapImage == null? null : mapImage.getImageAddress());
    }

    private void setLogo(String path) {
        logoField.setText(StringUtils.isBlank(path)? null : new File(path).getAbsolutePath());
    }

    private void setWelcome(String path) {
        welcomeField.setText(StringUtils.isBlank(path)? null : new File(path).getAbsolutePath());
    }

    private void setThank(String path) {
        thankField.setText(StringUtils.isBlank(path)? null : new File(path).getAbsolutePath());
    }

    @Override
    public boolean applyChanges() {
        CourseInfo site = getEditingObject();
        site.setName(nameField.getText());
        site.setLogoImage(logoField.getText());
        site.setMap(mapImage);
        setModifiedSettings();
        boolean modified = isModified();
        setModified(false);
        return modified;
    }

    private void setModifiedSettings() {
        Date now = new Date();
        CourseSettings cs[] = new CourseSettings[2];
        cs[0] = new CourseSettings(Constants.welcomeImage, StringUtils.stripToEmpty(welcomeField.getText()), now);
        cs[1] = new CourseSettings(Constants.thankyouImage, StringUtils.stripToEmpty(thankField.getText()), now);
        modifiedSettings = cs;
    }

    public CourseSettings[] getModifiedSettings() {
        return modifiedSettings;
    }

    private void logoLoadButtonActionPerformed() {
        File logo = fileChooser.selectImage(this);
        if (logo != null) {
            setLogo(logo.getAbsolutePath());
            setModified(true);
        }
    }

    private void logoClearButtonActionPerformed() {
        setLogo(null);
        setModified(true);
    }

    private void mapLoadButtonActionPerformed() {
        File file = fileChooser.selectMap(this);
        if (file != null) {
            BasicMapImage map = fileChooser.loadMap(file);
            if (map != null) {
                setMap(map);
                setModified(true);
            }
        }
    }

    private void mapClearButtonActionPerformed() {
        setMap(null);
        setModified(true);
    }

    private void welcomeLoadButtonActionPerformed() {
        File file = fileChooser.selectImage(this);
        if (file != null) {
            setWelcome(file.getAbsolutePath());
            setModified(true);
        }
    }

    private void welcomeClearButtonActionPerformed() {
        setWelcome(null);
        setModified(true);
    }

    private void thankLoadButtonActionPerformed() {
        File file = fileChooser.selectImage(this);
        if (file != null) {
            setThank(file.getAbsolutePath());
            setModified(true);
        }
    }

    private void thankClearButtonActionPerformed() {
        setThank(null);
        setModified(true);
    }

    /*
     * Bound properties
     */

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("com.stayprime.basestation.ui.site.Bundle");
        nameLabel = new JLabel();
        nameField = new JTextField();
        logoLabel = new JLabel();
        logoField = new JTextField();
        logoLoadButton = new JButton();
        logoClearButton = new JButton();
        welcomeLabel = new JLabel();
        welcomeField = new JTextField();
        welcomeLoadButton = new JButton();
        welcomeClearButton = new JButton();
        mapLabel = new JLabel();
        mapField = new JTextField();
        mapLoadButton = new JButton();
        mapClearButton = new JButton();
        thankLabel = new JLabel();
        thankField = new JTextField();
        thankLoadButton = new JButton();
        thankClearButton = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "right:default, $lcgap, 50dlu:grow, 2*($lcgap, default), $rgap, right:default, $lcgap, 50dlu:grow, 2*($lcgap, default)",
            "2*(default, $lgap), default"));

        //---- nameLabel ----
        nameLabel.setText(bundle.getString("GolfSiteEditor.nameLabel.text"));
        add(nameLabel, CC.xy(1, 1));
        add(nameField, CC.xywh(3, 1, 5, 1));

        //---- logoLabel ----
        logoLabel.setText(bundle.getString("GolfSiteEditor.logoLabel.text"));
        add(logoLabel, CC.xy(1, 3));

        //---- logoField ----
        logoField.setEditable(false);
        add(logoField, CC.xy(3, 3));

        //---- logoLoadButton ----
        logoLoadButton.setText(bundle.getString("GolfSiteEditor.logoLoadButton.text"));
        logoLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logoLoadButtonActionPerformed();
            }
        });
        add(logoLoadButton, CC.xy(5, 3));

        //---- logoClearButton ----
        logoClearButton.setText("<html>&nbsp;X&nbsp;");
        logoClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logoClearButtonActionPerformed();
            }
        });
        add(logoClearButton, CC.xy(7, 3));

        //---- welcomeLabel ----
        welcomeLabel.setText(bundle.getString("GolfSiteEditor.welcomeLabel.text"));
        add(welcomeLabel, CC.xy(9, 3));

        //---- welcomeField ----
        welcomeField.setEditable(false);
        add(welcomeField, CC.xy(11, 3));

        //---- welcomeLoadButton ----
        welcomeLoadButton.setText(bundle.getString("GolfSiteEditor.welcomeLoadButton.text"));
        welcomeLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomeLoadButtonActionPerformed();
            }
        });
        add(welcomeLoadButton, CC.xy(13, 3));

        //---- welcomeClearButton ----
        welcomeClearButton.setText("<html>&nbsp;X&nbsp;");
        welcomeClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomeClearButtonActionPerformed();
            }
        });
        add(welcomeClearButton, CC.xy(15, 3));

        //---- mapLabel ----
        mapLabel.setText(bundle.getString("GolfSiteEditor.mapLabel.text"));
        add(mapLabel, CC.xy(1, 5));

        //---- mapField ----
        mapField.setEditable(false);
        add(mapField, CC.xy(3, 5));

        //---- mapLoadButton ----
        mapLoadButton.setText(bundle.getString("GolfSiteEditor.mapLoadButton.text"));
        mapLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapLoadButtonActionPerformed();
            }
        });
        add(mapLoadButton, CC.xy(5, 5));

        //---- mapClearButton ----
        mapClearButton.setText("<html>&nbsp;X&nbsp;");
        mapClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapClearButtonActionPerformed();
            }
        });
        add(mapClearButton, CC.xy(7, 5));

        //---- thankLabel ----
        thankLabel.setText(bundle.getString("GolfSiteEditor.thankLabel.text"));
        add(thankLabel, CC.xy(9, 5));

        //---- thankField ----
        thankField.setEditable(false);
        add(thankField, CC.xy(11, 5));

        //---- thankLoadButton ----
        thankLoadButton.setText(bundle.getString("GolfSiteEditor.thankLoadButton.text"));
        thankLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thankLoadButtonActionPerformed();
            }
        });
        add(thankLoadButton, CC.xy(13, 5));

        //---- thankClearButton ----
        thankClearButton.setText("<html>&nbsp;X&nbsp;");
        thankClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thankClearButtonActionPerformed();
            }
        });
        add(thankClearButton, CC.xy(15, 5));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel logoLabel;
    private JTextField logoField;
    private JButton logoLoadButton;
    private JButton logoClearButton;
    private JLabel welcomeLabel;
    private JTextField welcomeField;
    private JButton welcomeLoadButton;
    private JButton welcomeClearButton;
    private JLabel mapLabel;
    private JTextField mapField;
    private JButton mapLoadButton;
    private JButton mapClearButton;
    private JLabel thankLabel;
    private JTextField thankField;
    private JButton thankLoadButton;
    private JButton thankClearButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
