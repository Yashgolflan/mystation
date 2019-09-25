/*
 * BaseStation2App.java
 */
package com.stayprime.basestation2;

import com.stayprime.basestation2.tasks.MonitorTask;
import com.aeben.golfclub.GolfClub;
import com.stayprime.basestation2.ui.DashboardManager;
import com.stayprime.basestation2.ui.BaseStation2View;
import com.stayprime.basestation2.ui.util.BasestationExitListener;
import com.stayprime.basestation2.ui.util.BaseStationUiUtil;
import com.alee.laf.WebLookAndFeel;
import com.stayprime.basestation2.comm.NotificationsManager;
import com.stayprime.basestation2.instance.ApplicationInstanceManager;
import com.stayprime.basestation2.instance.InstanceListener;
import com.stayprime.basestation2.services.Services;
import com.stayprime.basestation2.services.SendMessageControl;
import com.stayprime.basestation2.ui.util.TableUtil;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.storage.util.LocalStorage;
import com.stayprime.ui.ComboBoxProperty;
import com.stayprime.ui.PersistentCheckBox;
import com.stayprime.ui.PersistentComboBox;
import com.stayprime.ui.ToggleButtonProperty;
import com.stayprime.util.ClassUtilities;
import com.stayprime.util.ConfigUtil;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The main class of the application.
 */
public class BaseStation2App extends SingleFrameApplication {
    private static final Logger log = LoggerFactory.getLogger(BaseStation2App.class);

    private String basePath;

    private PropertiesConfiguration config;

    private final LocalStorage localStorage;

    private final CourseSettingsManager courseSettingsManager;

    private BaseStation2View view;

    private DashboardManager dashboardManager;

    private long startTime = System.currentTimeMillis();

    @Autowired
    private Services services;

    @Autowired
    private MonitorTask monitorTask;

    @Autowired
    private SendMessageControl sendMessageControl;

    @Autowired
    NotificationsManager notificationsManager;

    //Task to run in application blocking scope
    private Task currentTask;

    Throwable startupError;
    private boolean golfClubLoaded;

    public BaseStation2App() {
        localStorage = new LocalStorage(SpringApplication.workDir);
        courseSettingsManager = new CourseSettingsManager(localStorage);
    }

    /*
     * Swing Application Framework life cycle
     */

    @Override
    protected void initialize(String[] args) {
        loadConfig(args);
        registerSingleInstance();
        logVersionInformation();
    }

    private void loadConfig(String[] args) {
        basePath = SpringApplication.findBasePath(args, 0);
        log.info("basePath: " + basePath);
        File configFile = new File(basePath, Constant.CONFIGURATION_FILENAME);
        config = ConfigUtil.load(configFile, log);
    }

    private void registerSingleInstance() {
        if (config.getBoolean(Constant.CONFIG_SINGLEINSTANCE, true)) {
            int socket = config.getInt(Constant.CONFIG_SINGLEINSTANCEPORT,
                    ApplicationInstanceManager.SINGLE_INSTANCE_NETWORK_SOCKET);

            if (!ApplicationInstanceManager.registerInstance(socket)) {
                log.warn("Another instance of this application is already running.  Exiting.");
                System.exit(0);
            }

            ApplicationInstanceManager.setApplicationInstanceListener(new InstanceListener());
        }
    }

    private void logVersionInformation() {
        ClassUtilities.printAppInfo("StayPrime BaseStation Application", BaseStation2App.class);
        log.info("System Startup time: " + new Date(startTime));
    }

    @Override
    protected void startup() {
        try {
            BaseStationUiUtil.applyFrameFix(log);
            setupLookAndFeel();
            setLocale();

            BaseStationUiUtil.initializeFontSize("Droid Sans", 1.2f);
            setupCustomPersistentUI();
            addExitListener(new BasestationExitListener());

            initView();
            show(view);
        }
        catch (Throwable t) {
            this.startupError = new RuntimeException("Application startup failed.", t);
            throw t;
        }
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            UIManager.put("RangeSliderUI", "com.stayprime.basestation2.ui.custom.BasicRangeSliderUI");
            WebLookAndFeel.initializeManagers();
            return;
        }
        catch (Exception ex) {
            log.warn(ex.toString());
        }
        try {
            WebLookAndFeel.install();
            UIManager.put("RangeSliderUI", "com.stayprime.basestation2.ui.custom.WebRangeSliderUI");
            return;
        }
        catch (Exception ex) {
            log.warn(ex.toString());
        }
    }

    private void setLocale() {
        try {
            String localeSetting = config.getString(Constant.CONFIG_LOCALE, "en-GB");
            Locale.setDefault(Locale.forLanguageTag(localeSetting));
        }
        catch (Throwable t) {
            log.warn(t.toString());
        }
    }

    private void setupCustomPersistentUI() {
        org.jdesktop.application.ApplicationContext appContext = getContext();
        appContext.getSessionStorage().putProperty(PersistentCheckBox.class, new ToggleButtonProperty());
        appContext.getSessionStorage().putProperty(PersistentComboBox.class, new ComboBoxProperty());
        appContext.getSessionStorage().putProperty(JSplitPane.class, null);
        appContext.getSessionStorage().putProperty(JTable.class, new TableUtil.ExtendedTableProperty());
    }

    private void initView() {
        dashboardManager = new DashboardManager(config);
        dashboardManager.init();
        view = new BaseStation2View(this);
        view.setAppConfiguration(config);
        view.init();
        view.setCourseSettingsManager(courseSettingsManager);
    }

    /*
     * Application 
     */

    void loadFromStorage() {
        try {
            loadGolfClub();
            loadCarts();
            loadSettings();
        }
        catch (Exception ex) {
            log.warn("{} loading initial GolfClub and Carts data", ex);
        }
    }

    /**
     * Runs after the Spring Application is started.
     */
    void start() {
        dashboardManager.setCourseSettingsManager(courseSettingsManager);
        view.setServices(services);
        dashboardManager.setup();
        view.setSendMessageControl(sendMessageControl);
        view.setNotificationsManager(notificationsManager);

        runTask(new BaseStationStartupTask(this));
    }

    void runStartupItems() {
        if (!config.getBoolean("demoServer", false)) {
            monitorTask.start();
        }
    }

    public void stop() {
        ApplicationInstanceManager.stop();
        long totalTime = System.currentTimeMillis() - startTime;
        log.debug("Application total runtime: " + TimeUnit.MILLISECONDS.toMinutes(totalTime) + " Minutes");
    }

    /*
     * Asset and cart loading and syncing
     */

    public void syncGolfClub() {
        services.getAssetSyncService().syncAssets();
    }

    public void runSyncGolfClubTask() {
        view.runSyncGolfClubTask();
    }

    public void loadGolfClub() {
        golfClubLoaded(getGolfClub());
    }

    public GolfClub getGolfClub() {
        return localStorage.getGolfClub();
    }

    public boolean isGolfClubLoaded() {
        return golfClubLoaded;
    }

    public void golfClubLoaded(GolfClub golfClub) {
        if (golfClub != null) {
            golfClubLoaded = true;
            dashboardManager.setGolfCLub(golfClub);
            view.golfClubLoaded(golfClub);
        }
    }

    public void loadCarts() {
        List<CartInfo> carts = localStorage.listCarts();
        if (carts != null) {
            cartInfoLoaded(carts);
        }
    }

    public void cartInfoLoaded(List<CartInfo> carts) {
        dashboardManager.cartsStatusUpdated(carts);
        view.cartInfoUpdated(carts);
    }

    public void showStatusMessage(String message) {
        view.showStatusMessage(message);
    }

    private void loadSettings() {
        courseSettingsManager.readAllSettings();
    }

    /*
     * Application components accessors
     */

    public static BaseStation2App getApplication() {
        return Application.getInstance(BaseStation2App.class);
    }

    public LocalStorage getLocalStorage() {
        return localStorage;
    }

    public CourseSettingsManager getCourseSettingsManager() {
        return courseSettingsManager;
    }

    public PropertiesConfiguration getConfig() {
        return config;
    }

    public Services getServices() {
        return services;
    }

    public String getBasePath() {
        return basePath;
    }

    @Override
    public BaseStation2View getMainView() {
        return view;
    }

    public DashboardManager getDashboardManager() {
        return dashboardManager;
    }

    public long getStartTime() {
        return startTime;
    }
    
    public NotificationsManager getNotificationsManager() {
        return notificationsManager;
    }

    /*
     * Run application blocking task util methods
     */

    public void runTask(Task task) {
        currentTask = task;
        ActionEvent e = new ActionEvent(view.getComponent(), ActionEvent.ACTION_PERFORMED, "runTask");
        getContext().getActionMap().get("runTask").actionPerformed(e);
    }

    @Action(block = Task.BlockingScope.APPLICATION)
    public Task runTask() {
        Task task = currentTask;
        currentTask = null;
        return task;
    }

}
