/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PinLocationPanel.java
 *
 * Created on 4/04/2011, 09:50:52 AM
 */

package com.stayprime.basestation2.ui.mainview;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.basestation2.renderers.DashboardObjectRenderer;
import com.stayprime.basestation2.renderers.DistanceRenderer;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.view.Dashboard;
import com.stayprime.util.image.ImageRetrieverTask;
import com.stayprime.util.image.ImageRetrieverTask.ImageRetrievedCallback;
import com.stayprime.imageview.CachedImageConverter;
import com.aeben.elementos.mapview.DrawableMapImage;
import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.DrawableCourseImage;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.HoleDefinition;
import com.aeben.golfclub.DrawablePinLocation;
import com.aeben.golfclub.utils.Formatters;
import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.elementos.mapview.MapImageRenderer;
import com.aeben.elementos.mapview.ObjectRenderer;
import com.aeben.golfcourse.util.DistanceUnits;
import com.ezware.dialog.task.TaskDialog;
import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.ui.DashboardManager;
import com.stayprime.basestation2.ui.custom.JTableClickListener;
import com.stayprime.basestation2.util.CourseRenderUtil;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.basestation2.util.PinLocationUtil;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import com.stayprime.geo.MapUtils;
import com.stayprime.hibernate.entities.PinLocation;
import com.stayprime.util.UIUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationAction;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class PinLocationPanel extends javax.swing.JPanel
        implements MouseListener, MouseMotionListener {
    private static final Logger log = LoggerFactory.getLogger(PinLocationPanel.class);

    private GolfClub golfClub;

    private DashboardObjectRenderer pinLocationRenderer;
    private DistanceRenderer distanceRenderer;
    private MapImageRenderer mapImageRenderer;
    private List<DrawablePinLocation> originalPinLocations;
    private List<DrawablePinLocation> modifiedPinLocations;
    private List<DrawableGreen> greens;

    private List<ObjectRenderer> dashboardRenderers;

    private Dashboard dashboard;

    private Window shrinkWindow;
    private Dimension shrinkWindowSize;
    public DialogCallback callback;

    private HoleDefinition selectedHole;

    private float fontSize = 16f;

    protected boolean pinLocationChanged = false;
    public static final String PROP_PINLOCATIONCHANGED = "pinLocationChanged";

    protected boolean pinLocationSelected = false;
    public static final String PROP_PINLOCATIONSELECTED = "pinLocationSelected";

    private boolean pinLocationsSelected;
    public static final String PROP_PINLOCATIONSSELECTED = "pinLocationsSelected";

    private boolean zoomIntoPin = false;
    public static final String PROP_ZOOMINTOPIN = "zoomIntoPin";

    private CourseService courseService;
    
    public PinLocationPanel() {
        initComponents();

        distanceRenderer = new DistanceRenderer();
	distanceRenderer.setOutlineColor(Color.BLACK);
	distanceRenderer.setFillColor(Color.WHITE);

	mapImageRenderer = new MapImageRenderer();

        pinLocationRenderer = new DashboardObjectRenderer(new CachedImageConverter());

        gridNumberComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    changePinLocationRendererGrid(e.getItem());
                }
            }
        });

        pinLocationTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                setPinLocationSelected(pinLocationTable.getSelectedRowCount() == 1);
                setPinLocationsSelected(pinLocationTable.getSelectedRowCount() > 0);

                if(isZoomIntoPin() && dashboard != null && pinLocationTable.getSelectedRowCount() == 1) {
                    int row = pinLocationTable.getSelectedRow();
                    Object o = pinLocationTable.getValueAt(row, 0);

                    if(o instanceof DrawablePinLocation) {
                        DrawablePinLocation pinLocation = (DrawablePinLocation) o;

			if(pinLocation.coordinates != null) {
                            double scale = dashboard.getMaxScale()/dashboard.getScale();
                            dashboard.zoomInAndCenter(pinLocation.getCoordinates(), scale);
                        }
                    }
                }
            }
        });

        pinLocationTable.setDefaultRenderer(Object.class, new PinlocationTableCellRenderer());

        JTableClickListener.installClickListener(pinLocationTable, new JTableClickListener() {
            @Override
            public void tableClicked(JTable table, int row, boolean selected, MouseEvent e) {
                if(selected && isPinLocationSelected() && e.getClickCount() == 2) {
		    ApplicationAction pick = (ApplicationAction) Application.getInstance().getContext().
			    getActionMap(PinLocationPanel.this).get("pickPinLocation");
                    pick.setSelected(true);
                }
            }
        });
    }

    public void setShrinkWindow(Window shrinkWindow) {
        this.shrinkWindow = shrinkWindow;
        shrinkWindowSize = null;
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;

        if(dashboard != null) {
            dashboard.addPropertyChangeListener(pinLocationRenderer);
	}
    }

    public void setCallback(DialogCallback dialogCallback) {
	this.callback = dialogCallback;
    }

    public boolean isPinLocationChanged() {
        return pinLocationChanged;
    }

    public void setPinLocationChanged(boolean pinLocationChanged) {
        boolean oldPinLocationChanged = this.pinLocationChanged;
        this.pinLocationChanged = pinLocationChanged;
        firePropertyChange(PROP_PINLOCATIONCHANGED, oldPinLocationChanged, pinLocationChanged);
    }

    public boolean isPinLocationSelected() {
        return pinLocationSelected;
    }

    public void setPinLocationSelected(boolean pinLocationSelected) {
        boolean oldPinLocationSelected = this.pinLocationSelected;
        this.pinLocationSelected = pinLocationSelected;
        firePropertyChange(PROP_PINLOCATIONSELECTED, oldPinLocationSelected, pinLocationSelected);
    }

    public boolean isPinLocationsSelected() {
	return pinLocationsSelected;
    }

    public void setPinLocationsSelected(boolean pinLocationsSelected) {
	boolean oldPinLocationsSelected = this.pinLocationsSelected;
	this.pinLocationsSelected = pinLocationsSelected;
	firePropertyChange(PROP_PINLOCATIONSSELECTED, oldPinLocationsSelected, pinLocationsSelected);
    }

    public boolean isZoomIntoPin() {
	return zoomIntoPin;
    }

    public void setZoomIntoPin(boolean zoomIntoPin) {
	boolean oldZoomIntoPin = this.zoomIntoPin;
	this.zoomIntoPin = zoomIntoPin;
	firePropertyChange(PROP_ZOOMINTOPIN, oldZoomIntoPin, zoomIntoPin);
    }

    public void readSettings() {
        CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();

        golfClub = null;
        pickLocationButton.setSelected(false);

        DefaultComboBoxModel model = (DefaultComboBoxModel) gridNumberComboBox.getModel();
        model.removeAllElements();
        int grids = courseSettingsManager.getGridSystemNumberOfGrids();

        if(dashboard != null) {
            dashboard.setMaxScaleFactor(Math.pow(1.5, 8));
            dashboard.addMouseListener(this);
            dashboard.addMouseMotionListener(this);

            dashboardRenderers = new ArrayList<ObjectRenderer>(dashboard.getObjectRenderers());

            dashboard.setObjectRenderers(null);
            dashboard.addObjectRenderer(mapImageRenderer);
            dashboard.addObjectRenderer(pinLocationRenderer);
            dashboard.addObjectRenderer(distanceRenderer);
        }
        else
            dashboardRenderers = null;

        if(courseSettingsManager != null && grids > 0) {
            if(dashboard != null) {
                dashboard.putClientProperty(DashboardObjectRenderer.PROP_RENDER_GREEN_GRID, true);
                dashboard.putClientProperty(DashboardObjectRenderer.PROP_RENDER_GREEN_POINTS, true);
                dashboard.repaint();
            }

            for(int n = 1; n <= grids; n++)
                model.addElement(n);
            
            model.setSelectedItem(courseSettingsManager.getGridSystemCurrentGrid());
            gridNumberComboBox.setEnabled(true);
        }
        else {
            gridNumberComboBox.setEnabled(false);
        }

//        if(pinLocationRenderer != null) {
//            originalRendererObjects = pinLocationRenderer.getDrawableObjects();
//        }
//        else {
//            originalRendererObjects = null;
//        }

        setGolfClub(BaseStation2App.getApplication().getGolfClub());

    }

    void cleanup() {
        cleanup(true);
    }

    void cleanup(boolean revertObjects) {
        pickLocationButton.setSelected(false);
        showPinLocationButtonOnly(false);
	dashboard.setPanEnabled(true);

	if(dashboard != null) {
	    if(revertObjects) {
		//pinLocationRenderer != null) {// && originalRendererObjects != null)
		//pinLocationRenderer.setDrawableObjects(originalRendererObjects);
		dashboard.setObjectRenderers(dashboardRenderers);
		((DefaultTableModel)pinLocationTable.getModel()).setRowCount(0);
		dashboard.removeMouseListener(this);
		dashboard.removeMouseMotionListener(this);
		dashboard.removeObjectRenderer(distanceRenderer);
		dashboard.setMaxScaleFactor(Math.pow(1.5, 4));
		dashboard.putClientProperty(DashboardObjectRenderer.PROP_RENDER_GREEN_GRID, false);
		dashboard.putClientProperty(DashboardObjectRenderer.PROP_RENDER_GREEN_POINTS, false);
		dashboard.repaint();
	    }

	    mapImageRenderer.setDrawableObjects(null);
	    dashboard.setCursor(Cursor.getDefaultCursor());
	    dashboard.repaint();
	}
    }

    private void changePinLocationRendererGrid(Object item) {
        if(golfClub == null || modifiedPinLocations == null)
            return;

        int gridNumber = 0;
        StringBuilder warning = new StringBuilder();

        if(item instanceof Integer)
            gridNumber = (Integer) item;

        for(CourseDefinition courseDefinition: golfClub.getCourses()) {
            for(int n = 1; n <= courseDefinition.getHoleCount(); n++) {
                HoleDefinition hole = courseDefinition.getHoleNumber(n);
                if(hole != null) {
                    if(hole.pinLocation != null) {
                        if(gridNumber > 0) {
                            if(hole.green instanceof DrawableGreen) {
                                DrawableGreen green = (DrawableGreen) hole.green;
                                if(green.gridPoints != null && gridNumber <= green.gridPoints.size()) {

                                    boolean found = false;
                                    for(DrawablePinLocation pin: modifiedPinLocations) {
                                        if(pin.getParentObject() instanceof HoleDefinition) {
                                            if(pin.getParentObject().equals(hole)) {
                                                found = true;
                                                pin.coordinates = green.gridPoints.get(gridNumber - 1);
                                                setPinLocationChanged(true);
                                            }
                                        }
                                    }

                                    if(!found)
                                        warning.append("Hole ").append(hole.number).append(" pin was not set\n");
                                }
                                else
                                    warning.append("Hole ").append(hole.number).append(" pin was not set: The hole's grid configuration is incompatible.\n");
                            }
                            else
                                warning.append("Hole ").append(hole.number).append(" pin was not set: The hole definition is null.\n");
                        }

                    }
                }
            }
        }

        setPinLocationRendererObjects();

        if(warning.length() > 0) {
            UIUtil.showDetailedTaskDialog(BaseStation2App.getApplication().getMainFrame(),
                    "Pin Location System Warning",
                    "There were warnings setting the Grid number:",
                    warning.toString(),
                    "Warning", TaskDialog.StandardIcon.WARNING, true);
        }

    }

    private void setPinLocationRendererObjects() {
        List<DrawableObject> pinLocationObjects = new ArrayList<DrawableObject>();
        DashboardManager manager = BaseStation2App.getApplication().getDashboardManager();

        if(manager != null && modifiedPinLocations != null) {
            for(DrawablePinLocation pin: modifiedPinLocations) {
                DrawableCourseImage pinFlag = manager.createDefaultPinflag();
                pinFlag.coordinates = pin.coordinates;
                pinLocationObjects.add(pinFlag);
            }
        }

        if(greens != null) {
            pinLocationObjects.addAll(0, greens);
        }

        pinLocationRenderer.setDrawableObjects(pinLocationObjects);
        dashboard.repaint();

    }

    public void setGolfClub(GolfClub golfClub) {
        CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();

        this.golfClub = golfClub;

        originalPinLocations = new ArrayList<DrawablePinLocation>();
        modifiedPinLocations = new ArrayList<DrawablePinLocation>();
        greens = new ArrayList<DrawableGreen>();

        StringBuilder warning = new StringBuilder();
        if(golfClub != null) {

            int grids = 0;
            if(courseSettingsManager != null)
                grids = courseSettingsManager.getGridSystemNumberOfGrids();

            DefaultTableModel model = (DefaultTableModel) pinLocationTable.getModel();
            model.setRowCount(0);

            for(CourseDefinition course: golfClub.getCourses()) {
                if(course != null) {
                    for(int n = 1; n <= course.getHoleCount(); n++) {
                        HoleDefinition hole = course.getHoleNumber(n);

                        if(grids > 0) {
                            if(hole == null)
                                warning.append("Hole ").append(n).append(" is not defined.\n");
                            else if(hole.green == null)
                                warning.append("Hole ").append(n).append(" green is not defined.\n");
                            else if(hole.green.gridPoints == null || hole.green.gridPoints.size() == 0)
                                warning.append("Hole ").append(n).append(" green grid is not configured.\n");
                            else if(hole.green.gridPoints.size() != grids)
                                warning.append("Hole ").append(n).append(
                                        " green grid size is different to the  configured number of grids.\n" +
                                        "    Please configure it using the Drawing Tool.\n");
                        }

                        if(hole != null) {
                            if(hole.green != null)
                                greens.add(hole.green);

                            if(hole.pinLocation == null)
                                hole.pinLocation = new DrawablePinLocation("Pin " + n, n, hole, null);

                            originalPinLocations.add(hole.pinLocation);
                            DrawablePinLocation pin = hole.pinLocation.clone();
                            modifiedPinLocations.add(pin);
                            model.addRow(new Object[] {pin, hole.pinLocation.lastUpdated});
                        }
                    }
                }
            }
        }

        setPinLocationRendererObjects();
        setPinLocationChanged(false);

        if(warning.length() > 0) {
            UIUtil.showDetailedTaskDialog(BaseStation2App.getApplication().getMainFrame(),
                    "Pin Location System Warning",
                    "There were warnings while reading the pin location system configuration:",
                    warning.toString(),
                    "Warning", TaskDialog.StandardIcon.WARNING, true);
        }
    }

    /*
     * Implement MouseListener interface
     */
    private boolean settingPin = false;

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        settingPin = setPinLocationPoint(e);
    }

    public void mouseReleased(MouseEvent e) {
	if(settingPin) {
            settingPin = false;
	    setPinLocationPoint(e);
            clearPinMeasurements();
            dashboard.setPanEnabled(true);
            pickLocationButton.setSelected(false);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
	if(settingPin) {
            showPinMeasurements(e);
        }
    }

    public void mouseMoved(MouseEvent e) {
	showPinMeasurements(e);
    }

    private void clearPinMeasurements() {
        distanceRenderer.setDrawableObjects(null);
    }

    private void showPinMeasurements(MouseEvent e) {
        if(pickLocationButton.isSelected() && e.getSource() == dashboard && pinLocationTable.getSelectedRowCount() == 1 ) {
            int row = pinLocationTable.getSelectedRow();
            Object o = pinLocationTable.getValueAt(row, 0);

            if(o instanceof DrawablePinLocation) {
                DrawablePinLocation pinLocation = (DrawablePinLocation) o;

                if(dashboard != null && dashboard.getMap() != null && dashboard.getImageRectangle() != null) {

                    if(pinLocation.getParentObject() instanceof HoleDefinition) {
                        HoleDefinition hole = (HoleDefinition) pinLocation.getParentObject();

                        if(hole.green != null) {
                            Font font = getFont().deriveFont(fontSize);

                            DistanceUnits units = golfClub == null? DistanceUnits.Yards:
                                golfClub.getUnits() == DistanceUnits.Meters? DistanceUnits.Meters :
                                    DistanceUnits.Yards;

                            List<Point2D> points = dashboard.toMapPoints(hole.green.getShapeCoordinates());
                            List<Point2D> greenShape = dashboard.toViewPoints(points);
                            List distances = PinLocationUtil.createPinMeasurements(
                                    e.getPoint(), hole, greenShape, dashboard,
                                    font, units);

                            distanceRenderer.setDrawableObjects(distances);
                            dashboard.repaint();
                        }
                    }
                }
            }

        }
    }

    private boolean setPinLocationPoint(MouseEvent e) {
        if(pickLocationButton.isSelected() && e.getSource() == dashboard
                && pinLocationTable.getSelectedRowCount() == 1 ) {
            int row = pinLocationTable.getSelectedRow();
            Object o = pinLocationTable.getValueAt(row, 0);

            if(o instanceof DrawablePinLocation) {

                DrawablePinLocation pinLocation = (DrawablePinLocation) o;

                if(dashboard != null && dashboard.getMap() != null && dashboard.getImageRectangle() != null) {
                    Point mousePoint = e.getPoint();

                    if(pinLocation.getParentObject() instanceof HoleDefinition) {
                        HoleDefinition hole = (HoleDefinition) pinLocation.getParentObject();

                        Shape lastGreen = hole.green == null? null : hole.green.lastDrawnShape;

                        if(lastGreen == null || lastGreen.contains(mousePoint)) {
                            //If the green is valid, only look inside. Otherwise anywhere is ok.
                            Point2D mapPoint = dashboard.convertViewPointToMap(mousePoint);
                            Coordinates coord = dashboard.convertMapPointToCoordinates(mapPoint);

                            pinLocation.coordinates = coord;
                            dashboard.setPanEnabled(false);
                            setPinLocationChanged(true);
                            setPinLocationRendererObjects();
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    private void showPinLocationButtonOnly(boolean b) {
        pinLocationScrollPane.setVisible(!b);
        gridSystemPanel.setVisible(!b);
        savePinLocationsButton.setVisible(!b);
        cancelButton.setVisible(!b);
        pickLocationButton.setVisible(true);
    }

    public void reloadGolfClubImage() {
        Application.getInstance().getContext().getActionMap(this)
                .get("loadGolfClubImage").actionPerformed(
                new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, "loadGolfClubImage"));
    }

    @Action(block = Task.BlockingScope.COMPONENT)
    public Task loadGolfClubImage(ActionEvent event) {
	File imageFile = null;
	BasicMapImage selectedMap = null;

        if(selectedHole != null && selectedHole.map != null
		&& selectedHole.map.getImageAddress() != null) {
	    imageFile = new File(selectedHole.map.getImageAddress());
	    selectedMap = selectedHole.map;
	}
	else if(golfClub != null && golfClub.getCourseImage() != null
		&& golfClub.getCourseImage().getImageAddress() != null) {
	    imageFile = new File(golfClub.getCourseImage().getImageAddress());
	    selectedMap = golfClub.getCourseImage();
	    //dashboard.setMinScaleFillsViewport(true);
	}

	if(imageFile != null && dashboard != null &&  dashboard.getMap() != null
		&& dashboard.getMap().equals(selectedMap) == false) {
            ImageRetrieverTask.ImageRetrievedCallback imgCallback = new CourseImageRetrievedCallback(selectedMap);

            if(imageFile.canRead())
                return new ImageRetrieverTask(Application.getInstance(BaseStation2App.class), imageFile, imgCallback, true);
	    else
		dashboard.setImage(null);
	}

        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pinLocationScrollPane = new javax.swing.JScrollPane();
        pinLocationTable = new javax.swing.JTable();
        pickLocationButton = new javax.swing.JToggleButton();
        clearPinLocationsButton = new javax.swing.JButton();
        gridSystemPanel = new org.jdesktop.swingx.JXPanel();
        gridSystemSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        gridNumberComboBox = new javax.swing.JComboBox();
        gridNumberLabel = new javax.swing.JLabel();
        savePinLocationsButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setName("Form"); // NOI18N

        pinLocationScrollPane.setName("pinLocationScrollPane"); // NOI18N
        pinLocationScrollPane.setPreferredSize(new java.awt.Dimension(150, 150));

        pinLocationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Pin", "Updated"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        pinLocationTable.setName("pinLocationTable"); // NOI18N
        pinLocationTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        pinLocationScrollPane.setViewportView(pinLocationTable);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(PinLocationPanel.class, this);
        pickLocationButton.setAction(actionMap.get("pickPinLocation")); // NOI18N
        pickLocationButton.setName("pickLocationButton"); // NOI18N
        pickLocationButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                pickLocationButtonItemStateChanged(evt);
            }
        });

        clearPinLocationsButton.setAction(actionMap.get("clearPinLocation")); // NOI18N
        clearPinLocationsButton.setName("clearPinLocationsButton"); // NOI18N

        gridSystemPanel.setName("gridSystemPanel"); // NOI18N

        gridSystemSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(PinLocationPanel.class);
        gridSystemSeparator.setTitle(resourceMap.getString("gridSystemSeparator.title")); // NOI18N
        gridSystemSeparator.setName("gridSystemSeparator"); // NOI18N

        gridNumberComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "8" }));
        gridNumberComboBox.setName("gridNumberComboBox"); // NOI18N

        gridNumberLabel.setText(resourceMap.getString("gridNumberLabel.text")); // NOI18N
        gridNumberLabel.setName("gridNumberLabel"); // NOI18N

        javax.swing.GroupLayout gridSystemPanelLayout = new javax.swing.GroupLayout(gridSystemPanel);
        gridSystemPanel.setLayout(gridSystemPanelLayout);
        gridSystemPanelLayout.setHorizontalGroup(
            gridSystemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gridSystemSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
            .addGroup(gridSystemPanelLayout.createSequentialGroup()
                .addComponent(gridNumberLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridNumberComboBox, 0, 102, Short.MAX_VALUE))
        );
        gridSystemPanelLayout.setVerticalGroup(
            gridSystemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gridSystemPanelLayout.createSequentialGroup()
                .addComponent(gridSystemSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gridSystemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gridNumberLabel)
                    .addComponent(gridNumberComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        savePinLocationsButton.setAction(actionMap.get("savePinLocation")); // NOI18N
        savePinLocationsButton.setName("savePinLocationsButton"); // NOI18N

        cancelButton.setAction(actionMap.get("cancelAction")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pinLocationScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
            .addComponent(pickLocationButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
            .addComponent(gridSystemPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(clearPinLocationsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
            .addComponent(savePinLocationsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
            .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pinLocationScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pickLocationButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearPinLocationsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridSystemPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(savePinLocationsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pickLocationButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_pickLocationButtonItemStateChanged
        if(dashboard != null) {
            Dimension beforeShrink = getSize();

            dashboard.setCursor(pickLocationButton.isSelected()?
                Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) : Cursor.getDefaultCursor());

            showPinLocationButtonOnly(pickLocationButton.isSelected());
            if(pickLocationButton.isSelected()) {
		if(pinLocationTable.getSelectedRow() >= 0) {
		    Object o = pinLocationTable.getValueAt(pinLocationTable.getSelectedRow(), 0);

		    if(o instanceof DrawablePinLocation) {
			DrawablePinLocation pinLocation = (DrawablePinLocation) o;

			GolfCourseObject parentObject = pinLocation.getParentObject();
			if(parentObject instanceof HoleDefinition) {
			    selectedHole = (HoleDefinition) parentObject;
			    pinLocationRenderer.setObjectFilter(selectedHole.course, selectedHole);
			    reloadGolfClubImage();
			}
		    }
		}
	    }
	    else {
                distanceRenderer.setDrawableObjects(null);

		if(selectedHole != null) {
		    selectedHole = null;
		    pinLocationRenderer.setObjectFilter(null, null);
		    reloadGolfClubImage();
		}
	    }

            if(shrinkWindow != null) {
                if(pickLocationButton.isSelected()) {
                    shrinkWindowSize = shrinkWindow.getSize();
                    shrinkWindow.setSize(shrinkWindowSize.width,
                            shrinkWindowSize.height - beforeShrink.height + getPreferredSize().height);
                }
                else if(shrinkWindowSize != null) {
                    shrinkWindow.setSize(shrinkWindowSize);
                    shrinkWindowSize = null;
                }
            }
        }

    }//GEN-LAST:event_pickLocationButtonItemStateChanged

    @Action(block=Task.BlockingScope.ACTION, enabledProperty = "pinLocationChanged")
    public Task savePinLocation() {
        return new SavePinLocationTask(org.jdesktop.application.Application.getInstance());
    }

    private class SavePinLocationTask extends org.jdesktop.application.Task<Object, Void> {
        Integer gridNumber;
        SavePinLocationTask(org.jdesktop.application.Application app) {
            super(app);
            gridNumber = (Integer)gridNumberComboBox.getSelectedItem();
        }

        @Override protected Object doInBackground() {
            CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();
            if(gridNumber != null) {
                if(gridNumber < 1 || gridNumber > courseSettingsManager.getGridSystemNumberOfGrids()) {
                    throw new IllegalArgumentException("The selected grid number is invalid: " + gridNumber);
                }
                courseSettingsManager.setGridSystemCurrentGrid(gridNumber);
            }

            List<PinLocation> pins = new ArrayList<>(modifiedPinLocations.size());
            Date date = new Date();
            for(DrawablePinLocation pin: modifiedPinLocations) {
                HoleDefinition h = (HoleDefinition) pin.getParentObject();
                int course = h.course.getCourseNumber();
                String coordinates = pin.coordinates == null? null : pin.coordinates.toString();
                pins.add(new PinLocation(course, h.number, coordinates, date));
            }
            courseService.savePinLocations(pins);
            BaseStation2App.getApplication().syncGolfClub();
            return null;
        }

        @Override protected void succeeded(Object result) {
            cleanup(false);
            callback.dialogDone();
        }

        @Override
        protected void failed(Throwable t) {
//            TaskDialogs.showException(t);
            NotificationPopup.showErrorPopup("Pins", NotificationPopup.ERROR_TITLE);
        }
    }

    @Action
    public void cancelAction() {
	cleanup(true);
	if(callback != null)
	    callback.dialogDone();
    }

    @Action(enabledProperty = "pinLocationSelected")
    public void pickPinLocation() {
    }

    @Action(enabledProperty = "pinLocationsSelected")
    public void clearPinLocation() {
	if(pinLocationTable.getSelectedRowCount() > 0) {
	    int[] selectedRows = pinLocationTable.getSelectedRows();

	    for(int row: selectedRows) {
		Object o = pinLocationTable.getValueAt(row, 0);

		if(o instanceof DrawablePinLocation) {
		    DrawablePinLocation pinLocation = (DrawablePinLocation) o;
		    pinLocation.coordinates = null;

                    setPinLocationChanged(true);
                    setPinLocationRendererObjects();
                }
            }
	}
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearPinLocationsButton;
    private javax.swing.JComboBox gridNumberComboBox;
    private javax.swing.JLabel gridNumberLabel;
    private org.jdesktop.swingx.JXPanel gridSystemPanel;
    private org.jdesktop.swingx.JXTitledSeparator gridSystemSeparator;
    private javax.swing.JToggleButton pickLocationButton;
    private javax.swing.JScrollPane pinLocationScrollPane;
    private javax.swing.JTable pinLocationTable;
    private javax.swing.JButton savePinLocationsButton;
    // End of variables declaration//GEN-END:variables

    public interface DialogCallback {
        public void dialogDone();
    }

    private class CourseImageRetrievedCallback implements ImageRetrievedCallback {
	private BasicMapImage map;

	public CourseImageRetrievedCallback(BasicMapImage map) {
	    this.map = map;
	}

	public void imageRetrieved(BufferedImage image) {
	    if(dashboard != null) {
                DrawableMapImage m = CourseRenderUtil.createGreenViewReferenceMapImage(selectedHole, dashboard.getHeight());

                if (m != null) {
                    DrawableMapImage holeMap = new DrawableMapImage(map);
                    holeMap.setCachedImage(MapUtils.getNormalizedMapImage(map, image));
                    mapImageRenderer.setDrawableObjects(Arrays.asList(new DrawableObject[] {holeMap}));

                    dashboard.setMinScaleFillsViewport(false);
                    dashboard.setMap(m);
                    dashboard.setImage(m.getCachedImage());
                }
                else {
                    BaseStation2App.getApplication().loadGolfClub();
                    cleanup(false);
                    dashboard.setMinScaleFillsViewport(true);
                    dashboard.setMap(map);
                    dashboard.setImage(image);
                }
	    }
	}

	public void retrieveImageFailed(Exception cause) {
	    TaskDialogs.showException(cause);

	    if(dashboard != null)
		dashboard.setImage(null);
	}

	public void retrieveImageCanceled() {
	}

    }

    private class PinlocationTableCellRenderer extends DefaultTableCellRenderer {
        @Override
	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int column) {

	    if(value instanceof DrawablePinLocation) {
		StringBuilder str = new StringBuilder();
		DrawablePinLocation pinLocation = (DrawablePinLocation) value;

		if(pinLocation.getParentObject() instanceof HoleDefinition) {
		    HoleDefinition hole = (HoleDefinition) pinLocation.getParentObject();
		    if(hole.course != null)
			str.append(hole.course.getName()).append(" ");
		    str.append(hole.number);
		}
                else {
		    str.append("Hole ").append(pinLocation.getId());
                }

		return super.getTableCellRendererComponent(table, str.toString(), isSelected, hasFocus, row, column);
	    }
	    else if(value instanceof Date) {
		return super.getTableCellRendererComponent(table,
			Formatters.shortDateTimeFormat.format((Date) value),
			isSelected, hasFocus, row, column);
            }
            else {
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
	}
    }

}
