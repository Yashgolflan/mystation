/*
 * 
 */
package com.stayprime.basestation2.ui;

import com.aeben.elementos.mapview.BasicObjectRenderer;
import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.elementos.mapview.ObjectRenderer;
import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.DrawableCourseImage;
import com.aeben.golfclub.DrawableCoursePoint;
import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.DrawableHole;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.HoleDefinition;
import com.aeben.golfclub.utils.Formatters;
import com.aeben.golfclub.view.Dashboard;
import com.stayprime.basestation2.ui.mainview.DashboardBalloonLayer;
import com.stayprime.basestation2.Constant;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.basestation2.renderers.DashboardCartRenderer;
import com.stayprime.basestation2.renderers.DashboardObjectRenderer;
import com.stayprime.basestation2.renderers.HoleNameRenderer;
import com.stayprime.basestation2.ui.modules.DrawingTool;
import com.stayprime.cartapp.CartAppConst;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.imageview.CachedImageConverter;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class DashboardManager {
    private PropertiesConfiguration config;

    private Dashboard dashboard;
    private DrawingTool drawingTool;
    private DashboardObjectRenderer dashboardObjectRenderer;
    private DashboardObjectRenderer objectRenderer;
    private ObjectRenderer holeRenderer;
    private ObjectRenderer pinLocationRenderer;
    private DashboardCartRenderer cartRenderer;

    private DashboardBalloonLayer balloonLayer;

    private GolfClub golfClub;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private boolean showCartPaths;
    public static final String PROP_SHOWCARTPATHS = "showCartPaths";
    private boolean showActionZones;
    public static final String PROP_SHOWACTIONZONES = "showActionZones";
    private boolean showPinFlags;
    public static final String PROP_SHOWPINFLAGS = "showPinFlags";
    private boolean showCarts;
    public static final String PROP_SHOWCARTS = "showCarts";
    private boolean drawingToolInstalled;
    public static final String PROP_DRAWINGTOOLINSTALLED = "drawingToolInstalled";

    public DashboardManager() {
        this(new PropertiesConfiguration());
    }

    public DashboardManager(PropertiesConfiguration config) {
        this.config = config;
    }
    
    public void init() {
        if (dashboard != null) {
            throw new IllegalStateException("DashboardManager already initialized");
        }
        dashboard = new Dashboard();
        dashboard.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        createObjectRenderers();
        createBalloonLayer();
    }

    public void setCourseSettingsManager(final CourseSettingsManager settingsManager) {
        settingsManager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(CourseSettingsManager.PROP_PACEOFPLAYTHRESHOLD)) {
                    int thresholds[] = Formatters.getPaceOfPlayThresholdArray(settingsManager.getPaceOfPlayThreshold());
                    if(thresholds != null) {
                        cartRenderer.setPaceOfPlayThresholds(thresholds[0], thresholds[1]);
                    }
                }
            }
        });
    }

    public void setup() {
        if (dashboard == null) {
            throw new IllegalStateException("DashboardManager not initialized");
        }
        installDashboardRenderers();
        addListeners();
    }

    private void createObjectRenderers() {
        dashboardObjectRenderer = new DashboardObjectRenderer();
        dashboardObjectRenderer.setDashboard(dashboard);

        objectRenderer = dashboardObjectRenderer;
        holeRenderer = new HoleNameRenderer();
        pinLocationRenderer = new BasicObjectRenderer(new CachedImageConverter());
        cartRenderer = new DashboardCartRenderer();

        boolean renderCp = config.getBoolean(Constant.CONFIG_RENDERCARTPATHWIDTH, false);
        dashboardObjectRenderer.setRenderCartPathWidth(renderCp);
        String[] cpWidth = config.getString(Constant.CONFIG_CARTPATHWIDTH, "4,5").split(",");

        if (ArrayUtils.isNotEmpty(cpWidth)) {
            float width = NumberUtils.toFloat(cpWidth[0], 8f);
            dashboardObjectRenderer.setCartPathWidth(width);

            if (cpWidth.length >= 2) {
                float warninWidth = NumberUtils.toFloat(cpWidth[1], 16f);
                dashboardObjectRenderer.setCartPathWarningWidth(warninWidth);
            }
        }
    }

    private void createBalloonLayer() {
        balloonLayer = new DashboardBalloonLayer(dashboard, cartRenderer);
        cartRenderer.setRenderListener(balloonLayer);
    }

    private void addListeners() {
        PropertyChangeListener installRenderersSelectionListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                installDashboardRenderers();
            }
        };

        propertyChangeSupport.addPropertyChangeListener(PROP_SHOWCARTS, installRenderersSelectionListener);
        propertyChangeSupport.addPropertyChangeListener(PROP_DRAWINGTOOLINSTALLED, installRenderersSelectionListener);

        PropertyChangeListener showLayersSelectionListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setObjectRendererObjects();
            }
        };

        propertyChangeSupport.addPropertyChangeListener(PROP_SHOWPINFLAGS, showLayersSelectionListener);
        propertyChangeSupport.addPropertyChangeListener(PROP_SHOWCARTPATHS, showLayersSelectionListener);
        propertyChangeSupport.addPropertyChangeListener(PROP_SHOWACTIONZONES, showLayersSelectionListener);

//	//Call mainPanel.loadGolfClub() when dashboard is shown
//        screenChangeListener = new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                if(evt.getNewValue() instanceof BasicScreen) {
//                    BasicScreen basicScreen = (BasicScreen) evt.getNewValue();
//                    if(basicScreen.getScreenComponent() == dashboard) {
//                        mainPanel.loadGolfClub();
//                    }
//                }
//            }
//        };
    }

    public DrawableCourseImage createDefaultPinflag() {
        String flagCenterString = config.getString(Constant.CONFIG_PINFLAGCENTER, "0.11,0.98");
        String[] center = flagCenterString.split(",");
        Point2D.Double centerPoint = new Point2D.Double(Double.parseDouble(center[0]), Double.parseDouble(center[1]));

        Float flagPixels = config.getFloat(Constant.CONFIG_PINFLAGPIXELSIZE, null);
        Float flagMeters = config.getFloat(Constant.CONFIG_PINFLAGSIZE, null);
        String pinFlagImage = config.getString(Constant.CONFIG_PINFLAGIMAGE, Constant.DEFAULT_FLAG_IMAGE);

        DrawableCourseImage flag = new DrawableCourseImage(null, null, null,
                GolfCourseObject.ObjectType.PINFLAG, pinFlagImage, null,
                flagMeters, flagPixels, null, centerPoint);

        return flag;
    }

    public void cartsStatusUpdated(List<CartInfo> carts) {
        cartRenderer.cartInfoUpdated(carts);
        dashboard.repaint();

        if (balloonLayer != null) {
            balloonLayer.updateBalloonTips(carts);
        }
    }

    public DashboardObjectRenderer getObjectRenderer() {
        return objectRenderer;
    }

    public DashboardCartRenderer getCartRenderer() {
        return cartRenderer;
    }

    public void setHoleRenderer(ObjectRenderer holeNumberRenderer) {
        this.holeRenderer = holeNumberRenderer;
        installDashboardRenderers();
    }

    public void setPinLocationRenderer(ObjectRenderer pinLocationRenderer) {
        this.pinLocationRenderer = pinLocationRenderer;
        installDashboardRenderers();
    }

    public void setCartRenderer(DashboardCartRenderer cartRenderer) {
        this.cartRenderer = cartRenderer;
        installDashboardRenderers();
    }

    public void setDrawingTool(DrawingTool drawingTool) {
        this.drawingTool = drawingTool;
    }

    private void installDashboardRenderers() {
        if(dashboard != null) {
	    if(objectRenderer != null)
		objectRenderer.reset();

	    if(isDrawingToolInstalled()) {
                installDashboardRenderer(objectRenderer, false);
                installDashboardRenderer(holeRenderer, true);
                installDashboardRenderer(pinLocationRenderer, false);
                installDashboardRenderer(cartRenderer, false);
                installDashboardRenderer(drawingTool, true);
            }
            else {
                installDashboardRenderer(objectRenderer, true);
		installDashboardRenderer(holeRenderer, true);
                installDashboardRenderer(pinLocationRenderer, true);
                installDashboardRenderer(cartRenderer, isShowCarts());
                installDashboardRenderer(drawingTool, false);
            }
            //dashboard.repaint();
        }

    }

    private void installDashboardRenderer(ObjectRenderer renderer, boolean showRenderer) {
        if(showRenderer)
            dashboard.addObjectRenderer(renderer);
        else
            dashboard.removeObjectRenderer(renderer);

    }

    public void setGolfCLub(GolfClub golfClub) {
        this.golfClub = golfClub;
        setObjectRendererObjects();
    }

    public void setObjectRendererObjects() {
        List<DrawableObject> holes = new ArrayList<DrawableObject>();
        List<DrawableObject> objects = new ArrayList<DrawableObject>();
        List<DrawableObject> shapes = new ArrayList<DrawableObject>();
        List<DrawableObject> cartPaths = new ArrayList<DrawableObject>();
        List<DrawableObject> pinFlags = new ArrayList<DrawableObject>();

        if(golfClub != null) {
            for(GolfCourseObject object: golfClub.getCourseObjects()) {
                if(object instanceof DrawableObject)
                    shapes.add((DrawableObject) object);
            }

            for(CourseDefinition course: golfClub.getCourses()) {
                for(int n = 1; n <= course.getHoleCount(); n++) {
                    HoleDefinition hole = course.getHoleNumber(n);

                    if(hole != null) {
			holes.add(new DrawableHole(hole));

                        for(DrawableCourseShape object: hole.getShapes()) {
                            shapes.add(object);
                        }
                        for(DrawableCoursePoint object: hole.getPoints()) {
                            shapes.add(object);
                        }

                        if(hole.cartPath instanceof DrawableObject)
                            cartPaths.add((DrawableObject) hole.cartPath);

                        if(hole.green instanceof DrawableObject)
                            shapes.add((DrawableObject) hole.green);

//                        if(hole.getApproachLine() instanceof DrawableObject)
//                            shapes.add((DrawableObject) hole.getApproachLine());

                        if(hole.pinLocation instanceof DrawableCoursePoint) {
                            DrawableCoursePoint point = (DrawableCoursePoint) hole.pinLocation;
                            DrawableCourseImage flag = createDefaultPinflag();
                            flag.name = point.name;
                            flag.id = n;
                            flag.setParent(hole);
                            flag.coordinates = point.coordinates;
                            pinFlags.add(flag);
                        }
                    }
                }
            }

            if(holeRenderer != null) {
		holeRenderer.setDrawableObjects(holes);
	    }

            if(objectRenderer != null) {
                if(isShowActionZones())
                    objects.addAll(shapes);
                if(isShowCartPaths())
                    objects.addAll(cartPaths);
                objectRenderer.setDrawableObjects(objects);
            }

            if(pinLocationRenderer != null) {
                if(isShowPinFlags())
                    pinLocationRenderer.setDrawableObjects(pinFlags);
                else
                    pinLocationRenderer.setDrawableObjects(null);
            }

            if(dashboard != null) {
                dashboard.repaint();
            }
        }

    }

    /*
     * Interface status properties getters and setters
     */

    public Dashboard getDashboard() {
        return dashboard;
    }

    public boolean isShowActionZones() {
        return showActionZones;
    }

    public void setShowActionZones(boolean showActionZones) {
        boolean oldShowActionZones = this.showActionZones;
        this.showActionZones = showActionZones;
        propertyChangeSupport.firePropertyChange(PROP_SHOWACTIONZONES, oldShowActionZones, showActionZones);
    }

    public boolean isShowCartPaths() {
        return showCartPaths;
    }

    public void setShowCartPaths(boolean showCartPaths) {
        boolean oldShowCartPaths = this.showCartPaths;
        this.showCartPaths = showCartPaths;
        propertyChangeSupport.firePropertyChange(PROP_SHOWCARTPATHS, oldShowCartPaths, showCartPaths);
    }

    public boolean isShowPinFlags() {
        return showPinFlags;
    }

    public void setShowPinFlags(boolean showPinFlags) {
        boolean oldShowPinFlags = this.showPinFlags;
        this.showPinFlags = showPinFlags;
        propertyChangeSupport.firePropertyChange(PROP_SHOWPINFLAGS, oldShowPinFlags, showPinFlags);
    }

    public boolean isShowCarts() {
        return showCarts;
    }

    public void setShowCarts(boolean showCarts) {
        boolean oldShowCarts = this.showCarts;
        this.showCarts = showCarts;
        propertyChangeSupport.firePropertyChange(PROP_SHOWCARTS, oldShowCarts, showCarts);
    }

    public boolean isDrawingToolInstalled() {
        return drawingToolInstalled;
    }

    public void setDrawingToolInstalled(boolean drawingToolInstalled) {
        boolean oldDrawingToolInstalled = this.drawingToolInstalled;
        this.drawingToolInstalled = drawingToolInstalled;
        propertyChangeSupport.firePropertyChange(PROP_DRAWINGTOOLINSTALLED, oldDrawingToolInstalled, drawingToolInstalled);
    }

    /*
     * Property change support
     */
    public void addPropertyChangeListener(PropertyChangeListener listener ) {
        propertyChangeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener(PropertyChangeListener listener ) {
        propertyChangeSupport.removePropertyChangeListener( listener );
    }

}
