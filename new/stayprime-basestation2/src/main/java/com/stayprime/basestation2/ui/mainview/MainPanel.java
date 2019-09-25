/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainPanel.java
 *
 * Created on 21/02/2011, 11:00:38 AM
 */
package com.stayprime.basestation2.ui.mainview;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.basestation2.ui.ScreenManager;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.view.Dashboard;
import com.stayprime.basestation.ui.FileChooser;
import com.stayprime.basestation2.Constant;
import com.stayprime.basestation2.comm.NotificationsManager;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.services.Services;
import com.stayprime.basestation2.ui.cartassignment.CartAssignmentEditor;
import com.stayprime.basestation2.ui.dialog.LoginDialog;
import com.stayprime.basestation2.services.SendMessageControl;
import com.stayprime.basestation2.ui.util.ApplicationUtil;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.imageview.BasicImageRenderer;
import com.stayprime.imageview.CachedImageRenderer;
import com.stayprime.imageview.ImageRenderer;
import com.stayprime.legacy.screen.BasicScreen;
import com.stayprime.legacy.screen.Screen;
import com.stayprime.legacy.screen.ScreenParent;
import com.stayprime.storage.util.LocalStorage;
import com.stayprime.ui.PersistentCheckBox;
import com.stayprime.ui.swingx.JXCollapsiblePane;
import com.stayprime.util.UIUtil;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.javadev.AnimatingCardLayout;
import org.javadev.effects.FadeAnimation;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationAction;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.ImagePainter;

/**
 *
 * @author benjamin
 */
public class MainPanel extends javax.swing.JPanel implements ScreenParent {
    private static final Logger log = LoggerFactory.getLogger(MainPanel.class);

    private BasicScreen dashboardScreen;
    private Screen currentScreen;
    private AnimatingCardLayout cardLayout;
    private Component toolbarGlue;
    private JDialog pinLocationDialog;//, cartStatusDialog;
    private Timer notificationsBlinkTimer;
    private ScreenManager screenManager;
    private CourseSettingsActions settingsActions;
    private LoginDialog loginDialog;
    private boolean printCoordinates = true;

    private boolean showingDashboard = false;
    public static final String PROP_SHOWINGDASHBOARD = "showingDashboard";

    private boolean showingDashboardScreen;
    public static final String PROP_SHOWINGDASHBOARDSCREEN = "showingDashboardScreen";

    private CartAssignmentEditor cartAssignmentEditor;

    private Services services;

    /**
     * Creates new form MainPanel
     */
    public MainPanel() {
    }

    /**
     * Inits UI and ties components together.
     * This method is separated from the constructor to avoid initialization
     * problems with NetBeans GUI editor.
     */
    public void init() {
        dashboardManager = BaseStation2App.getApplication().getDashboardManager();
        settingsActions = new CourseSettingsActions(BaseStation2App.getApplication());
        settingsActions.setDashboard(dashboardManager.getDashboard());

        initComponents();
        setAdminMode(false);
        initModules();
        setupUi();

        initDashboard();
        initDashboardScreen();

        setupDrawingTool();
        setupScreenManager();
        setupPinLocationPanel();
        setupEventListeners();
    }

    private void initModules() {
        cartTrackingPanel.init();
        onCourseAdsScreen.init();
//        golfSiteSetupScreen.init();
        menuScreen.init();
        cartManagementPanel.init();
        cartPacePanel.init();
    }

    public void setSendMessageControl(SendMessageControl messageControl) {
        cartPacePanel.setSendMessageControl(messageControl);
    }

    public void setServices(Services services) {
        this.services = services;
        cartManagementPanel.setServices(services);

        CourseService courseService = services == null ? null : services.getCourseService();
        golfSiteSetupScreen.setCourseService(courseService);
        onCourseAdsScreen.setCourseService(courseService);
        menuScreen.setCourseService(courseService);
        pinLocationPanel.setCourseService(courseService);
        cartTrackingPanel.setCartService(services == null ? null : services.getCartService());
        drawingTool.setCourseService(courseService);
    }

    public void setCartInfoFilter(CartInfoFilter cartInfoFilter) {
        cartPacePanel.setCartInfoFilter(cartInfoFilter);
    }

    public void setCourseSettingsManager(CourseSettingsManager courseSettingsManager) {
        getAction(MainActions.cartPathOnly).setSelected(courseSettingsManager.getCartPathOnly());
        getAction(MainActions.weatherAlert).setSelected(courseSettingsManager.getWeatherAlert());
        initCourseSettings(courseSettingsManager);
        cartPacePanel.setCourseSettingsManager(courseSettingsManager);
    }

    private void initCourseSettings(final CourseSettingsManager settingsManager) {
        getAction(MainActions.cartPathOnly).setSelected(settingsManager.getCartPathOnly());
        getAction(MainActions.weatherAlert).setSelected(settingsManager.getWeatherAlert());

        settingsActions.setSettingsManager(settingsManager);

        settingsManager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CourseSettingsManager.PROP_CARTPATHONLY)) {
                    //cartPathButton.setSelected(courseSettingsManager.getCartPathOnly());
                    getAction(MainActions.cartPathOnly).setSelected(settingsManager.getCartPathOnly());
                }
                else if (evt.getPropertyName().equals(CourseSettingsManager.PROP_WEATHERALERT)) {
                    //inclementWeatherButton.setSelected(courseSettingsManager.getWeatherAlert());
                    getAction(MainActions.weatherAlert).setSelected(settingsManager.getWeatherAlert());

                }
            }
        });
    }

    public void setNotificationsManager(NotificationsManager notificationsManager) {
        notificationsPanel.setNotificationsManager(notificationsManager);
    }

    public void setFileChooser(FileChooser fileChooser) {
        golfSiteSetupScreen.setFileChooser(fileChooser);
        onCourseAdsScreen.setFileChooser(fileChooser);
    }

    public void cartInfoUpdated(Collection<CartInfo> status) {
        cartPacePanel.cartInfoUpdated(status);
        cartTrackingPanel.cartInfoUpdated(status);
    }

    private void setupUi() {
        cardLayout = (AnimatingCardLayout) cardPanel.getLayout();
        toolbarGlue = Box.createGlue();//Horizontal gaps for main toolbar and sidebars
        drawingToolBarTitle.add(Box.createGlue(), 1);
        sideBarTitle.add(Box.createGlue(), 1);
        zoomButtonsToolbar.add(Box.createGlue(), 2);
    }

    private void initDashboard() {
        final Dashboard dashboard = dashboardManager.getDashboard();
        dashboardPanel.add(dashboard.getComponent(), "Center");

//        dashboardManager.setDashboard(dashboard);
        pinLocationPanel.setDashboard(dashboard);
        cartTrackingPanel.setDashboard(dashboard);
        cartPacePanel.setDashboard(dashboard);


        Paint checkerPaint = UIUtil.getCheckerPaint(Color.black, Color.darkGray, 50);
        BasicImageRenderer basicImageRenderer = new BasicImageRenderer();
        basicImageRenderer.setBackgroundPaint(checkerPaint);

        final ImageRenderer renderer = new CachedImageRenderer(basicImageRenderer);
        dashboard.setImageRenderer(renderer);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(printCoordinates && SwingUtilities.isRightMouseButton(e)) {
                    System.out.println(dashboard.projectInverse(e.getPoint()));
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePressed(e);
            }

        };

        dashboard.addMouseListener(mouseAdapter);
        dashboard.addMouseMotionListener(mouseAdapter);
    }

    private void initDashboardScreen() {
        //Initial screen
        dashboardScreen = new BasicScreen(dashboardPanel.getName());
        dashboardScreen.setScreenComponent(dashboardManager.getDashboard());
        dashboardScreen.setToolBarComponent(zoomButtonsToolbar);
        showScreen(dashboardScreen);
    }

    private void setupDrawingTool() {
        //Extra configuration for drawing tool toolbar items
        ((Container) drawingTool.getToolbarComponent()).add(Box.createHorizontalStrut(10), 0);
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setMaximumSize(new Dimension(10, 100));
        ((Container) drawingTool.getToolbarComponent()).add(separator, 1);
        ((Container) drawingTool.getToolbarComponent()).add(Box.createHorizontalStrut(16), 2);

        //Pass components to the dashboardManager
//        dashboardManager.setDashboard(dashboard);
        dashboardManager.setDrawingTool(drawingTool);
    }

    private void setupScreenManager() {
        //Pass components to the ScreenManager
        screenManager = new ScreenManager(this, dashboardScreen);
        screenManager.addScreenButton(dashboardScreen, dashboardButton, false);
        screenManager.addScreenButton(menuScreen, menuButton, false);
        screenManager.addScreenButton(cartManagementPanel, cartMgtButton, false);
        screenManager.addScreenButton(golfSiteSetupScreen, courseMgtButton, false);
        screenManager.addScreenButton(reportScreen, showReportButton, false);
        screenManager.addScreenButton(drawingTool, drawingToolButton, false);
        screenManager.addScreenButton(onCourseAdsScreen, onCourseAdsButton, false);
        screenManager.showScreen(dashboardScreen);
    }

    private void setupPinLocationPanel() {
        pinLocationPanel.setCallback(new PinLocationPanel.DialogCallback() {
            @Override
            public void dialogDone() {
                pinLocationButton.setSelected(false);
            }
        });
    }

    private void setupEventListeners() {
        //If sidebar animation is enabled, optimize the Dashboard drawing
        sidebarPanel.addPropertyChangeListener(JXCollapsiblePane.ANIMATION_STATE_KEY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Dashboard dashboard = dashboardManager.getDashboard();

                if (dashboard != null && (evt.getNewValue().equals("collapsed") || evt.getNewValue().equals("expanded")))
                    dashboard.setTransientDraw(false);
                else
                    dashboard.setTransientDraw(true);
            }
        });

        //Collapse the dashboard layers when not showing
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
//                dashLayerPanel.setCollapsed(dashboardButton.isSelected() == false || pinLocationButton.isSelected());
            }
        };
        dashboardButton.addItemListener(itemListener);
        pinLocationButton.addItemListener(itemListener);

        //Blink the notifications button if there are new notifications
        notificationsPanel.addPropertyChangeListener(new NotificationsChangeListener());
        notificationsBlinkTimer = new NotificationsBlinkTimer(500);
    }

//    public void setCartStorageService(CartService cartStorageService) {
//        tournamentPanel.setCartStorageService(cartStorageService);
//    }

//    public void setCommServer(CommServer remoteUdpCommServer) {
//        tournamentPanel.setRemoteUDPCommServer(remoteUdpCommServer);
//    }

    public void setAdminMode(boolean admin) {
        ApplicationAction action = getAction(MainActions.administratorLogin);
        action.setSelected(admin);
        adminButton.setSelected(admin);

        getAction(MainActions.showCourseManagementScreen).setEnabled(admin);
        getAction(MainActions.showCartManagementScreen).setEnabled(admin);
        getAction(MainActions.showReport).setEnabled(admin);
        getAction(MainActions.showDrawingTool).setEnabled(admin);

        courseMgtButton.setVisible(admin);
        cartMgtButton.setVisible(admin);
        drawingToolButton.setVisible(admin);
        showReportButton.setVisible(admin);
        onCourseAdsButton.setVisible(admin);
    }

    public void setGolfClub(GolfClub golfClub) {
        cartPacePanel.setGolfClub(golfClub);
        if (golfClub != null) {
            setLogoImage(golfClub.getLogoImage());
            setMapImage(golfClub.getCourseImage());
        }
    }

    private void setLogoImage(String logoImage) {
        if (StringUtils.isNotBlank(logoImage)) {
            try {
                BufferedImage img = ImageIO.read(new File(logoImage));
                ImagePainter bottomBannerImagePainter = new ImagePainter(img);
                bottomBannerImagePainter.setScaleToFit(true);
                bottomBannerImagePainter.setScaleType(ImagePainter.ScaleType.InsideFit);
                bottomBannerImagePainter.setVerticalAlignment(VerticalAlignment.BOTTOM);

                logoPanel.setBackgroundPainter(bottomBannerImagePainter);
            }
            catch (Exception ex) {
                log.warn("Error loading the logo image" + ex);
            }
        }
        else {
            logoPanel.setBackgroundPainter(null);
        }
    }

    private void setMapImage(BasicMapImage courseImage) {
        if(courseImage != null) {
            BasicMapImage currentMap = dashboardManager.getDashboard().getMap();

            if(ObjectUtils.notEqual(courseImage, currentMap)) {
                performAction(MainActions.loadGolfClubImage);
            }
        }
        else {

        }
    }

    public ApplicationAction getAction(MainActions act) {
        return ApplicationUtil.getAction(this, act.name());
    }

    private void performAction(MainActions act) {
        getAction(act).actionPerformed(new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, act.name()));
    }

    public boolean isShowingDashboard() {
        return showingDashboard;
    }

    protected void setShowingDashboard(boolean showingDashboard) {
        boolean oldShowingDashboard = this.showingDashboard;
        this.showingDashboard = showingDashboard;
        firePropertyChange(PROP_SHOWINGDASHBOARD, oldShowingDashboard, showingDashboard);
    }

    public boolean isShowingDashboardScreen() {
        return showingDashboardScreen;
    }

    public void setShowingDashboardScreen(boolean showingDashboardScreen) {
        boolean oldShowingDashboardScreen = this.showingDashboardScreen;
        this.showingDashboardScreen = showingDashboardScreen;
        firePropertyChange(PROP_SHOWINGDASHBOARDSCREEN, oldShowingDashboardScreen, showingDashboardScreen);
    }
    
    /**
     * State change sync methods
     */
    public void weatherAlertAndCartPathOnlyButtonsStateChanged(Boolean cartPathOnly, Boolean weatherAlert) {
        settingsActions.getSettingsManager().setWeatherAlertModified(weatherAlert);
        settingsActions.getSettingsManager().setCartPathOnlyModified(cartPathOnly);
    }
    
    public void pinsGridNumberStateChanged(Integer index) {
        settingsActions.getSettingsManager().setGridSystemCurrentGridModified(index);
    }
    
    /*
     * Implements ScreenParent
     */
    public void exitScreen(Screen screen) {
        showDashboardPanel();
    }

    public final boolean showScreen(Screen screen) {
        Screen showScreen = screen == null ? dashboardScreen : screen;

        if (showScreen != currentScreen) {
            uninstallScreenToolbarComponents(currentScreen);

            //Clean up previous screen state
            if (currentScreen == drawingTool) {
                uninstallDrawingTool();
            }
            else if (currentScreen == dashboardScreen) {
                pinLocationButton.setSelected(false);
            }

            // Set up new screen state
            setShowingDashboard(showScreen == dashboardScreen || showScreen == drawingTool);
            setShowingDashboardScreen(showScreen == dashboardScreen);

            installScreenToolbarComponents(showScreen);

            if (showScreen == dashboardScreen || showScreen == drawingTool) {
                cardLayout.show(cardPanel, dashboardScreen.getName());

                if (showScreen == drawingTool) {
                    installDrawingTool();
                    drawingTool.reloadGolfClub();
                }
                else if (showScreen == dashboardScreen) {
                }
            }
            else {
                cardLayout.show(cardPanel, showScreen.getName());
            }

            currentScreen = showScreen;
        }

        return true;
    }

    //Deleted showDrawingToolFromCurrentScreen.
    @Override
    public void setUI(PanelUI ui) {
        if (pinLocationButton != null) {
            pinLocationButton.setSelected(false);
        }
        super.setUI(ui);
    }

    @Override
    protected void setUI(ComponentUI ui) {
        if (pinLocationButton != null) {
            pinLocationButton.setSelected(false);
        }
        super.setUI(ui);
    }

    /**
     * Create and configure pinLocationDialog once. Configure ApplicationAction
     * showPinLocationDialog to be selected/unselected accordingly.
     */
    private void createPinLocationDialog() {
        if (pinLocationDialog == null) {
            //ResourceMap resourceMap = BaseStation2App.getInstance().getContext().getResourceMap(MainPanel.class);
            pinLocationDialog = new JDialog(BaseStation2App.getApplication().getMainFrame(), "Pin Location");
            pinLocationDialog.setContentPane(pinLocationPanel);

            //To deal with Look and Feel Changes
            SwingUtilities.updateComponentTreeUI(pinLocationPanel);
            pinLocationDialog.setName("pinLocationDialog");
            pinLocationDialog.pack();

            //Unselect the pin location button when the dialog closes
            pinLocationDialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    getAction(MainActions.showPinLocationDialog).setSelected(false);
                }

                @Override
                public void componentShown(ComponentEvent e) {
                    getAction(MainActions.showPinLocationDialog).setSelected(true);
                }
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        dashboardManager = dashboardManager;
        zoomInButton1 = new javax.swing.JButton();
        zoomOutButton1 = new javax.swing.JButton();
        cartStatusTabSelectorButtonGroup = new javax.swing.ButtonGroup();
        pinLocationPanel = new com.stayprime.basestation2.ui.mainview.PinLocationPanel();
        leftMenuPanel = new org.jdesktop.swingx.JXPanel();
        leftMenuButtonsPanel = new org.jdesktop.swingx.JXPanel();
        logoLabel = new javax.swing.JLabel();
        dashboardControlsPanel = new org.jdesktop.swingx.JXPanel();
        dashboardButton = new javax.swing.JToggleButton();
        dashLayerPanel = new org.jdesktop.swingx.JXCollapsiblePane() {
            public void paintComponent(Graphics g) {
            }
        };
        actionZonesCheckbox = new PersistentCheckBox();
        cartPathsCheckbox = new PersistentCheckBox();
        pinLocationCheckbox = new PersistentCheckBox();
        positionCheckbox = new PersistentCheckBox();
        cartStatusButton = new javax.swing.JToggleButton();
        notificationsButton = new javax.swing.JToggleButton();
        cartAssignmentButton = new javax.swing.JButton();
        menuButton = new javax.swing.JToggleButton();
        onCourseAdsButton = new javax.swing.JButton();
        courseMgtButton = new javax.swing.JToggleButton();
        cartMgtButton = new javax.swing.JToggleButton();
        showReportButton = new javax.swing.JToggleButton();
        drawingToolButton = new javax.swing.JToggleButton();
        logoPanel = new org.jdesktop.swingx.JXPanel();
        rightPanel = new org.jdesktop.swingx.JXPanel();
        topButtonsToolbar = new org.jdesktop.swingx.JXPanel();
        zoomButtonsToolbar = new org.jdesktop.swingx.JXPanel();
        zoomInButton = new javax.swing.JButton();
        zoomOutButton = new javax.swing.JButton();
        atacButton = new javax.swing.JToggleButton();
        pinLocationButton = new javax.swing.JToggleButton();
        weatherAlertButton = new javax.swing.JToggleButton();
        cartPathOnlyButton = new javax.swing.JToggleButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        adminButton = new javax.swing.JToggleButton();
        cardPanel = new javax.swing.JPanel();
        dashboardPanel = new javax.swing.JPanel();
        drawingSidebarPanel = new org.jdesktop.swingx.JXCollapsiblePane();
        drawingToolBarTitle = new javax.swing.JToolBar();
        drawingToolTitleLabel = new javax.swing.JLabel();
        drawingToolCloseButton = new javax.swing.JButton();
        drawingToolScrollPane = new javax.swing.JScrollPane();
        drawingTool = new com.stayprime.basestation2.ui.modules.DrawingTool();
        cartManagementPanel = new com.stayprime.basestation2.ui.modules.CartManagementPanel();
        menuScreen = new com.stayprime.basestation2.ui.modules.MenuScreen();
        reportScreen = new com.stayprime.basestation2.ui.reports.ReportScreen();
        onCourseAdsScrollPane = new javax.swing.JScrollPane();
        onCourseAdsScreen = new com.stayprime.basestation2.ui.modules.OnCourseAdsScreen();
        golfSiteSetupScreen = new com.stayprime.basestation.ui.site.GolfSiteSetupScreen();
        sidebarPanel = new org.jdesktop.swingx.JXCollapsiblePane();
        sideBarTitle = new javax.swing.JToolBar();
        sidebarTitleLabel = new javax.swing.JLabel();
        sidebarCloseButton = new javax.swing.JButton();
        sidebarCardPanel = new org.jdesktop.swingx.JXPanel();
        notificationsSidebar = new org.jdesktop.swingx.JXPanel();
        notificationsPanel = new com.stayprime.basestation2.ui.mainview.NotificationsPanel();
        cartStatusSidebar = new org.jdesktop.swingx.JXPanel();
        paceOfPlayButton = new javax.swing.JToggleButton();
        cartTrackingButton = new javax.swing.JToggleButton();
        cartStatusTabsPanel = new org.jdesktop.swingx.JXPanel();
        cartPacePanel = new com.stayprime.basestation2.ui.mainview.CartPacePanel();
        cartTrackingPanel = new com.stayprime.basestation2.ui.mainview.CartTrackingPanel();

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(MainPanel.class, this);
        zoomInButton1.setAction(actionMap.get("zoomIn")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(MainPanel.class);
        zoomInButton1.setText(resourceMap.getString("zoomInButton1.text")); // NOI18N
        zoomInButton1.setFocusable(false);
        zoomInButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton1.setName("zoomInButton1"); // NOI18N
        zoomInButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        zoomOutButton1.setAction(actionMap.get("zoomOut")); // NOI18N
        zoomOutButton1.setText(resourceMap.getString("zoomOutButton1.text")); // NOI18N
        zoomOutButton1.setFocusable(false);
        zoomOutButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton1.setName("zoomOutButton1"); // NOI18N
        zoomOutButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        pinLocationPanel.setName("pinLocationPanel"); // NOI18N

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        leftMenuPanel.setOpaque(false);
        leftMenuPanel.setName("leftMenuPanel"); // NOI18N
        leftMenuPanel.setLayout(new java.awt.BorderLayout());

        leftMenuButtonsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 1, 1));
        leftMenuButtonsPanel.setName("leftMenuButtonsPanel"); // NOI18N
        org.jdesktop.swingx.VerticalLayout verticalLayout1 = new org.jdesktop.swingx.VerticalLayout();
        verticalLayout1.setGap(5);
        leftMenuButtonsPanel.setLayout(verticalLayout1);

        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel.setIcon(resourceMap.getIcon("logoLabel.icon")); // NOI18N
        logoLabel.setName("logoLabel"); // NOI18N
        leftMenuButtonsPanel.add(logoLabel);

        dashboardControlsPanel.setOpaque(false);
        dashboardControlsPanel.setAlignmentX(0.0F);
        dashboardControlsPanel.setName("dashboardControlsPanel"); // NOI18N
        dashboardControlsPanel.setLayout(new java.awt.BorderLayout());

        dashboardButton.setAction(actionMap.get("showDashboardPanel")); // NOI18N
        dashboardButton.setSelected(true);
        dashboardButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dashboardButton.setName("dashboardButton"); // NOI18N
        dashboardControlsPanel.add(dashboardButton, java.awt.BorderLayout.NORTH);

        dashLayerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 0, 0));
        dashLayerPanel.setOpaque(false);
        dashLayerPanel.setName("dashLayerPanel"); // NOI18N
        dashLayerPanel.setPaintBorderInsets(false);
        dashLayerPanel.getContentPane().setLayout(new javax.swing.BoxLayout(dashLayerPanel.getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        actionZonesCheckbox.setText(resourceMap.getString("actionZonesCheckbox.text")); // NOI18N
        actionZonesCheckbox.setName("actionZonesCheckbox"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, dashboardManager, org.jdesktop.beansbinding.ELProperty.create("${showActionZones}"), actionZonesCheckbox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        dashLayerPanel.getContentPane().add(actionZonesCheckbox);

        cartPathsCheckbox.setText(resourceMap.getString("cartPathsCheckbox.text")); // NOI18N
        cartPathsCheckbox.setName("cartPathsCheckbox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, dashboardManager, org.jdesktop.beansbinding.ELProperty.create("${showCartPaths}"), cartPathsCheckbox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        dashLayerPanel.getContentPane().add(cartPathsCheckbox);

        pinLocationCheckbox.setText(resourceMap.getString("pinLocationCheckbox.text")); // NOI18N
        pinLocationCheckbox.setName("pinLocationCheckbox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, dashboardManager, org.jdesktop.beansbinding.ELProperty.create("${showPinFlags}"), pinLocationCheckbox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        dashLayerPanel.getContentPane().add(pinLocationCheckbox);

        positionCheckbox.setText("Cart Position"); // NOI18N
        positionCheckbox.setName("positionCheckbox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, dashboardManager, org.jdesktop.beansbinding.ELProperty.create("${showCarts}"), positionCheckbox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        dashLayerPanel.getContentPane().add(positionCheckbox);

        dashboardControlsPanel.add(dashLayerPanel, java.awt.BorderLayout.CENTER);

        leftMenuButtonsPanel.add(dashboardControlsPanel);

        cartStatusButton.setAction(actionMap.get("showCartStatus")); // NOI18N
        cartStatusButton.setText(resourceMap.getString("cartStatusButton.text")); // NOI18N
        cartStatusButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cartStatusButton.setName("cartStatusButton"); // NOI18N
        leftMenuButtonsPanel.add(cartStatusButton);

        notificationsButton.setAction(actionMap.get("showNotifications")); // NOI18N
        notificationsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        notificationsButton.setName("notificationsButton"); // NOI18N
        leftMenuButtonsPanel.add(notificationsButton);

        cartAssignmentButton.setAction(actionMap.get("showCartAssignment")); // NOI18N
        cartAssignmentButton.setText(resourceMap.getString("cartAssignmentButton.text")); // NOI18N
        cartAssignmentButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cartAssignmentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cartAssignmentButton.setName("cartAssignmentButton"); // NOI18N
        leftMenuButtonsPanel.add(cartAssignmentButton);

        menuButton.setAction(actionMap.get("showFoodAndBeverageScreen")); // NOI18N
        menuButton.setFocusable(false);
        menuButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        menuButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuButton.setName("cartMgtButton"); // NOI18N
        menuButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        leftMenuButtonsPanel.add(menuButton);

        onCourseAdsButton.setAction(actionMap.get("showOnCourseAdsPanel")); // NOI18N
        onCourseAdsButton.setText(resourceMap.getString("onCourseAdsButton.text")); // NOI18N
        onCourseAdsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        onCourseAdsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        onCourseAdsButton.setName("onCourseAdsButton"); // NOI18N
        leftMenuButtonsPanel.add(onCourseAdsButton);

        courseMgtButton.setAction(actionMap.get("showCourseManagementScreen")); // NOI18N
        courseMgtButton.setFocusable(false);
        courseMgtButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        courseMgtButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        courseMgtButton.setName("courseMgtButton"); // NOI18N
        courseMgtButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        leftMenuButtonsPanel.add(courseMgtButton);

        cartMgtButton.setAction(actionMap.get("showCartManagementScreen")); // NOI18N
        cartMgtButton.setFocusable(false);
        cartMgtButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cartMgtButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cartMgtButton.setName("cartMgtButton"); // NOI18N
        cartMgtButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        leftMenuButtonsPanel.add(cartMgtButton);

        showReportButton.setAction(actionMap.get("showReport")); // NOI18N
        showReportButton.setText(resourceMap.getString("showReportButton.text")); // NOI18N
        showReportButton.setToolTipText(resourceMap.getString("showReportButton.toolTipText")); // NOI18N
        showReportButton.setFocusable(false);
        showReportButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        showReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showReportButton.setName("showReportButton"); // NOI18N
        leftMenuButtonsPanel.add(showReportButton);

        drawingToolButton.setAction(actionMap.get("showDrawingTool")); // NOI18N
        drawingToolButton.setFocusable(false);
        drawingToolButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        drawingToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        drawingToolButton.setName("drawingToolButton"); // NOI18N
        drawingToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        leftMenuButtonsPanel.add(drawingToolButton);

        leftMenuPanel.add(leftMenuButtonsPanel, java.awt.BorderLayout.NORTH);

        logoPanel.setName("logoPanel"); // NOI18N
        logoPanel.setLayout(new java.awt.BorderLayout());
        leftMenuPanel.add(logoPanel, java.awt.BorderLayout.CENTER);

        add(leftMenuPanel, java.awt.BorderLayout.WEST);

        rightPanel.setMinimumSize(new java.awt.Dimension(650, 500));
        rightPanel.setName("rightPanel"); // NOI18N
        rightPanel.setPreferredSize(new java.awt.Dimension(650, 500));
        rightPanel.setLayout(new java.awt.BorderLayout());

        topButtonsToolbar.setName("topButtonsToolbar"); // NOI18N
        topButtonsToolbar.setLayout(new javax.swing.BoxLayout(topButtonsToolbar, javax.swing.BoxLayout.X_AXIS));

        zoomButtonsToolbar.setName("zoomButtonsToolbar"); // NOI18N
        zoomButtonsToolbar.setLayout(new javax.swing.BoxLayout(zoomButtonsToolbar, javax.swing.BoxLayout.X_AXIS));

        zoomInButton.setAction(actionMap.get("zoomIn")); // NOI18N
        zoomInButton.setText(resourceMap.getString("zoomInButton.text")); // NOI18N
        zoomInButton.setFocusable(false);
        zoomInButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton.setName("zoomInButton"); // NOI18N
        zoomInButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomButtonsToolbar.add(zoomInButton);

        zoomOutButton.setAction(actionMap.get("zoomOut")); // NOI18N
        zoomOutButton.setText(resourceMap.getString("zoomOutButton.text")); // NOI18N
        zoomOutButton.setFocusable(false);
        zoomOutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton.setName("zoomOutButton"); // NOI18N
        zoomOutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomButtonsToolbar.add(zoomOutButton);

        atacButton.setAction(actionMap.get("loadAtacCarts")); // NOI18N
        atacButton.setText(resourceMap.getString("atacButton.text")); // NOI18N
        atacButton.setName("atacButton"); // NOI18N
        atacButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                atacButtonItemStateChanged(evt);
            }
        });
        zoomButtonsToolbar.add(atacButton);

        pinLocationButton.setAction(actionMap.get("showPinLocationDialog")); // NOI18N
        pinLocationButton.setName("pinLocationButton"); // NOI18N
        pinLocationButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                pinLocationButtonItemStateChanged(evt);
            }
        });
        zoomButtonsToolbar.add(pinLocationButton);

        weatherAlertButton.setAction(actionMap.get("weatherAlert")); // NOI18N
        weatherAlertButton.setFocusable(false);
        weatherAlertButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        weatherAlertButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        weatherAlertButton.setName("weatherAlertButton"); // NOI18N
        weatherAlertButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                weatherAlertButtonItemStateChanged(evt);
            }
        });
        zoomButtonsToolbar.add(weatherAlertButton);

        cartPathOnlyButton.setAction(actionMap.get("cartPathOnly")); // NOI18N
        cartPathOnlyButton.setFocusable(false);
        cartPathOnlyButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cartPathOnlyButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cartPathOnlyButton.setName("cartPathOnlyButton"); // NOI18N
        cartPathOnlyButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cartPathOnlyButtonItemStateChanged(evt);
            }
        });
        zoomButtonsToolbar.add(cartPathOnlyButton);

        topButtonsToolbar.add(zoomButtonsToolbar);

        filler2.setName("filler2"); // NOI18N
        topButtonsToolbar.add(filler2);

        adminButton.setAction(actionMap.get("administratorLogin")); // NOI18N
        adminButton.setIcon(resourceMap.getIcon("adminButton.icon")); // NOI18N
        adminButton.setText(resourceMap.getString("adminButton.text")); // NOI18N
        adminButton.setFocusable(false);
        adminButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        adminButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        adminButton.setName("adminButton"); // NOI18N
        topButtonsToolbar.add(adminButton);

        rightPanel.add(topButtonsToolbar, java.awt.BorderLayout.NORTH);

        cardPanel.setName("cardPanel"); // NOI18N
        cardPanel.setLayout(new java.awt.CardLayout());
        cardLayout = new AnimatingCardLayout(new FadeAnimation());
        cardLayout.setAnimationDuration(500);
        cardPanel.setLayout(cardLayout);

        dashboardPanel.setName("dashboardPanel"); // NOI18N
        dashboardPanel.setLayout(new java.awt.BorderLayout());

        drawingSidebarPanel.setAnimated(false);
        drawingSidebarPanel.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("drawingSidebarPanel.border.lineColor"), 2)); // NOI18N
        drawingSidebarPanel.setCollapsed(true);
        drawingSidebarPanel.setDirection(org.jdesktop.swingx.JXCollapsiblePane.Direction.RIGHT);
        drawingSidebarPanel.setName("drawingSidebarPanel"); // NOI18N
        drawingSidebarPanel.getContentPane().setLayout(new java.awt.BorderLayout());

        drawingToolBarTitle.setFloatable(false);
        drawingToolBarTitle.setRollover(true);
        drawingToolBarTitle.setName("drawingToolBarTitle"); // NOI18N

        drawingToolTitleLabel.setFont(drawingToolTitleLabel.getFont().deriveFont(drawingToolTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, drawingToolTitleLabel.getFont().getSize()-4));
        drawingToolTitleLabel.setText(resourceMap.getString("drawingToolTitleLabel.text")); // NOI18N
        drawingToolTitleLabel.setName("drawingToolTitleLabel"); // NOI18N
        drawingToolBarTitle.add(drawingToolTitleLabel);

        drawingToolCloseButton.setAction(actionMap.get("closeDrawingTool")); // NOI18N
        drawingToolCloseButton.setFont(drawingToolCloseButton.getFont().deriveFont(drawingToolCloseButton.getFont().getSize()-4f));
        drawingToolCloseButton.setText(resourceMap.getString("drawingToolCloseButton.text")); // NOI18N
        drawingToolCloseButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        drawingToolCloseButton.setFocusable(false);
        drawingToolCloseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        drawingToolCloseButton.setName("drawingToolCloseButton"); // NOI18N
        drawingToolCloseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        drawingToolBarTitle.add(drawingToolCloseButton);

        drawingSidebarPanel.getContentPane().add(drawingToolBarTitle, java.awt.BorderLayout.NORTH);

        drawingToolScrollPane.setViewport(new JViewport() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, getSize().height);
            }
        });
        drawingToolScrollPane.setName("drawingToolScrollPane"); // NOI18N

        drawingTool.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        drawingTool.setName("drawingTool"); // NOI18N
        drawingToolScrollPane.setViewportView(drawingTool);

        drawingSidebarPanel.getContentPane().add(drawingToolScrollPane, java.awt.BorderLayout.CENTER);

        dashboardPanel.add(drawingSidebarPanel, java.awt.BorderLayout.EAST);

        cardPanel.add(dashboardPanel, "dashboardPanel");

        cartManagementPanel.setName("cartManagementPanel"); // NOI18N
        cardPanel.add(cartManagementPanel, "cartManagementPanel");

        menuScreen.setName("menuScreen"); // NOI18N
        cardPanel.add(menuScreen, "menuScreen");

        reportScreen.setName("reportScreen"); // NOI18N
        cardPanel.add(reportScreen, "reportScreen");

        onCourseAdsScrollPane.setName("onCourseAdsScrollPane"); // NOI18N

        onCourseAdsScreen.setName("onCourseAdsScreen"); // NOI18N
        onCourseAdsScrollPane.setViewportView(onCourseAdsScreen);

        cardPanel.add(onCourseAdsScrollPane, "onCourseAdsScreen");

        golfSiteSetupScreen.setName("golfSiteSetupScreen"); // NOI18N
        cardPanel.add(golfSiteSetupScreen, "golfSiteSetupScreen");

        rightPanel.add(cardPanel, java.awt.BorderLayout.CENTER);

        sidebarPanel.setAnimated(false);
        sidebarPanel.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("sidebarPanel.border.lineColor"), 2)); // NOI18N
        sidebarPanel.setCollapsed(true);
        sidebarPanel.setDirection(org.jdesktop.swingx.JXCollapsiblePane.Direction.RIGHT);
        sidebarPanel.setAnimated(true);
        sidebarPanel.setName("sidebarPanel"); // NOI18N
        sidebarPanel.getContentPane().setLayout(new java.awt.BorderLayout());

        sideBarTitle.setFloatable(false);
        sideBarTitle.setRollover(true);
        sideBarTitle.setName("sideBarTitle"); // NOI18N

        sidebarTitleLabel.setFont(sidebarTitleLabel.getFont().deriveFont(sidebarTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, sidebarTitleLabel.getFont().getSize()-2));
        sidebarTitleLabel.setText(resourceMap.getString("sidebarTitleLabel.text")); // NOI18N
        sidebarTitleLabel.setName("sidebarTitleLabel"); // NOI18N
        sideBarTitle.add(sidebarTitleLabel);

        sidebarCloseButton.setFont(sidebarCloseButton.getFont().deriveFont(sidebarCloseButton.getFont().getSize()-4f));
        sidebarCloseButton.setText(resourceMap.getString("sidebarCloseButton.text")); // NOI18N
        sidebarCloseButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        sidebarCloseButton.setFocusable(false);
        sidebarCloseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sidebarCloseButton.setName("sidebarCloseButton"); // NOI18N
        sidebarCloseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sidebarCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sidebarCloseButtonActionPerformed(evt);
            }
        });
        sideBarTitle.add(sidebarCloseButton);

        sidebarPanel.getContentPane().add(sideBarTitle, java.awt.BorderLayout.NORTH);

        sidebarCardPanel.setName("sidebarCardPanel"); // NOI18N
        sidebarCardPanel.setLayout(new java.awt.CardLayout());

        notificationsSidebar.setName("notificationsSidebar"); // NOI18N
        notificationsSidebar.setLayout(new java.awt.BorderLayout());

        notificationsPanel.setName("notificationsPanel"); // NOI18N
        notificationsSidebar.add(notificationsPanel, java.awt.BorderLayout.CENTER);

        sidebarCardPanel.add(notificationsSidebar, "notificationsSidebar");

        cartStatusSidebar.setName("cartStatusSidebar"); // NOI18N

        cartStatusTabSelectorButtonGroup.add(paceOfPlayButton);
        paceOfPlayButton.setSelected(true);
        paceOfPlayButton.setText(resourceMap.getString("paceOfPlayButton.text")); // NOI18N
        paceOfPlayButton.setName("paceOfPlayButton"); // NOI18N
        paceOfPlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paceOfPlayButtonActionPerformed(evt);
            }
        });

        cartStatusTabSelectorButtonGroup.add(cartTrackingButton);
        cartTrackingButton.setText(resourceMap.getString("cartTrackingButton.text")); // NOI18N
        cartTrackingButton.setName("cartTrackingButton"); // NOI18N
        cartTrackingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cartTrackingButtonActionPerformed(evt);
            }
        });

        cartStatusTabsPanel.setName("cartStatusTabsPanel"); // NOI18N
        cartStatusTabsPanel.setLayout(new java.awt.CardLayout());

        cartPacePanel.setName("cartPacePanel"); // NOI18N
        cartStatusTabsPanel.add(cartPacePanel, "cartPacePanel");

        cartTrackingPanel.setName("cartTrackingPanel"); // NOI18N
        cartStatusTabsPanel.add(cartTrackingPanel, "cartTrackingPanel");

        javax.swing.GroupLayout cartStatusSidebarLayout = new javax.swing.GroupLayout(cartStatusSidebar);
        cartStatusSidebar.setLayout(cartStatusSidebarLayout);
        cartStatusSidebarLayout.setHorizontalGroup(
            cartStatusSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cartTrackingButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(paceOfPlayButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cartStatusTabsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cartStatusSidebarLayout.setVerticalGroup(
            cartStatusSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartStatusSidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paceOfPlayButton)
                .addGap(0, 0, 0)
                .addComponent(cartTrackingButton)
                .addGap(18, 18, 18)
                .addComponent(cartStatusTabsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
        );

        sidebarCardPanel.add(cartStatusSidebar, "cartStatusSidebar");

        sidebarPanel.getContentPane().add(sidebarCardPanel, java.awt.BorderLayout.CENTER);

        rightPanel.add(sidebarPanel, java.awt.BorderLayout.EAST);

        add(rightPanel, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void pinLocationButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_pinLocationButtonItemStateChanged
        if (pinLocationButton.isSelected()) {
            createPinLocationDialog();
            pinLocationPanel.setShrinkWindow(pinLocationDialog);
            pinLocationPanel.readSettings();
            Application.getInstance(BaseStation2App.class).show(pinLocationDialog);
        }
        else {
            pinLocationPanel.cleanup();
            pinLocationDialog.setVisible(false);
            /*******************************************************************
             *
             *
             *
             *
             *               loadGolfClub();
             *
             *
             *
             *
             ******************************************************************/
        }

    }//GEN-LAST:event_pinLocationButtonItemStateChanged

    private void sidebarCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sidebarCloseButtonActionPerformed
        if (notificationsButton.isSelected() && notificationsSidebar.isVisible())
            showNotifications(null);
        else if (cartStatusButton.isSelected() && cartStatusSidebar.isVisible())
            showCartStatus(null);
    }//GEN-LAST:event_sidebarCloseButtonActionPerformed

    private void paceOfPlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paceOfPlayButtonActionPerformed
        ((CardLayout) cartStatusTabsPanel.getLayout()).show(cartStatusTabsPanel, cartPacePanel.getName());
    }//GEN-LAST:event_paceOfPlayButtonActionPerformed

    private void cartTrackingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cartTrackingButtonActionPerformed
        ((CardLayout) cartStatusTabsPanel.getLayout()).show(cartStatusTabsPanel, cartTrackingPanel.getName());
    }//GEN-LAST:event_cartTrackingButtonActionPerformed

    private void weatherAlertButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_weatherAlertButtonItemStateChanged

    }//GEN-LAST:event_weatherAlertButtonItemStateChanged

    private void cartPathOnlyButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cartPathOnlyButtonItemStateChanged

    }//GEN-LAST:event_cartPathOnlyButtonItemStateChanged

    private void atacButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_atacButtonItemStateChanged

    }//GEN-LAST:event_atacButtonItemStateChanged

    @Action
    public void showDashboardPanel() {
        screenManager.showScreen(dashboardScreen);
    }

    @Action//(enabledProperty=PROP_SHOWINGDASHBOARDSCREEN)
    public void showNotifications(ActionEvent e) {
        ApplicationAction action = getAction(MainActions.showNotifications);

        if (notificationsSidebar.isVisible() == false || sidebarPanel.isCollapsed()) {
            ((CardLayout) sidebarCardPanel.getLayout()).show(sidebarCardPanel, notificationsSidebar.getName());
            sidebarTitleLabel.setText("Notifications");
            action.setSelected(true);

            //If this comes from an UI event, unselect the other sidebar
            //otherwise, it should come from the notificationsListener, leave
            //the other button selected
            if (e != null && cartStatusButton.isSelected()) {
                cartStatusButton.setSelected(false);
            }
        }
        else {
            action.setSelected(false);
            if (cartStatusButton.isSelected()) {
                showCartStatus(null);
            }
        }

        updateSidebarStatus();
        //notificationsPanel.setNotificationsDisplayed();
        //Collapsed is bound to notificationsButton.isSelected()
        //notificationsSidebarPanel.setCollapsed(action.isSelected() == false);
    }

    @Action
    public void showCartStatus(ActionEvent e) {
        ApplicationAction action = getAction(MainActions.showCartStatus);

        if (cartStatusSidebar.isVisible() == false || sidebarPanel.isCollapsed()) {
            ((CardLayout) sidebarCardPanel.getLayout()).show(sidebarCardPanel, cartStatusSidebar.getName());
            sidebarTitleLabel.setText("Cart status");
            action.setSelected(true);

            if (e != null && notificationsButton.isSelected())
                notificationsButton.setSelected(false);
        }
        else {
            action.setSelected(false);
            if (notificationsButton.isSelected())
                showNotifications(null);
        }

        if (action.isSelected())
            cartTrackingPanel.setUp();
        else
            cartTrackingPanel.cleanup();

        updateSidebarStatus();
    }

    @Action(enabledProperty = PROP_SHOWINGDASHBOARDSCREEN)
    public void showPinLocationDialog(ActionEvent e) {
        ApplicationAction action = getAction(MainActions.showPinLocationDialog);

        if (e.getSource() == pinLocationButton)
            action.setSelected(pinLocationButton.isSelected());
        else
            action.setSelected(!pinLocationButton.isSelected());
    }

    @Action
    public void closePinLocationDialog() {
        pinLocationDialog.setVisible(false);
    }

    @Action
    public void showFoodAndBeverageScreen() {
        screenManager.showScreen(menuScreen);
    }

    @Action
    public void showCourseManagementScreen() {
        screenManager.showScreen(golfSiteSetupScreen);
    }

    @Action
    public void showCartManagementScreen() {
        screenManager.showScreen(cartManagementPanel);
    }

    @Action
    public void showDrawingTool() {
        //Toggle the drawing tool screen
        if (currentScreen.equals(drawingTool))
            screenManager.showScreen(dashboardScreen);
        else {
            screenManager.showScreen(drawingTool);
        }
    }
    @Action
    public void showReport() {
          //Toggle the drawing tool screen
        screenManager.showScreen(reportScreen);
    }

    @Action
    public void showOnCourseAdsPanel() {
        screenManager.showScreen(onCourseAdsScreen);
    }

    @Action(enabledProperty = "showingDashboard")
    public void zoomIn() {
        dashboardManager.getDashboard().zoomIn();
    }

    @Action(enabledProperty = "showingDashboard")
    public void zoomOut() {
        dashboardManager.getDashboard().zoomOut();
    }

    @Action
    public void closeDrawingTool() {
        drawingTool.exitThisScreen();
    }

    @Action
    public Task weatherAlert() {
        return settingsActions.weatherAlert(getAction(MainActions.weatherAlert));
    }

    @Action
    public Task cartPathOnly() {
        return settingsActions.cartPathOnly(getAction(MainActions.cartPathOnly));
    }

    @Action
    public void administratorLogin() {
        if (loginDialog == null) {
            JFrame frame = BaseStation2App.getApplication().getMainFrame();
            loginDialog = new LoginDialog(frame);
            loginDialog.setCourseService(services.getCourseService());
        }

        if (loginDialog.isConnected()) {
            loginDialog.disconnect();
        }
        else {
            loginDialog.connect();
        }

        setAdminMode(loginDialog.isConnected());
    }

    @Action(block = Task.BlockingScope.COMPONENT)
    public Task loadGolfClubImage(ActionEvent event) {
        return settingsActions.loadGolfClubImage();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox actionZonesCheckbox;
    private javax.swing.JToggleButton adminButton;
    private javax.swing.JToggleButton atacButton;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JButton cartAssignmentButton;
    com.stayprime.basestation2.ui.modules.CartManagementPanel cartManagementPanel;
    private javax.swing.JToggleButton cartMgtButton;
    com.stayprime.basestation2.ui.mainview.CartPacePanel cartPacePanel;
    private javax.swing.JToggleButton cartPathOnlyButton;
    private javax.swing.JCheckBox cartPathsCheckbox;
    private javax.swing.JToggleButton cartStatusButton;
    private org.jdesktop.swingx.JXPanel cartStatusSidebar;
    private javax.swing.ButtonGroup cartStatusTabSelectorButtonGroup;
    private org.jdesktop.swingx.JXPanel cartStatusTabsPanel;
    private javax.swing.JToggleButton cartTrackingButton;
    com.stayprime.basestation2.ui.mainview.CartTrackingPanel cartTrackingPanel;
    private javax.swing.JToggleButton courseMgtButton;
    private org.jdesktop.swingx.JXCollapsiblePane dashLayerPanel;
    private javax.swing.JToggleButton dashboardButton;
    private org.jdesktop.swingx.JXPanel dashboardControlsPanel;
    com.stayprime.basestation2.ui.DashboardManager dashboardManager;
    private javax.swing.JPanel dashboardPanel;
    private org.jdesktop.swingx.JXCollapsiblePane drawingSidebarPanel;
    com.stayprime.basestation2.ui.modules.DrawingTool drawingTool;
    private javax.swing.JToolBar drawingToolBarTitle;
    private javax.swing.JToggleButton drawingToolButton;
    private javax.swing.JButton drawingToolCloseButton;
    private javax.swing.JScrollPane drawingToolScrollPane;
    private javax.swing.JLabel drawingToolTitleLabel;
    private javax.swing.Box.Filler filler2;
    private com.stayprime.basestation.ui.site.GolfSiteSetupScreen golfSiteSetupScreen;
    private org.jdesktop.swingx.JXPanel leftMenuButtonsPanel;
    private org.jdesktop.swingx.JXPanel leftMenuPanel;
    private javax.swing.JLabel logoLabel;
    private org.jdesktop.swingx.JXPanel logoPanel;
    private javax.swing.JToggleButton menuButton;
    com.stayprime.basestation2.ui.modules.MenuScreen menuScreen;
    private javax.swing.JToggleButton notificationsButton;
    com.stayprime.basestation2.ui.mainview.NotificationsPanel notificationsPanel;
    private org.jdesktop.swingx.JXPanel notificationsSidebar;
    private javax.swing.JButton onCourseAdsButton;
    com.stayprime.basestation2.ui.modules.OnCourseAdsScreen onCourseAdsScreen;
    private javax.swing.JScrollPane onCourseAdsScrollPane;
    private javax.swing.JToggleButton paceOfPlayButton;
    private javax.swing.JToggleButton pinLocationButton;
    private javax.swing.JCheckBox pinLocationCheckbox;
    com.stayprime.basestation2.ui.mainview.PinLocationPanel pinLocationPanel;
    private javax.swing.JCheckBox positionCheckbox;
    private com.stayprime.basestation2.ui.reports.ReportScreen reportScreen;
    private org.jdesktop.swingx.JXPanel rightPanel;
    private javax.swing.JToggleButton showReportButton;
    private javax.swing.JToolBar sideBarTitle;
    private org.jdesktop.swingx.JXPanel sidebarCardPanel;
    private javax.swing.JButton sidebarCloseButton;
    private org.jdesktop.swingx.JXCollapsiblePane sidebarPanel;
    private javax.swing.JLabel sidebarTitleLabel;
    private org.jdesktop.swingx.JXPanel topButtonsToolbar;
    private javax.swing.JToggleButton weatherAlertButton;
    private org.jdesktop.swingx.JXPanel zoomButtonsToolbar;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JButton zoomInButton1;
    private javax.swing.JButton zoomOutButton;
    private javax.swing.JButton zoomOutButton1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public void setConfig(PropertiesConfiguration configuration) {
        if (configuration.getBoolean("showWeatherButton", false) == false)
            zoomButtonsToolbar.remove(weatherAlertButton);
        if (configuration.getBoolean("showCartPathButton", false) == false)
            zoomButtonsToolbar.remove(cartPathOnlyButton);
        if (configuration.getBoolean("showAtacButton", false) == false)
            zoomButtonsToolbar.remove(atacButton);

        if (configuration.getBoolean("showNotificationsButton", true) == false)
            leftMenuButtonsPanel.remove(notificationsButton);
        if (configuration.getBoolean("showCartStatusButton", true) == false)
            leftMenuButtonsPanel.remove(cartStatusButton);
        if (configuration.getBoolean(Constant.CONFIG_SHOWCARTASSIGNMENT, false) == false)
            leftMenuButtonsPanel.remove(cartAssignmentButton);
        if (configuration.getBoolean("showFoodAndBeverageButton", false) == false)
            leftMenuButtonsPanel.remove(menuButton);
        if (configuration.getBoolean("showReportButton", false) == false)
            leftMenuButtonsPanel.remove(showReportButton);

        printCoordinates = configuration.getBoolean("printCoordinates", true);
    }

    private void updateSidebarStatus() {
        if (notificationsButton.isSelected() || cartStatusButton.isSelected())
            sidebarPanel.setCollapsed(false);
        else
            sidebarPanel.setCollapsed(true);
    }

    private void installDrawingTool() {
        Dashboard dashboard = dashboardManager.getDashboard();

        if (drawingSidebarPanel.isCollapsed()) {
            drawingSidebarPanel.setCollapsed(false);
            //if(dashboard != null)
            //    dashboard.setTransientDraw(true);
        }

        if (dashboard != null) {
            dashboard.setMaxScaleFactor(Math.pow(1.5, 10));
        }

        drawingTool.setDashboard(dashboard);
        drawingTool.setDashboardObjectRenderer(dashboardManager.getObjectRenderer());
        topButtonsToolbar.add(zoomInButton1, 0);
        topButtonsToolbar.add(zoomOutButton1, 1);
        dashboardManager.setDrawingToolInstalled(true);
    }

    private void uninstallDrawingTool() {
        Dashboard dashboard = dashboardManager.getDashboard();

        if (!drawingSidebarPanel.isCollapsed()) {
            drawingSidebarPanel.setCollapsed(true);
            //if(dashboard != null)
            //    dashboard.setTransientDraw(true);
        }
        if (dashboard != null) {
            //dashboard.removeDashboardListener(drawingTool);
            dashboard.setMaxScaleFactor(Math.pow(1.5, 4));
        }
        drawingTool.setDashboard(null);
        topButtonsToolbar.remove(zoomInButton1);
        topButtonsToolbar.remove(zoomOutButton1);

        dashboardManager.setDrawingToolInstalled(false);
    }

    private void installScreenToolbarComponents(Screen screen) {
        topButtonsToolbar.remove(toolbarGlue);
        if (screen != null && screen.getToolbarComponent() != null) {
            int i = 0;
            Component c = screen.getToolbarComponent();
            if (c.getParent() != null)
                c.getParent().remove(c);

            topButtonsToolbar.add(c, 0);
            topButtonsToolbar.validate();
            topButtonsToolbar.repaint();
        }
        else
            topButtonsToolbar.add(toolbarGlue, 0);
    }

    private void uninstallScreenToolbarComponents(Screen screen) {
        if (screen != null && screen.getToolbarComponent() != null) {
            Component c = screen.getToolbarComponent();
            topButtonsToolbar.remove(c);
        }
    }

    private class NotificationsBlinkTimer extends Timer {

        public NotificationsBlinkTimer(int delay) {
            super(delay, null);
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (notificationsButton.getBackground() == Color.red)
                        notificationsButton.setBackground(null);
                    else
                        notificationsButton.setBackground(Color.red);
                }
            });
        }

        @Override
        public void stop() {
            super.stop();
            notificationsButton.setBackground(null);
        }
    }

    private class NotificationsChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(NotificationsPanel.PROP_NEWNOTIFICATIONARRIVED)) {
                if (notificationsPanel.isNewNotificationArrived()) {
                    notificationsBlinkTimer.start();
                    ApplicationAction action = getAction(MainActions.showNotifications);

                    if (!action.isSelected() || !notificationsSidebar.isVisible()) {
                        showNotifications(null);
                        notificationsPanel.setNewNotificationArrived(false);
                    }
                }
                else
                    notificationsBlinkTimer.stop();
            }
//            else if (evt.getPropertyName().equals(NotificationsPanel.PROP_NOTIFICATIONSLISTEMPTY)) {
//                if (notificationsPanel.isNotificationsListEmpty()) {
//                    ApplicationAction action = getAction(MainActions.showNotifications);
//
//                    if (action.isSelected()) {
//                        showNotifications(null);
//                    }
//                }
//            }
        }
    }

    @Action(block = Task.BlockingScope.APPLICATION)
    public Task showCartAssignment() {
        if (cartAssignmentEditor == null) {
            cartAssignmentEditor = new CartAssignmentEditor();
            cartAssignmentEditor.setCartService(services.getCartService());
        }
        return cartAssignmentEditor.load();
    }
    
    public LocalStorage getLocalStorage() {
        return settingsActions.getSettingsManager().getLocalStorage();
    }

}
