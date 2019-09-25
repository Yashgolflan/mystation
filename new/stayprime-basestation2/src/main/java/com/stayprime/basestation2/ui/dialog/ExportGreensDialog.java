/*
 * Created by JFormDesigner on Sun Dec 06 17:58:57 GST 2015
 */

package com.stayprime.basestation2.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.stayprime.basestation.ui.FileChooser;
import java.io.File;

/**
 * @author Benjamin Baron
 */
public class ExportGreensDialog extends JDialog {
    private FileChooser fileChooser;
    private File selectedFile;
    private int result = 0;

    public ExportGreensDialog(Frame owner) {
        super(owner);
        initComponents();
        layoutCombo.setModel(new DefaultComboBoxModel(Layout.values()));
        layoutCombo.setSelectedItem(Layout.Eighteen1);
    }

    public void setFileChooser(FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    public ExportGreensDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public Layout getPageLayout() {
        return (Layout) layoutCombo.getSelectedItem();
    }

    public int getResult() {
        return result;
    }

    public boolean isPinPositionsSelected() {
        return pinsCheckBox.isSelected();
    }

    private void fileButtonActionPerformed() {
        File file = fileChooser.selectPdf(this);
        if (file != null) {
            String name = file.getName();
            int len = name.length();
            if (len > 4) {
                String ext = name.substring(len - 4, len);
                if (ext.equalsIgnoreCase(".pdf") == false) {
                    file = new File(file.getPath() + ".pdf");
                }
            }
            selectedFile = file;
            fileButton.setText(selectedFile.getName());
            okButton.setEnabled(true);
        }
    }

    private void okButtonActionPerformed() {
        result = 1;
        setVisible(false);
    }

    private void cancelButtonActionPerformed() {
        result = 0;
        setVisible(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("com.stayprime.basestation2.resources.ExportGreensDialog");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        layoutLabel = new JLabel();
        layoutCombo = new JComboBox();
        fileLabel = new JLabel();
        fileButton = new JButton();
        pinsCheckBox = new JCheckBox();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setTitle(bundle.getString("ExportGreensDialog.this.title"));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.createEmptyBorder("7dlu, 7dlu, 7dlu, 7dlu"));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "default, $lcgap, default:grow",
                    "3*(default, $lgap)"));

                //---- layoutLabel ----
                layoutLabel.setText(bundle.getString("ExportGreensDialog.layoutLabel.text"));
                contentPanel.add(layoutLabel, CC.xy(1, 1));
                contentPanel.add(layoutCombo, CC.xy(3, 1));

                //---- fileLabel ----
                fileLabel.setText(bundle.getString("ExportGreensDialog.fileLabel.text"));
                contentPanel.add(fileLabel, CC.xy(1, 3));

                //---- fileButton ----
                fileButton.setText(bundle.getString("ExportGreensDialog.fileButton.text"));
                fileButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fileButtonActionPerformed();
                    }
                });
                contentPanel.add(fileButton, CC.xy(3, 3));

                //---- pinsCheckBox ----
                pinsCheckBox.setText(bundle.getString("ExportGreensDialog.pinsCheckBox.text"));
                pinsCheckBox.setSelected(true);
                contentPanel.add(pinsCheckBox, CC.xywh(1, 5, 3, 1));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.createEmptyBorder("5dlu, 0dlu, 0dlu, 0dlu"));
                buttonBar.setLayout(new FormLayout(
                    "$glue, $button, $rgap, $button",
                    "pref"));

                //---- okButton ----
                okButton.setText(bundle.getString("ExportGreensDialog.okButton.text"));
                okButton.setEnabled(false);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed();
                    }
                });
                buttonBar.add(okButton, CC.xy(2, 1));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("ExportGreensDialog.cancelButton.text"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed();
                    }
                });
                buttonBar.add(cancelButton, CC.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(350, 165);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel layoutLabel;
    private JComboBox layoutCombo;
    private JLabel fileLabel;
    private JButton fileButton;
    private JCheckBox pinsCheckBox;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public enum Layout {
        One("One green", 1, 1),
        Two("Two greens", 1, 2),
        Six("Six greens", 2, 3),
        Nine1("Nine greens", 3, 3),
        Eighteen1("Eighteen greens", 3, 6),
        Eighteen2("Eighteen greens", 4, 5);

        public final String name;
        public final int horiz, vert;

        private Layout(String name, int horiz, int vert) {
            this.name = name;
            this.horiz = horiz;
            this.vert = vert;
        }

        @Override
        public String toString() {
            return name + (horiz > 1? " (" + horiz + "x" + vert + ")" : "");
        }

    }
}
