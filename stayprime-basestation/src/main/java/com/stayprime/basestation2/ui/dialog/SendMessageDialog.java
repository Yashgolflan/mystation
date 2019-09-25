/*
 * Created by JFormDesigner on Mon Oct 19 12:00:28 GST 2015
 */

package com.stayprime.basestation2.ui.dialog;

import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.util.MaxLengthDocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import javax.swing.text.AbstractDocument;
import org.apache.commons.lang.StringUtils;

/**
 * @author Benjamin Baron
 */
public class SendMessageDialog extends JDialog implements SendMessageView {
    private static final String TEXT_SUBMIT = "text-submit";
    private static final String INSERT_BREAK = "insert-break";

    private SendMessageActions actions;
    private int cartNumber;

    public SendMessageDialog(Frame owner) {
        super(owner);
        initComponents();
        setMaxChars(150);
        setTextAreaInputMap();
    }

    private void setMaxChars(final int maxChars) {
        final AbstractDocument doc = (AbstractDocument) textArea.getDocument();
        doc.setDocumentFilter(new MaxLengthDocumentFilter(doc, maxChars));
    }

    private void setTextAreaInputMap() {
        InputMap input = textArea.getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        input.put(shiftEnter, INSERT_BREAK);  // input.get(enter)) = "insert-break"
        input.put(enter, TEXT_SUBMIT);

        ActionMap actions = textArea.getActionMap();
        actions.put(TEXT_SUBMIT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });
    }

    @Override
    public void setActions(SendMessageActions actions) {
        this.actions = actions;
    }

    @Override
    public void setCartNumber(int cartNumber) {
        this.cartNumber = cartNumber;
        initComponentsI18n();
        titleLabel.setText(titleLabel.getText() + " - " + "Cart " + cartNumber);
    }

    @Override
    public void setup() {
        textArea.setText("");
        textArea.requestFocus();
    }

    @Override
    public void showException(Exception ex) {
        TaskDialogs.showException(ex);
    }

    private void okButtonActionPerformed() {
        if (StringUtils.isNotBlank(textArea.getText())) {
            actions.sendMessage(cartNumber, textArea.getText());
        }
        else {
            textArea.requestFocus();
        }
    }

    private void cancelButtonActionPerformed() {
        actions.cancel();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        titleLabel = new JLabel();
        textAreaScrollPane = new JScrollPane();
        textArea = new JTextArea();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.createEmptyBorder("7dlu, 7dlu, 7dlu, 7dlu"));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "default:grow",
                    "default, $lgap, fill:default:grow"));

                //---- titleLabel ----
                titleLabel.setFont(new Font("Droid Sans", Font.BOLD, 14));
                contentPanel.add(titleLabel, CC.xy(1, 1));

                //======== textAreaScrollPane ========
                {

                    //---- textArea ----
                    textArea.setLineWrap(true);
                    textArea.setRows(8);
                    textArea.setColumns(40);
                    textAreaScrollPane.setViewportView(textArea);
                }
                contentPanel.add(textAreaScrollPane, CC.xy(1, 3));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.createEmptyBorder("5dlu, 0dlu, 0dlu, 0dlu"));
                buttonBar.setLayout(new FormLayout(
                    "$glue, $button, $rgap, $button",
                    "pref"));

                //---- okButton ----
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed();
                    }
                });
                buttonBar.add(okButton, CC.xy(2, 1));

                //---- cancelButton ----
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

        initComponentsI18n();

        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initComponentsI18n() {
        // JFormDesigner - Component i18n initialization - DO NOT MODIFY  //GEN-BEGIN:initI18n
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("com.stayprime.basestation2.resources.BaseStation2App");
        titleLabel.setText(bundle.getString("SendMessageDialog.titleLabel.text"));
        okButton.setText(bundle.getString("SendMessageDialog.okButton.text"));
        cancelButton.setText(bundle.getString("SendMessageDialog.cancelButton.text"));
        // JFormDesigner - End of component i18n initialization  //GEN-END:initI18n
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel titleLabel;
    private JScrollPane textAreaScrollPane;
    private JTextArea textArea;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) {
        new SendMessageDialog(null).setVisible(true);
    }

}
