/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DrawingTool.java
 *
 * Created on 15/03/2011, 03:07:18 AM
 */

package com.stayprime.basestation2.ui.modules;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.basestation2.renderers.DashboardCartTrackRenderer;
import com.stayprime.util.image.ImageRetrieverTask;
import com.stayprime.util.image.ImageRetrieverTask.ImageRetrievedCallback;
import com.aeben.golfclub.view.Dashboard;
import com.stayprime.legacy.Pair;
import com.stayprime.ui.PersistentCheckBox;
import com.stayprime.legacy.screen.Screen;
import com.stayprime.legacy.screen.ScreenParent;
import com.aeben.elementos.mapview.BasicDrawablePointObject;
import com.aeben.elementos.mapview.BasicDrawableShapeObject;
import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.DrawableCoursePoint;
import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.GolfCourseObject.ObjectType;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.HoleDefinition;
import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.elementos.mapview.DrawableShapeObject;
import com.aeben.elementos.mapview.MapProjection;
import com.aeben.elementos.mapview.ObjectRenderer;
import com.aeben.golfcourse.util.DistanceUnits;
import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.Constant;
import com.stayprime.basestation2.renderers.DashboardObjectRenderer;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import com.stayprime.geo.MapUtils;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class DrawingTool extends JPanel
        implements Screen, ObjectRenderer, Scrollable {
    private static final Logger log = LoggerFactory.getLogger(DrawingTool.class);
    private float fontIncrease = -2f;

    public static enum Operation {ADD, EDIT, DELETE, EXIT, SELECT_COURSE, SELECT_HOLE};

    private ScreenParent screenParent;
    private GolfClub golfClub;
    private Operation operation;
    private Pair<Operation, GolfCourseObject> pendingOperation;
    private List<DrawableObject> trackPoints;

    //Object selector properties
    protected CourseDefinition selectedCourse;
    public static final String PROP_SELECTEDCOURSE = "selectedCourse";
    protected HoleDefinition selectedHole;
    public static final String PROP_SELECTEDHOLE = "selectedHole";
    protected boolean deletableObjectSelected = false;
    public static final String PROP_DELETABLEOBJECTSELECTED = "deletableObjectSelected";
    protected boolean validObjectParentSelected = false;
    public static final String PROP_VALIDOBJECTPARENTSELECTED = "validObjectParentSelected";

    //Custom object selector
    protected CustomObjectParent customObjectParent;
    public static final String PROP_CUSTOMOBJECTPARENT = "customObjectParent";

    private final PropertyChangeListener modifiedListener;

    //Keep a local copy of the objectInformationChanged property for Save action's enabledProperty
    protected boolean objectInformationChanged;
    public static final String PROP_OBJECTINFORMATIONCHANGED = "objectInformationChanged";

    private boolean changesSaved;

    CourseService courseService;

    /** Creates new form DrawingTool */
    public DrawingTool() {
        initComponents();
        objectsList.setModel(new DefaultListModel());
	toolbar.add(Box.createHorizontalStrut(8), 1);
	toolbar.add(Box.createHorizontalStrut(8), 3);
	toolbar.add(Box.createHorizontalStrut(16), 5);
        toolbar.add(Box.createGlue());

	initGolfClubObjectListSelector();

        objectsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(objectsList.getSelectedValue() instanceof GolfCourseObject) {
                    GolfCourseObject object = (GolfCourseObject) objectsList.getSelectedValue();
                    startEditingObject(object);

		    if(getCustomObjectParent() == null)
			setDeletableObjectSelected(
				(object != selectedHole.cartPath && object.getType() != ObjectType.CARTH_PATH) ||
				(object != selectedHole.green && object.getType() != ObjectType.GREEN) ||
				(object != selectedHole.getApproachLine() && object.getType() != ObjectType.APPROACH_LINE));
		    else
			setDeletableObjectSelected(getCustomObjectParent().isObjectDeletable(object));
                }
                else {
                    if(operation == Operation.EDIT)
                        stopEditingObject(null);

                    setDeletableObjectSelected(false);
                }
            }
        });

	modifiedListener = new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent evt) {
		setObjectInformationChanged(editorPanel.isObjectInformationChanged());
	    }
	};

	editorPanel.addPropertyChangeListener(
		DrawingToolEditorPanel.PROP_OBJECTINFORMATIONCHANGED,
		modifiedListener);
	trackPoints = new ArrayList<DrawableObject>();
    }

    public void init() {
        loadConfig();
    }

    private void loadConfig() {
        PropertiesConfiguration config = BaseStation2App.getApplication().getConfig();
        if (config != null && config.getBoolean(Constant.showDrawingToolUtils)) {
            toolbar.add(loadPointsButton, 0);
            toolbar.add(clearPointsButton, 1);
        }
    }

    /*
     * Utility object setters
     */

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void setDashboardObjectRenderer(DashboardObjectRenderer objectRenderer) {
        editorPanel.setDashboardObjectRenderer(objectRenderer);
    }

    public void setDashboard(Dashboard dashboard) {
	editorPanel.setDashboard(dashboard);
    }

    public CustomObjectParent getCustomObjectParent() {
	return customObjectParent;
    }

    public void setCustomObjectParent(CustomObjectParent customObjectParent) {
	CustomObjectParent oldCustomObjectParent = this.customObjectParent;
	this.customObjectParent = customObjectParent;
	firePropertyChange(PROP_CUSTOMOBJECTPARENT, oldCustomObjectParent, customObjectParent);

	setSelectedHole(null);
	setSelectedCourse(null);

	if(oldCustomObjectParent != null && oldCustomObjectParent.getComponent() != null) {
	    ((CardLayout)objectParentPanel.getLayout()).first(objectParentPanel);
	    objectParentPanel.remove(oldCustomObjectParent.getComponent());
	}

	if(customObjectParent != null) {
	    if(customObjectParent.getComponent() != null) {
		objectParentPanel.add(customObjectParent.getComponent(), "customObjectParent");
		((CardLayout)objectParentPanel.getLayout()).last(objectParentPanel);
	    }

	    setObjectInformationChanged(false);
	    stopEditingObject(pendingOperation);
	    fillObjectsList(customObjectParent.getObjectsList());
	    setValidObjectParentSelected(customObjectParent.canCreateObjects());

	    setObjectRendererObjects();
	}

    }

    public void setGolfClub(GolfClub golfClub) {
        this.golfClub = golfClub;

	setCourseList(golfClub != null? golfClub.getCourses() : null);

        setObjectRendererObjects();
    }

    private void fillObjectsList(List objects) {
        DefaultListModel model = (DefaultListModel) objectsList.getModel();
        model.removeAllElements();

        if(objects != null) {
            for(Object object: objects) {
                model.addElement(object);
            }
	}
    }

    private void setObjectRendererObjects() {
        List<DrawableObject> shapes = new ArrayList<DrawableObject>();
        List<DrawableObject> points = new ArrayList<DrawableObject>();

	if(golfClub != null && (getCustomObjectParent() == null || getCustomObjectParent().useDrawingToolObjectFilters())) {
	    for(GolfCourseObject object: golfClub.getCourseObjects()) {
		if(object instanceof DrawableShapeObject)
		    shapes.add((DrawableShapeObject) object);
		else if(object instanceof DrawableObject)
		    points.add((DrawableObject) object);
	    }

	    for(CourseDefinition course: golfClub.getCourses()) {
		for(int n = 1; n <= course.getHoleCount(); n++) {
		    HoleDefinition hole = course.getHoleNumber(n);

		    if(hole != null) {
			for(DrawableCourseShape object: hole.getShapes()) {
                            shapes.add(object);
			}
			for(DrawableCoursePoint object: hole.getPoints()) {
                            points.add(object);
			}

			if(hole.cartPath instanceof DrawableObject)
			    shapes.add((DrawableObject) hole.cartPath);

			if(hole.green instanceof DrawableObject)
			    shapes.add((DrawableObject) hole.green);

                        if(hole.getHoleOutline() instanceof DrawableObject)
			    shapes.add((DrawableObject) hole.getHoleOutline());

			if(hole.getApproachLine() instanceof DrawableObject)
			    shapes.add((DrawableObject) hole.getApproachLine());

		    }
		}
	    }
	}

	if(getCustomObjectParent() != null) {
	    List<DrawableObject> objects = getCustomObjectParent().getObjectRendererObjects();
	    if(objects != null)
		shapes.addAll(objects);
	}

        shapes.addAll(points);

        if (editorPanel.getObjectRenderer() != null) {
            editorPanel.getObjectRenderer().setDrawableObjects(shapes);
        }
    }

    public boolean isDeletableObjectSelected() {
        return deletableObjectSelected;
    }

    protected void setDeletableObjectSelected(boolean deletableObjectSelected) {
        boolean oldDeletableObjectSelected = this.deletableObjectSelected;
        this.deletableObjectSelected = deletableObjectSelected;
        firePropertyChange(PROP_DELETABLEOBJECTSELECTED, oldDeletableObjectSelected, deletableObjectSelected);
    }

    public boolean isObjectInformationChanged() {
	return objectInformationChanged;
    }

    public void setObjectInformationChanged(boolean objectInformationChanged) {
	boolean oldObjectInformationChanged = this.objectInformationChanged;
	this.objectInformationChanged = objectInformationChanged;
	firePropertyChange(PROP_OBJECTINFORMATIONCHANGED, oldObjectInformationChanged, objectInformationChanged);
    }

    private void resetObjectFilter() {
        int selectedIndex = showObjectsComboBox.getSelectedIndex();
	DrawingToolObjectRenderer objectRenderer = editorPanel.getObjectRenderer();

        if(objectRenderer != null) {
            if(selectedIndex == 0)
                objectRenderer.setObjectFilter(null, null);
            if(selectedIndex == 1)
                objectRenderer.setObjectFilter(getSelectedCourse(), null);
            if(selectedIndex == 2)
                objectRenderer.setObjectFilter(getSelectedCourse(), selectedHole);
            if(selectedIndex == 3)
                objectRenderer.setObjectFilter(new CourseDefinition(null, 0, null, 0), new HoleDefinition(null, 0));
        }

        if(editorPanel.getDashboard() != null)
            editorPanel.getDashboard().repaint();
    }

    private boolean stopEditingObject(Pair<Operation, GolfCourseObject> operation) {
        this.pendingOperation = null;
        //if (editorPanel.isObjectInformationChanged()) {
        if (isObjectInformationChanged()) {
            int result = JOptionPane.showConfirmDialog(this, "Do you want to save the changes \nto this object definition?");
            if (result == JOptionPane.CANCEL_OPTION)
                return false;
            else if (result == JOptionPane.YES_OPTION) {
                this.pendingOperation = operation;
                Application.getInstance().getContext().getActionMap(this).get("saveAction")
                        .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "saveAction"));
                return false;
            }
            else {
                operation = null;
                restoreEditingObject();
                setObject(null);
                editorPanel.setObjectInformationChanged(false);
                return true;
            }
        }
        else {
            operation = null;
            setObject(null);
            editorPanel.setObjectInformationChanged(false); //setObject sets objectInfoChanged
            return true;
        }
    }

    private void restoreEditingObject() {
        GolfCourseObject selected = editorPanel.getSelectedObject();
        if (selected != null) {
            reloadGolfClub();
        }
    }

    private void startEditingObject(GolfCourseObject object) {
        startEditingObject(object, true);
    }

    private void startEditingObject(GolfCourseObject object, boolean autoCenter) {
        if(object != editorPanel.getSelectedObject()) {
            if(stopEditingObject(new Pair(Operation.EDIT, object))) {
                if(object == null)
                    log.warn("Trying to start editing null object.");
                setObject(object);
                editorPanel.setObjectInformationChanged(false);

                if(autoCenterCheckBox.isSelected() && autoCenter) //Avoid zooming into object when it was just added
                    centerEditingObject();

                operation = Operation.EDIT;
            }
            else {
                if(((DefaultListModel)objectsList.getModel()).contains(editorPanel.getSelectedObject()))
                    objectsList.setSelectedValue(editorPanel.getSelectedObject(), true);
                else
                    objectsList.clearSelection();
            }
        }
    }

    private void setObject(GolfCourseObject originalObject) {
        if(originalObject != null) {
            editorPanel.setObject(originalObject,
		    getCustomObjectParent() == null? null : getCustomObjectParent().getValidObjectTypes(originalObject));
	}
        else {
            editorPanel.setObject(null, null);
	}

        savePanel.setVisible(originalObject instanceof GolfCourseObject);
    }

    private int getNextObjectId(HoleDefinition selectedHole) {
        int nextId = 10;
        for(GolfCourseObject object: selectedHole.getPoints()) {
            if(object.getId() != null && object.getId() >= nextId)
                nextId = object.getId() + 1;
        }
        for(GolfCourseObject object: selectedHole.getShapes()) {
            if(object.getId() != null && object.getId() >= nextId)
                nextId = object.getId() + 1;
        }
        return nextId;
    }

    public void reloadMapImage() {
        Application.getInstance().getContext().getActionMap(this)
                .get("loadGolfClubImage").actionPerformed(
                new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, "loadGolfClubImage"));
    }

    public void reloadGolfClub() {
        setGolfClub(BaseStation2App.getApplication().getGolfClub());

        CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();
        editorPanel.setGridSystemNumberOfGrids(courseSettingsManager.getGridSystemNumberOfGrids());
    }

    /*
     * Stuff specific to the Course/Hole object list selector
     */

    private void initGolfClubObjectListSelector() {
        courseComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                CourseDefinition course = null;
                if(e.getStateChange() == ItemEvent.SELECTED && e.getItem() instanceof CourseDefinition) {
                    course = (CourseDefinition) e.getItem();

                    //Only react to an item selection if it's different than the
                    //previously selected (and user confirmed if save is pending) course.
                    if(course != getSelectedCourse()) {
                        //If there's an object being edited, ask the user for confirmation
                        //and only then pass down the event to fill the hole combo.
                        //If the confirmation fails, or is pending, select the previously
                        //selected course.
                        if(stopEditingObject(new Pair(Operation.SELECT_COURSE, course))) {
                            setSelectedCourse(course);
                            fillHoleNumberCombo(course);
                        }
                        else {
                            courseComboBox.setSelectedItem(getSelectedCourse());
                        }
                    }
                }
            }
        });

        holeComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
		CourseDefinition course = getSelectedCourse();
                HoleDefinition hole = null;

                if(e.getStateChange() == ItemEvent.SELECTED && course != null
                        && e.getItem() instanceof Integer) {
                    hole = course.getHoleNumber((Integer)e.getItem());

                    setValidObjectParentSelected(hole != null);

                    log.trace("holeComboBox itemStateChanged. Item: " + e.getItem() + ". "
                            + "State change: " + (e.getStateChange() == ItemEvent.SELECTED? "selected. " : "deselected. ")
                            + "Hole: " + hole + ". " + ". Selected hole: " + hole + ". ");

                    if(hole != getSelectedHole()) {
                        if(stopEditingObject(new Pair(Operation.SELECT_HOLE, hole))) {
                            setSelectedHole(hole);
                            fillObjectsList(hole);
                            resetObjectFilter();
                        }
                        else {
                            holeComboBox.setSelectedItem(selectedHole.number);
                        }
                    }

		    reloadMapImage();
                }
                else
                    setValidObjectParentSelected(false);
            }
        });
    }

    public CourseDefinition getSelectedCourse() {
	return selectedCourse;
    }

    public void setSelectedCourse(CourseDefinition selectedCourse) {
	CourseDefinition oldSelectedCourse = this.selectedCourse;
	this.selectedCourse = selectedCourse;
	firePropertyChange(PROP_SELECTEDCOURSE, oldSelectedCourse, selectedCourse);
    }

    public HoleDefinition getSelectedHole() {
	return selectedHole;
    }

    public boolean isValidObjectParentSelected() {
        return validObjectParentSelected;
    }

    protected void setValidObjectParentSelected(boolean validObjectParentSelected) {
        boolean oldValidObjectParentSelected = this.validObjectParentSelected;
        this.validObjectParentSelected = validObjectParentSelected;
        firePropertyChange(PROP_VALIDOBJECTPARENTSELECTED, oldValidObjectParentSelected, validObjectParentSelected);
    }

    public void setSelectedHole(HoleDefinition selectedHole) {
	HoleDefinition oldSelectedHole = this.selectedHole;
	this.selectedHole = selectedHole;
	firePropertyChange(PROP_SELECTEDHOLE, oldSelectedHole, selectedHole);
    }

    private void setCourseList(List<CourseDefinition> list) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) courseComboBox.getModel();
        while(model.getSize() > 0)
            model.removeElementAt(0);

	if(list != null) {
	    for(CourseDefinition course: list) {
		model.addElement(course);
	    }
	}
    }

    private void fillHoleNumberCombo(CourseDefinition course) {
        DefaultComboBoxModel holeComboModel = (DefaultComboBoxModel) holeComboBox.getModel();
        //Object selected = holeComboModel.getSelectedItem();
        holeComboModel.removeAllElements();

        if(course != null) {
            for(int n = 1; n <= course.getHoleCount(); n++) {
                if(course.getHoleNumber(n) != null)
                    holeComboModel.addElement(n);
            }
        }
    }

    private void fillObjectsList(HoleDefinition hole) {
        if(hole != null) {
	    List<GolfCourseObject> objects = new ArrayList<GolfCourseObject>();

            if (hole.cartPath == null) {
                hole.cartPath = new DrawableCourseShape("Cart Path #" + hole.number, 0, hole,
                        ObjectType.CARTH_PATH, new ArrayList<Coordinates>(), false, 2, Color.black, Color.black);
            }
            if (hole.green == null) {
                hole.green = new DrawableGreen("Green #" + hole.number, 1, hole,
                        new ArrayList<Coordinates>(), true, 2f,
                        new Color(0x8800FF00, true), new Color(0x8800FF00, true));
            }
            if (hole.getApproachLine() == null) {
                hole.setApproachLine(new DrawableCourseShape("Approach line #" + hole.number,
                        7, hole, ObjectType.APPROACH_LINE, new ArrayList<Coordinates>(),
                        false, 2f, Color.white, null));
            }
            if (hole.getHoleOutline() == null) {
                hole.setHoleOutline(new DrawableCourseShape("Outline #" + hole.number,
                        8, hole, ObjectType.HOLE_OUTLINE, new ArrayList<Coordinates>(),
                        false, 2f, Color.white, null));
            }

            objects.add(hole.cartPath);
            objects.add(hole.green);
            objects.add(hole.getApproachLine());
            objects.add(hole.getHoleOutline());

            for(GolfCourseObject object: hole.getPoints()) {
                objects.add(object);
            }

            for(GolfCourseObject object: hole.getShapes()) {
                objects.add(object);
            }

	    fillObjectsList(objects);
        }
	else {
	    fillObjectsList((List)null);
	}
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        toolbar = new org.jdesktop.swingx.JXPanel();
        rulerButton = new javax.swing.JToggleButton();
        glue1 = new com.stayprime.ui.Glue();
        showObjectsLabel = new javax.swing.JLabel();
        imageComboBox = new javax.swing.JComboBox() {
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        showObjectsComboBox = new javax.swing.JComboBox() {
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        fileChooser = new javax.swing.JFileChooser();
        loadPointsButton = new javax.swing.JButton();
        clearPointsButton = new javax.swing.JButton();
        objectParentPanel = new org.jdesktop.swingx.JXPanel();
        courseObjectListSelector = new org.jdesktop.swingx.JXPanel();
        courseComboBox = new javax.swing.JComboBox();
        holeLabel = new javax.swing.JLabel();
        holeComboBox = new javax.swing.JComboBox();
        courseLabel = new javax.swing.JLabel();
        objectsSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        objectsScrollPane = new javax.swing.JScrollPane();
        objectsList = new javax.swing.JList();
        objectControlPanel = new org.jdesktop.swingx.JXPanel();
        centerObjectButton = new javax.swing.JButton();
        autoCenterCheckBox = new PersistentCheckBox();
        addObjectButton = new javax.swing.JButton();
        deleteObjectButton = new javax.swing.JButton();
        editorPanel = new com.stayprime.basestation2.ui.modules.DrawingToolEditorPanel();
        saveSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        savePanel = new org.jdesktop.swingx.JXPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        toolbar.setName("toolbar"); // NOI18N
        toolbar.setLayout(new javax.swing.BoxLayout(toolbar, javax.swing.BoxLayout.LINE_AXIS));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(DrawingTool.class);
        rulerButton.setText(resourceMap.getString("rulerButton.text")); // NOI18N
        rulerButton.setName("rulerButton"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editorPanel, org.jdesktop.beansbinding.ELProperty.create("${mouseListener.measurementMode}"), rulerButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        rulerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rulerButtonMouseClicked(evt);
            }
        });
        toolbar.add(rulerButton);

        glue1.setMaximumSize(new java.awt.Dimension(10, 10));
        glue1.setName("glue1"); // NOI18N
        glue1.setPreferredSize(new java.awt.Dimension(10, 10));
        toolbar.add(glue1);

        showObjectsLabel.setText(resourceMap.getString("showObjectsLabel.text")); // NOI18N
        showObjectsLabel.setName("showObjectsLabel"); // NOI18N
        toolbar.add(showObjectsLabel);

        imageComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Course Image", "Hole Image" }));
        imageComboBox.setName("imageComboBox"); // NOI18N
        imageComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                imageComboBoxItemStateChanged(evt);
            }
        });
        toolbar.add(imageComboBox);

        showObjectsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All objects", "Course objects", "Hole objects", "Editing object" }));
        showObjectsComboBox.setName("showObjectsComboBox"); // NOI18N
        showObjectsComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                showObjectsComboBoxItemStateChanged(evt);
            }
        });
        toolbar.add(showObjectsComboBox);

        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setName("fileChooser"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(DrawingTool.class, this);
        loadPointsButton.setAction(actionMap.get("loadPoints")); // NOI18N
        loadPointsButton.setName("loadPointsButton"); // NOI18N

        clearPointsButton.setAction(actionMap.get("clearPoints")); // NOI18N
        clearPointsButton.setName("clearPointsButton"); // NOI18N

        setName("Form"); // NOI18N
        com.stayprime.ui.VerticalLayout2 verticalLayout21 = new com.stayprime.ui.VerticalLayout2();
        verticalLayout21.setAlignment(3);
        setLayout(verticalLayout21);

        objectParentPanel.setName("objectParentPanel"); // NOI18N
        objectParentPanel.setLayout(new java.awt.CardLayout());

        courseObjectListSelector.setName("courseObjectListSelector"); // NOI18N
        courseObjectListSelector.setLayout(new java.awt.GridBagLayout());

        courseComboBox.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        courseComboBox.setName("courseComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        courseObjectListSelector.add(courseComboBox, gridBagConstraints);

        holeLabel.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        holeLabel.setText(resourceMap.getString("holeLabel.text")); // NOI18N
        holeLabel.setName("holeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        courseObjectListSelector.add(holeLabel, gridBagConstraints);

        holeComboBox.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        holeComboBox.setMaximumRowCount(9);
        holeComboBox.setName("holeComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        courseObjectListSelector.add(holeComboBox, gridBagConstraints);

        courseLabel.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        courseLabel.setText(resourceMap.getString("courseLabel.text")); // NOI18N
        courseLabel.setName("courseLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        courseObjectListSelector.add(courseLabel, gridBagConstraints);

        objectParentPanel.add(courseObjectListSelector, "card1");

        add(objectParentPanel);

        objectsSeparator.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        objectsSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        objectsSeparator.setName("objectsSeparator"); // NOI18N
        objectsSeparator.setTitle(resourceMap.getString("objectsSeparator.title")); // NOI18N
        add(objectsSeparator);

        objectsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        objectsScrollPane.setName("objectsScrollPane"); // NOI18N

        objectsList.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        objectsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        objectsList.setName("objectsList"); // NOI18N
        objectsList.setVisibleRowCount(6);
        objectsScrollPane.setViewportView(objectsList);

        add(objectsScrollPane);

        objectControlPanel.setName("objectControlPanel"); // NOI18N
        objectControlPanel.setLayout(new java.awt.GridLayout(0, 2, 2, 2));

        centerObjectButton.setAction(actionMap.get("centerEditingObject")); // NOI18N
        centerObjectButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        centerObjectButton.setIcon(resourceMap.getIcon("centerObjectButton.icon")); // NOI18N
        centerObjectButton.setText(resourceMap.getString("centerObjectButton.text")); // NOI18N
        centerObjectButton.setName("centerObjectButton"); // NOI18N
        objectControlPanel.add(centerObjectButton);

        autoCenterCheckBox.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        autoCenterCheckBox.setSelected(true);
        autoCenterCheckBox.setText(resourceMap.getString("autoCenterCheckBox.text")); // NOI18N
        autoCenterCheckBox.setName("autoCenterCheckBox"); // NOI18N
        objectControlPanel.add(autoCenterCheckBox);

        addObjectButton.setAction(actionMap.get("addObject")); // NOI18N
        addObjectButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        addObjectButton.setText(resourceMap.getString("addObjectButton.text")); // NOI18N
        addObjectButton.setName("addObjectButton"); // NOI18N
        objectControlPanel.add(addObjectButton);

        deleteObjectButton.setAction(actionMap.get("deleteObject")); // NOI18N
        deleteObjectButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        deleteObjectButton.setText(resourceMap.getString("deleteObjectButton.text")); // NOI18N
        deleteObjectButton.setName("deleteObjectButton"); // NOI18N
        objectControlPanel.add(deleteObjectButton);

        add(objectControlPanel);

        editorPanel.setName("editorPanel"); // NOI18N
        add(editorPanel);

        saveSeparator.setFont(getFont().deriveFont(getFont().getSize()-5f));
        saveSeparator.setName("saveSeparator"); // NOI18N
        saveSeparator.setTitle(resourceMap.getString("saveSeparator.title")); // NOI18N
        add(saveSeparator);

        savePanel.setName("savePanel"); // NOI18N
        savePanel.setLayout(new java.awt.GridLayout(1, 0));

        saveButton.setAction(actionMap.get("saveAction")); // NOI18N
        saveButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        saveButton.setText(resourceMap.getString("saveButton.text")); // NOI18N
        saveButton.setName("saveButton"); // NOI18N
        savePanel.add(saveButton);

        cancelButton.setAction(actionMap.get("cancelAction")); // NOI18N
        cancelButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        savePanel.add(cancelButton);

        add(savePanel);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void showObjectsComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showObjectsComboBoxItemStateChanged
	resetObjectFilter();
    }//GEN-LAST:event_showObjectsComboBoxItemStateChanged

    private void imageComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_imageComboBoxItemStateChanged
	reloadMapImage();
    }//GEN-LAST:event_imageComboBoxItemStateChanged

    private void rulerButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rulerButtonMouseClicked
	if(SwingUtilities.isRightMouseButton(evt)) {
	    DistanceUnits u = editorPanel.getObjectRenderer().getRulerUnits();
	    DistanceUnits n = u == DistanceUnits.Yards?
		DistanceUnits.Meters : DistanceUnits.Yards;

	    editorPanel.getObjectRenderer().setRulerUnits(n);
	    editorPanel.getDashboard().repaint();
	    setRulerButtonText();
	}
    }//GEN-LAST:event_rulerButtonMouseClicked

    private void setRulerButtonText() {
	if(editorPanel.getObjectRenderer() != null) {
	    DistanceUnits u = editorPanel.getObjectRenderer().getRulerUnits();
	    rulerButton.setText(u.getShortName());
	}
    }

    @Override
    public Dimension getPreferredSize() {
	return super.getPreferredSize();
    }

    @Action(block = Task.BlockingScope.COMPONENT)
    public Task loadGolfClubImage(ActionEvent event) {
	File imageFile = null;
	BasicMapImage selectedMap = null;
        if(imageComboBox.getSelectedIndex() == 1) {
	    if(selectedHole != null && selectedHole.map != null
		    && selectedHole.map.getImageAddress() != null) {
		imageFile = new File(selectedHole.map.getImageAddress());
		selectedMap = selectedHole.map;
	    }
	}
	else {
	    if(golfClub != null && golfClub.getCourseImage() != null
		    && golfClub.getCourseImage().getImageAddress() != null) {
		imageFile = new File(golfClub.getCourseImage().getImageAddress());
		selectedMap = golfClub.getCourseImage();
		//dashboard.setMinScaleFillsViewport(true);
	    }
	}

	Dashboard dashboard = editorPanel.getDashboard();
	if(imageFile != null && dashboard != null &&  dashboard.getMap() != null
		&& dashboard.getMap().equals(selectedMap) == false) {
            ImageRetrieverTask.ImageRetrievedCallback callback = new CourseImageRetrievedCallback(selectedMap, dashboard);

            if(imageFile.canRead())
                return new ImageRetrieverTask(Application.getInstance(BaseStation2App.class), imageFile, callback, true);
	    else
		dashboard.setImage(null);
	}

        return null;
    }

    @Action(enabledProperty = PROP_VALIDOBJECTPARENTSELECTED)
    public void addObject() {
        if(selectedHole != null || getCustomObjectParent() != null) {
            if(stopEditingObject(new Pair(Operation.ADD, null))) {
                operation = Operation.ADD;
                objectsList.clearSelection();

		if(getCustomObjectParent() == null) {
		    DrawableCoursePoint point = new DrawableCoursePoint("", getNextObjectId(selectedHole), selectedHole, ObjectType.HAZARD, null);
		    setObject(point);
		}
		else {
		    setObject(getCustomObjectParent().createNewObject());
		}

                editorPanel.setObjectInformationChanged(true);
            }
            else {
                objectsList.setSelectedValue(editorPanel.getSelectedObject(), true);
            }
        }
    }

    @Action(enabledProperty = PROP_DELETABLEOBJECTSELECTED)
    public void deleteObject() {
        pendingOperation = null;
        int result = JOptionPane.showConfirmDialog(this, "Do you want to delete \nthis object definition?",
                "Question", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            operation = Operation.DELETE;
            editorPanel.setObjectInformationChanged(false);

            Application.getInstance().getContext().getActionMap(this).get("saveAction")
                    .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "saveAction"));
        }
    }

    @Action(block = Task.BlockingScope.APPLICATION, enabledProperty = PROP_OBJECTINFORMATIONCHANGED)
    public Task saveAction() {
        return new SaveActionTask(org.jdesktop.application.Application.getInstance());
    }

    private class SaveActionTask extends org.jdesktop.application.Task<Object, Void> {
        SaveActionTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to SaveActionTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            try {
		GolfCourseObject selectedObject = editorPanel.getSelectedObject();

		if(getCustomObjectParent() == null) {
                    System.out.println(selectedHole);
                    System.out.println(selectedObject);
                    System.out.println(selectedObject == null? "Null object" : selectedObject.getId());
                    changesSaved = true;
		    if(operation == Operation.ADD)
			courseService.createHoleObject(selectedHole, selectedObject);
		    else if(operation == Operation.EDIT)
			courseService.updateHoleObject(selectedHole, selectedObject.getId(), selectedObject);
		    else if(operation == Operation.DELETE)
			courseService.deleteHoleObject(selectedHole, selectedObject.getId());
		}
            }
            catch(Exception ex) {
                return ex;
            }
            return null;
        }
        @Override protected void succeeded(Object result) {
            if(result instanceof Exception) {
//                Exception exception = (Exception) result;
//                setMessage("Error saving object definition: " + result);
//                notifyError(exception);
                NotificationPopup.showErrorPopup("Drawing Tool", NotificationPopup.ERROR_TITLE);
            }
            else {
		GolfCourseObject selectedObject = editorPanel.getSelectedObject();

                if(operation == Operation.ADD) {
		    if(getCustomObjectParent() == null) {
                        if (selectedObject instanceof DrawableCoursePoint) {
                            selectedHole.getPoints().add((DrawableCoursePoint) selectedObject);
                        }
                        else if (selectedObject instanceof DrawableCourseShape) {
                            selectedHole.getShapes().add((DrawableCourseShape) selectedObject);
                        }
                    }
		    else if(selectedObject instanceof DrawableObject) {
			DrawableObject drawableObject = (DrawableObject) selectedObject;
			getCustomObjectParent().getObjectsList().add(drawableObject);
		    }

                    editorPanel.setObjectInformationChanged(false);

                    setObjectRendererObjects();
                    DefaultListModel model = (DefaultListModel) objectsList.getModel();
                    model.addElement(selectedObject);

                    if(pendingOperation == null) {
                        startEditingObject(selectedObject, false);
                        objectsList.setSelectedValue(selectedObject, true);
                    }
                    //setObject(modifiedObject);
                }
                else if(operation == Operation.DELETE) {
		    if(getCustomObjectParent() == null) {
			selectedHole.getPoints().remove(selectedObject);
			selectedHole.getShapes().remove(selectedObject);
                    }
                    else {
			getCustomObjectParent().getObjectsList().remove(selectedObject);
                    }

                    setObjectRendererObjects();
                    DefaultListModel model = (DefaultListModel) objectsList.getModel();
                    model.removeElement(selectedObject);
                    editorPanel.setObjectInformationChanged(false);

                    stopEditingObject(pendingOperation);
                }
                else if(operation == Operation.EDIT) {
		    if(getCustomObjectParent() == null) {
			if(selectedObject.getType() == ObjectType.CARTH_PATH) {
			    if(selectedHole.cartPath != selectedObject)
				throw new IllegalStateException("Cart path selectedObject does not match selectedHole.cartPath");
			    else if(selectedObject.getType() != ObjectType.CARTH_PATH)
				throw new IllegalStateException("Cart path selectedObject.type can not be changed");
			    else
				selectedHole.cartPath = (DrawableCourseShape) selectedObject;
			}
			else if(selectedObject.getType() == ObjectType.GREEN) {
			    if(selectedObject instanceof DrawableGreen == false)
				throw new IllegalStateException("Green modifiedObject is not of a Green object instance");
			    else if(selectedHole.green != selectedObject)
				throw new IllegalStateException("Green selectedObject does not match selectedHole.green");
			    else if(selectedObject.getType() != ObjectType.GREEN)
				throw new IllegalStateException("Green selectedObject.type can not be changed");
			    else
				selectedHole.green = (DrawableGreen) selectedObject;
			}
			else if(selectedObject.getType() == ObjectType.APPROACH_LINE) {
			    if(selectedObject instanceof DrawableCourseShape == false)
				throw new IllegalStateException("ApproachLine modifiedObject is not of a Green object instance");
			    else if(selectedHole.getApproachLine() != selectedObject)
				throw new IllegalStateException("ApproachLine selectedObject does not match selectedHole.approachLine");
			    else if(selectedObject.getType() != ObjectType.APPROACH_LINE)
				throw new IllegalStateException("ApproachLine selectedObject.type can not be changed");
			    else
				selectedHole.setApproachLine((DrawableCourseShape) selectedObject);
			}
			else if(selectedObject.getType() == ObjectType.HOLE_OUTLINE) {
			    if(selectedObject instanceof DrawableCourseShape == false)
				throw new IllegalStateException("modifiedObject instanceof DrawableCourseShape == false");
			    else if(selectedHole.getHoleOutline() != selectedObject)
				throw new IllegalStateException("selectedHole.getHoleOutline() != selectedObject");
			    else if(selectedObject.getType() != ObjectType.HOLE_OUTLINE)
				throw new IllegalStateException("modifiedObject.getType() != ObjectType.HOLE_OUTLINE");
			    else
				selectedHole.setHoleOutline((DrawableCourseShape) selectedObject);
			}
			else {
			    int index = selectedHole.getPoints().indexOf(selectedObject);
			    if(index >= 0) {
				selectedHole.getPoints().set(index, (DrawableCoursePoint) selectedObject);
                            }
                            else {
                                index = selectedHole.getShapes().indexOf(selectedObject);
                                if(index >= 0) {
                                    selectedHole.getShapes().set(index, (DrawableCourseShape) selectedObject);
                                }
                                else {
                                    throw new IllegalStateException("selectedObject not found in selectedHole");
                                }
                            }
			}
		    }
		    else {
			List objects = getCustomObjectParent().getObjectsList();
			int index = objects.indexOf(selectedObject);
			if(index >= 0)
			    objects.set(index, selectedObject);
			else
			    throw new IllegalStateException("selectedObject not found in selectedHole");
		    }

                    setObjectRendererObjects();

                    DefaultListModel model = (DefaultListModel) objectsList.getModel();
                    int index = model.indexOf(selectedObject);
                    if( index >= 0)
                        model.setElementAt(selectedObject, index);
                    else
                        throw new IllegalStateException("selectedObject not found in objectsList");

                    setObject(selectedObject);
                    editorPanel.setObjectInformationChanged(false);
                }

                if(pendingOperation != null) {
                    switch(pendingOperation.firstItem) {
                        case ADD:
                            addObject();
                            break;
                        case EDIT:
                            objectsList.setSelectedValue(pendingOperation.secondItem, true);
                            //startEditingObject(pendingOperation.secondItem);
                            break;
                        case EXIT:
                            exitThisScreen();
                            break;
                        case SELECT_COURSE:
                            courseComboBox.setSelectedItem(pendingOperation.secondItem);
                            break;
                        case SELECT_HOLE:
                            holeComboBox.setSelectedItem(((HoleDefinition)pendingOperation.secondItem).number);
                            break;
                    }
                }
            }
            pendingOperation = null;
        }
    }

    @Action
    public void cancelAction() {
        //setObjectInformationChanged(false);
        if(stopEditingObject(null))
            ((DefaultListSelectionModel)objectsList.getSelectionModel()).clearSelection();
    }

    @Action
    public void centerEditingObject() {
	final Dashboard dashboard = editorPanel.getDashboard();

        if(dashboard != null)
            dashboard.repaint();

	//Attempt to invoke after repainting
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		GolfCourseObject modifiedObject = editorPanel.getSelectedObject();

                if(dashboard != null && modifiedObject != null) {
                    if(modifiedObject instanceof DrawableCoursePoint) {
                        DrawableCoursePoint point = (DrawableCoursePoint) modifiedObject;
                        if(point.coordinates != null) {
                            double scale = dashboard.getMaxScale()/dashboard.getScale()/2;
                            dashboard.zoomInAndCenter(((DrawableCoursePoint)modifiedObject).coordinates, scale);
                        }
                    }
                    else if(modifiedObject instanceof DrawableCourseShape) {
                        DrawableCourseShape drawableCourseShape = (DrawableCourseShape) modifiedObject;
                        Shape shape = drawableCourseShape.getLastDrawnShape();

                        if(shape != null) {
                            Rectangle2D r = shape.getBounds2D();
                            double scale = Math.min(0.5 *dashboard.getWidth() / r.getWidth(),
                                    0.5 * dashboard.getHeight() / r.getHeight());
                            dashboard.zoomInAndCenterViewPoint(new Point2D.Double(
                                    r.getCenterX(),
                                    r.getCenterY()), scale);
                        }
                    }
                }
            }
        });
    }

    @Action
    public void loadPoints() {
        if (fileChooser.showOpenDialog(toolbar) == JFileChooser.APPROVE_OPTION) {
            try {
                for(File file: fileChooser.getSelectedFiles()) {
		    BufferedReader reader = new BufferedReader(new FileReader(file));

		    List<Coordinates> points = new ArrayList<Coordinates>();
		    String line = reader.readLine();
		    while(line != null) {
			try {
			    String[] split = line.split(",");

			    if(split.length == 3) {
				line = line.substring(0, line.lastIndexOf(","));
				Coordinates c = new Coordinates(line.trim());
				BasicDrawablePointObject p = new BasicDrawablePointObject(c);
				p.color = Color.RED;
				trackPoints.add(p);
			    }
			    else {
				Coordinates c = new Coordinates(line.trim());
				points.add(c);
			    }
			}
			catch(Exception ex) {}
			line = reader.readLine();
		    }

		    trackPoints.add(new BasicDrawableShapeObject(points, false, 1f, Color.blue, null));
		    DashboardCartTrackRenderer trackRenderer = editorPanel.getObjectRenderer().getTrackRenderer();
		    trackRenderer.setDrawableObjects(trackPoints);
		    editorPanel.getDashboard().repaint();
		}
            }
            catch(Exception ex) {
		log.error("Error loading points file" + ex);
		log.debug("Error loading points file", ex);
//                TaskDialogs.showException(new RuntimeException("Error loading points file", ex));
                NotificationPopup.showErrorDialog("Error loading points file");
            }
        }
    }

    @Action
    public void clearPoints() {
	trackPoints.clear();
	DashboardCartTrackRenderer trackRenderer = editorPanel.getObjectRenderer().getTrackRenderer();
	trackRenderer.setDrawableObjects(trackPoints);
	editorPanel.getDashboard().repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addObjectButton;
    private javax.swing.JCheckBox autoCenterCheckBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton centerObjectButton;
    private javax.swing.JButton clearPointsButton;
    private javax.swing.JComboBox courseComboBox;
    private javax.swing.JLabel courseLabel;
    private org.jdesktop.swingx.JXPanel courseObjectListSelector;
    private javax.swing.JButton deleteObjectButton;
    private com.stayprime.basestation2.ui.modules.DrawingToolEditorPanel editorPanel;
    private javax.swing.JFileChooser fileChooser;
    private com.stayprime.ui.Glue glue1;
    private javax.swing.JComboBox holeComboBox;
    private javax.swing.JLabel holeLabel;
    private javax.swing.JComboBox imageComboBox;
    private javax.swing.JButton loadPointsButton;
    private org.jdesktop.swingx.JXPanel objectControlPanel;
    private org.jdesktop.swingx.JXPanel objectParentPanel;
    private javax.swing.JList objectsList;
    private javax.swing.JScrollPane objectsScrollPane;
    private org.jdesktop.swingx.JXTitledSeparator objectsSeparator;
    private javax.swing.JToggleButton rulerButton;
    private javax.swing.JButton saveButton;
    private org.jdesktop.swingx.JXPanel savePanel;
    private org.jdesktop.swingx.JXTitledSeparator saveSeparator;
    private javax.swing.JComboBox showObjectsComboBox;
    private javax.swing.JLabel showObjectsLabel;
    private org.jdesktop.swingx.JXPanel toolbar;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private void notifyError(Exception error) {
        TaskDialogs.showException(error);
    }

    /*
     * Implement Screen interface
     */
    @Override
    public void enterScreen(ScreenParent screenParent) {
        this.screenParent = screenParent;
	imageComboBox.setSelectedIndex(0);
        changesSaved = false;
    }

    @Override
    public boolean exitScreen() {
        boolean exit = stopEditingObject(new Pair(Operation.EXIT, null));
	if(exit) {
	    imageComboBox.setSelectedIndex(0);

	    CustomObjectParent objectParent = getCustomObjectParent();
	    if(objectParent != null) {
		objectParent.setObjectsList(objectParent.getObjectsList());
	    }

            if (changesSaved) {
                BaseStation2App.getApplication().runSyncGolfClubTask();
            }
	}
	return exit;
    }

    public void exitThisScreen() {
        if(screenParent != null)
            screenParent.exitScreen(this);
    }

    @Override
    public Component getToolbarComponent() {
        return toolbar;
    }

    /*
     * Implements ObjectRenderer interface
     */
    @Override
    public void renderObjects(Graphics g, MapProjection p, boolean quickDraw) {
        if(editorPanel.getObjectRenderer() != null) {
            editorPanel.getObjectRenderer().renderObjects(g, p, quickDraw);
            editorPanel.getMouseListener().getDistanceRenderer().renderObjects(g, p, quickDraw);
        }
    }

    @Override
    public void setDrawableObjects(List<DrawableObject> courseObjects) {
    }

    @Override
    public List<DrawableObject> getDrawableObjects() {
        return null;
    }

    @Override
    public void reset() {
    }

    /*
     * Implements Scrollable interface
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
       return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ((orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width) - 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /*
     * Image loader callback (Course/Hole)
     */

    private class CourseImageRetrievedCallback implements ImageRetrievedCallback {
	private BasicMapImage map;
	private Dashboard dashboard;

	public CourseImageRetrievedCallback(BasicMapImage map, Dashboard dashboard) {
	    this.map = map;
	    this.dashboard = editorPanel.getDashboard();
	}

	public void imageRetrieved(BufferedImage image) {
	    if(dashboard != null) {
		dashboard.setMap(map);

		boolean courseImage = imageComboBox.getSelectedIndex() == 0;
		dashboard.setMinScaleFillsViewport(courseImage);
		if(courseImage) {
		    dashboard.setImage(image);
                }
                else {
		    dashboard.setImage(MapUtils.getNormalizedMapImage(map, image));
                }
	    }
	}

	public void retrieveImageFailed(Exception cause) {
	    TaskDialogs.showException(cause);
	    editorPanel.getDashboard().setImage(null);
	}

	public void retrieveImageCanceled() {
	}
    }

    /*
     * Custom object parent interface
     */

    public interface CustomObjectParent {
	public Component getComponent();
	public List<DrawableObject> getObjectsList();
	public void setObjectsList(List<DrawableObject> objects);
	public GolfCourseObject createNewObject();
	public ObjectType[] getValidObjectTypes(GolfCourseObject object);
	public boolean isObjectDeletable(GolfCourseObject object);
	public boolean canCreateObjects();
	public boolean useDrawingToolObjectFilters();
	public List<DrawableObject> getObjectRendererObjects();
    }
}
