/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.ui.dialog;

import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.UserLogin;
import com.stayprime.legacy.sql.AbstractConnectionProvider;
import com.stayprime.ui.swingx.CurveShapePainter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.*;
import org.jdesktop.swingx.JXLabel;

public class LoginDialog extends AbstractConnectionProvider {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JFrame parent;
    private JDialog dialog;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    protected boolean connected;
    private final String userLabel = "Login as Administrator";

    private CourseService courseService;

    public LoginDialog(JFrame parent) {
        this.parent = parent;
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public boolean connect() {
        dialog = new JDialog(parent, "Login", true);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));
        
        pfPassword.addKeyListener(keyListener);
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(actionListener);

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnCancel);

        JXLabel bannerLabel = new JXLabel();
        bannerLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bannerLabel.setBackgroundPainter(new CurveShapePainter());
        bannerLabel.setText("<html><font color='#eeeeee" + "'>" + userLabel);
        bannerLabel.setFont(bannerLabel.getFont().deriveFont(Font.BOLD, bannerLabel.getFont().getSize() + 4.0F));

        dialog.getContentPane().add(bannerLabel, BorderLayout.NORTH);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);
        dialog.getContentPane().add(bp, BorderLayout.PAGE_END);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        dialog.setAlwaysOnTop(true);
        return connected;
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    protected void setConnected(boolean connected) {
        boolean oldConnected = this.connected;
        this.connected = connected;
        propertyChangeSupport.firePropertyChange(PROP_CONNECTED, oldConnected, connected);
    }

    @Override
    public void addConnectionStatusObserver(Observer observer) {
    }

    @Override
    public void removeConnectionStatusObserver(Observer observer) {
    }

    public static void main(String[] args) {
        new LoginDialog(null).connect();
    }

    public Connection getConnection() {
        return null;
    }

    public boolean disconnect() {
        setConnected(false);
        return true;
    }

    public void notifyError(Throwable error) {
        System.err.println("" + error.getMessage());
    }

    private KeyListener keyListener = new KeyListener() {

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                login();
            }
        }
        public void keyTyped(KeyEvent e) {
        }
        public void keyReleased(KeyEvent e) {
        }
    };

    private ActionListener actionListener = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
            login();
        }
    };

    private void login() {
        if (courseService == null) {
            NotificationPopup.showErrorPopup(null);
            return;
        }
        UserLogin user = courseService.authUser(getUsername(), getPassword());
        if (user != null) {
            JOptionPane.showMessageDialog(dialog,
                    "Welcome " + user.getFirstName() + "! You have successfully logged in.",
                    "Login",
                    JOptionPane.INFORMATION_MESSAGE);
            connected = true;
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(dialog,
                    "Invalid username or password",
                    "Login",
                    JOptionPane.ERROR_MESSAGE);
            // reset username and password
            tfUsername.setText("");
            pfPassword.setText("");
            connected = false;
        }
    }
}
