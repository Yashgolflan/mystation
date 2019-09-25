package com.stayprime.basestation2.db;

import com.stayprime.legacy.sql.AbstractConnectionProvider;
import com.stayprime.ui.swingx.CurveShapePainter;
import com.stayprime.ui.swingx.CustomLoginPane;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.JDBCLoginService;

public class LoginPaneSQLConnectionProvider extends AbstractConnectionProvider {

    private String url = "jdbc:mysql://localhost:3306/stayprime";
    private Connection connection;
    private Properties connectionProperties;
    private Component loginPaneParent;
    public String userLabel = "Login as Administrator";
    JDBCLoginService loginService;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    protected boolean connected;
    //public static final String PROP_CONNECTED = "connected";

    public LoginPaneSQLConnectionProvider(String url, Properties properties, Component loginPaneParent) {
	this.url = url;
	this.connectionProperties = new Properties();
	if (properties != null) {
	    connectionProperties.putAll(properties);
	}
    }

    public void setLoginPaneParent(Component loginPaneParent) {
        this.loginPaneParent = loginPaneParent;
    }

    public LoginPaneSQLConnectionProvider(String url, Properties properties) {
	this(url, properties, null);
    }

    public LoginPaneSQLConnectionProvider() {
	this(null, null);
    }

    public boolean connect() {
	Properties props = new Properties();
	props.putAll(connectionProperties);
	loginService = new JDBCLoginService("com.mysql.jdbc.Driver", url, props);
	CustomLoginPane loginPane = new CustomLoginPane(loginService);
	loginPane.getBannerLabel().setBackgroundPainter(new CurveShapePainter());
	loginPane.getBannerLabel().setText("<html><font color='#eeeeee" + "'>" + userLabel);
	loginPane.getBannerLabel().setFont(loginPane.getBannerLabel().getFont().deriveFont(Font.BOLD, loginPane.getBannerLabel().getFont().getSize() + 4.0F));
	JXLoginPane.Status status = JXLoginPane.showLoginDialog(loginPaneParent, loginPane);
	if (status == JXLoginPane.Status.SUCCEEDED) {
	    connection = loginService.getConnection();
	    setConnected(true);
	    return true;
	}
	else {
	    setConnected(false);
	    connection = null;
	    return false;
	}
    }

    public boolean disconnect() {
	if (connection != null) {
	    try {
		connection.close();
	    }
	    catch (SQLException ex) {
	    }
	    connection = null;
	}
	setConnected(false);
	return true;
    }

    public Connection getConnection() {
	return connection;
    }

    public void notifyError(Throwable error) {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public boolean isConnected() {
	return connected;
    }

    protected void setConnected(boolean connected) {
	boolean oldConnected = this.connected;
	this.connected = connected;
	propertyChangeSupport.firePropertyChange(PROP_CONNECTED, oldConnected, connected);
    }

    public void addConnectionStatusObserver(Observer observer) {
    }

    public void removeConnectionStatusObserver(Observer observer) {
    }
}
