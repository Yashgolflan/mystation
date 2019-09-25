/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DrawingToolEditorPanel.java
 *
 * Created on 8/06/2011, 01:51:45 PM
 */

package com.stayprime.basestation2.ui.modules;

import com.aeben.elementos.mapview.DrawableShapeObject;
import com.aeben.golfclub.DrawableCourseImage;
import com.aeben.golfclub.DrawableCoursePoint;
import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.DrawableRestrictedZone;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.GolfCourseObject.ObjectType;
import com.aeben.golfclub.view.Dashboard;
import com.stayprime.basestation2.renderers.DashboardObjectRenderer;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXTaskPane;

/**
 *
 * @author benjamin
 */
public class DrawingToolEditorPanel extends javax.swing.JPanel implements DrawingToolInterface {
    private static final Logger log = LoggerFactory.getLogger(DrawingToolEditorPanel.class);
    private float fontIncrease = -2f;

    private GolfCourseObject selectedObject;
    private int gridSystemNumberOfGrids = 8;
    private DrawableCourseShape greenGridShape;
    private DrawingToolObjectRenderer objectRenderer;
    private DrawingToolMouseListener mouseListener;
    private Dashboard dashboard;
    private ObjectType[] validObjectTypes;

    private boolean editingGreenGridPoints = false;
    private boolean editingGreenGridLines = false;
    private boolean objectInformationChanged = false;
    public static final String PROP_OBJECTINFORMATIONCHANGED = "objectInformationChanged";

    /** Creates new form DrawingToolEditorPanel */
    public DrawingToolEditorPanel() {
        initComponents();

        ((org.jdesktop.swingx.VerticalLayout) taskPaneContainer.getLayout()).setGap(4);
        ((JComponent)locationPanel.getContentPane()).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        ((JComponent)shapeEditPanel.getContentPane()).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        ((JComponent)greenPointsTaskPane.getContentPane()).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        ((JComponent)gridPointsTaskPane.getContentPane()).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        PropertyChangeListener taskPaneGroupListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if(((JXTaskPane)evt.getSource()).isCollapsed() == false) {
                    locationPanel.setCollapsed(evt.getSource() != locationPanel);
                    shapeEditPanel.setCollapsed(evt.getSource() != shapeEditPanel);
                    greenPointsTaskPane.setCollapsed(evt.getSource() != greenPointsTaskPane);
                    gridPointsTaskPane.setCollapsed(evt.getSource() != gridPointsTaskPane);

                    setEditingGreenGridPoints(evt.getSource() == gridPointsTaskPane);
                }
                else if(evt.getSource() == gridPointsTaskPane)
                    setEditingGreenGridPoints(false);
            }

        };
        locationPanel.addPropertyChangeListener("collapsed", taskPaneGroupListener);
        shapeEditPanel.addPropertyChangeListener("collapsed", taskPaneGroupListener);
        greenPointsTaskPane.addPropertyChangeListener("collapsed", taskPaneGroupListener);
        gridPointsTaskPane.addPropertyChangeListener("collapsed", taskPaneGroupListener);

        typeCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    changeEditingObjectType((ObjectType) e.getItem());
                }
            }
        });

        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateEditingObjectName(nameField.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                updateEditingObjectName(nameField.getText());
            }
            public void changedUpdate(DocumentEvent e) {
                updateEditingObjectName(nameField.getText());
            }
        });

        messageArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateEditingObjectMessage(messageArea.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                updateEditingObjectMessage(messageArea.getText());
            }
            public void changedUpdate(DocumentEvent e) {
                updateEditingObjectMessage(messageArea.getText());
            }
        });

        colorComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(colorComboBox.getSelectedItem() instanceof Color)
                    updateEditingObjectColor((Color) colorComboBox.getSelectedItem(),
                            opacitySlider.getValue()/100f);
            }

        });

        opacitySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(selectedObject instanceof DrawableCourseShape) {
                    DrawableCourseShape shape = (DrawableCourseShape) selectedObject;
                    if(colorComboBox.getSelectedItem() instanceof Color && shape.fillColor != null)
                        updateEditingObjectColor(shape.fillColor, opacitySlider.getValue()/100f);
                }
            }
        });

	objectRenderer = new DrawingToolObjectRenderer();
	mouseListener = new DrawingToolMouseListener(this, objectRenderer);
    }

    public boolean isObjectInformationChanged() {
        return objectInformationChanged;
    }

    public void setObjectInformationChanged(boolean objectInformationChanged) {
        boolean oldObjectInformationChanged = this.objectInformationChanged;
        this.objectInformationChanged = objectInformationChanged;
        firePropertyChange(PROP_OBJECTINFORMATIONCHANGED, oldObjectInformationChanged, objectInformationChanged);
    }

    public GolfCourseObject getEditingObject() {
        if(editingGreenGridPoints || editingGreenGridLines)
            return greenGridShape;
        else
            return selectedObject;
    }

    public GolfCourseObject getSelectedObject() {
	return selectedObject;
    }

    public void setObject(GolfCourseObject original, ObjectType[] types) {
        locationPanel.setAnimated(false);
        shapeEditPanel.setAnimated(false);
        greenPointsTaskPane.setAnimated(false);
        gridPointsTaskPane.setAnimated(false);

        //Object Type combo setup
	this.validObjectTypes = types;
	if(types != null)
	    typeCombo.setModel(new DefaultComboBoxModel(types));
	else if(original != null) {
            if(original.getType() == ObjectType.CARTH_PATH)
                typeCombo.setModel(new DefaultComboBoxModel(new ObjectType[] {ObjectType.CARTH_PATH}));
            else if(original.getType() == ObjectType.GREEN)
                typeCombo.setModel(new DefaultComboBoxModel(new ObjectType[] {ObjectType.GREEN}));
            else if(original.getType() == ObjectType.APPROACH_LINE)
                typeCombo.setModel(new DefaultComboBoxModel(new ObjectType[] {ObjectType.APPROACH_LINE}));
            else
                typeCombo.setModel(new DefaultComboBoxModel(new ObjectType[] {
                    ObjectType.HAZARD, ObjectType.HOLE_ZONE, ObjectType.HOLE_OUTLINE,
		    ObjectType.ACTION_ZONE, ObjectType.RESTRICTED_ZONE, ObjectType.CLUBHOUSE_ZONE,
		    ObjectType.CART_BARN, ObjectType.AD_ZONE, ObjectType.REFERENCE
                }));
        }

        //Set original and modified objects
        if(original != null) {
            selectedObject = original;
        }
        else {
            selectedObject = null;
        }

        //Set visible panels based on object type
        editObjectPanel.setVisible(
                selectedObject instanceof GolfCourseObject);
        locationPanel.setVisible(
                selectedObject instanceof DrawableCoursePoint ||
                selectedObject instanceof DrawableCourseImage);
        shapeEditPanel.setVisible(
                selectedObject instanceof DrawableCourseShape);
        messagePanel.setVisible(
                selectedObject instanceof DrawableRestrictedZone);
        greenPointsTaskPane.setVisible(
                selectedObject instanceof DrawableGreen);
        gridPointsTaskPane.setVisible(
                selectedObject instanceof DrawableGreen);

        //Turn off all point and shape editors
        pickLocationButton.setSelected(false);
        editShapeButton.setSelected(false);
        moveShapeButton.setSelected(false);
        frontOfGreenButton.setSelected(false);
        middleOfGreenButton.setSelected(false);
        backOfGreenButton.setSelected(false);
        gridCenterButton.setSelected(false);
	editGridPointsButton.setSelected(false);

        //Set the collapsed state of the task panes
        if(selectedObject instanceof DrawableCourseImage || selectedObject instanceof DrawableCoursePoint) {
            locationPanel.setCollapsed(false);
        }
        if(selectedObject instanceof DrawableCourseShape) {
            shapeEditPanel.setCollapsed(false);
            greenPointsTaskPane.setCollapsed(true);
            gridPointsTaskPane.setCollapsed(true);
        }

        if(objectRenderer != null) {
            objectRenderer.setEditingObject(selectedObject);
            objectRenderer.setEditingShape(false);
	    objectRenderer.setEditingGridLines(false);
        }
        if(mouseListener != null) {
            mouseListener.setOperation(DrawingToolMouseListener.Operation.NONE);
        }

        nameField.setText(null);
        if(selectedObject != null) {
            typeCombo.setSelectedItem(selectedObject.getType());
            nameField.setText(selectedObject.getName());
        }

        if(selectedObject instanceof DrawableCoursePoint) {
            DrawableCoursePoint point = (DrawableCoursePoint) selectedObject;
            locationField.setText(point.coordinates == null? null :
                    point.coordinates.toReadableString());
        }
        if(selectedObject instanceof DrawableCourseShape) {
            DrawableCourseShape shape = (DrawableCourseShape) selectedObject;
            //TODO: deal with shapes with null fill color
            if(shape.fillColor == null)
                shape.fillColor = Color.WHITE;
            opacitySlider.setValue(Math.round(shape.fillColor.getAlpha()/255f*100f));
            colorComboBox.setSelectedItem(new Color(shape.fillColor.getRGB(), false));
        }
        if(selectedObject instanceof DrawableRestrictedZone) {
            DrawableRestrictedZone zone = (DrawableRestrictedZone) selectedObject;
            messageArea.setText(zone.message);
        }

        locationPanel.setAnimated(true);
        shapeEditPanel.setAnimated(true);
        greenPointsTaskPane.setAnimated(true);
        gridPointsTaskPane.setAnimated(true);

        invalidate();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(getTopLevelAncestor() != null)
                    getTopLevelAncestor().validate();
            }
        });
    }

    private void changeEditingObjectType(ObjectType type) {
        if(selectedObject != null && selectedObject.getType() != type) {
            switch(type) {
                case HOLE_ZONE:
                case HOLE_OUTLINE:
                case CLUBHOUSE_ZONE:
                case CART_BARN:
                case AD_ZONE:
                    DrawableCourseShape newShape;
                    if(selectedObject instanceof DrawableCourseShape) {
                        newShape = ((DrawableCourseShape)selectedObject);
                        newShape.type = type;
                    }
                    else {
                        newShape = new DrawableCourseShape(selectedObject.getName(),
                                selectedObject.getId(), selectedObject.getParentObject());
                        newShape.type = type;
                    }
                    setObject(newShape, validObjectTypes);
                    setObjectInformationChanged(true);
                    break;
                case ACTION_ZONE:
                case RESTRICTED_ZONE:
                    DrawableRestrictedZone zone;
                    if(selectedObject instanceof DrawableRestrictedZone) {
                        zone = ((DrawableRestrictedZone)selectedObject);
                        zone.type = type;
                    }
                    else if(selectedObject instanceof DrawableCourseShape) {
                        DrawableCourseShape shape = (DrawableCourseShape) selectedObject;
                        zone = new DrawableRestrictedZone(shape.getName(),
                                shape.getId(), shape.getParentObject(),
                                type, shape.shapeCoordinates, shape.closedShape,
                                shape.strokeWidth, shape.strokeColor, shape.fillColor,
                                null);
                    }
                    else {
                        zone = new DrawableRestrictedZone(selectedObject.getName(),
                                selectedObject.getId(), selectedObject.getParentObject());
                        zone.type = type;
                    }
                    setObject(zone, validObjectTypes);
                    setObjectInformationChanged(true);
                    break;
                case HAZARD:
                case FRONT_GREEN:
                case MIDDLE_GREEN:
                case BACK_GREEN:
                case REFERENCE:
                    DrawableCoursePoint newPoint;
                    if(selectedObject instanceof DrawableCoursePoint) {
                        newPoint = ((DrawableCoursePoint)selectedObject);
                        newPoint.type = type;
                    }
                    else if(selectedObject instanceof DrawableCourseImage) {
                        DrawableCourseImage image = (DrawableCourseImage) selectedObject;
                        newPoint = new DrawableCoursePoint(image.getName(),
                                image.getId(), image.getParentObject(),
                                type, image.coordinates);
                    }
                    else {
                        newPoint = new DrawableCoursePoint(selectedObject.getName(),
                                selectedObject.getId(), selectedObject.getParentObject(), type, null);
                    }
                    setObject(newPoint , validObjectTypes);
                    setObjectInformationChanged(true);
                    break;
                default:
                    log.warn("Change object type failed on object type: "
                            + selectedObject.getType() + " to: " + type);
            }
        }

        setObjectInformationChanged(true);
    }

    private void updateEditingObjectName(String text) {
        if(selectedObject instanceof DrawableCoursePoint)
            ((DrawableCoursePoint) selectedObject).name = text;
        else if(selectedObject instanceof DrawableCourseImage)
            ((DrawableCourseImage) selectedObject).name = text;
        else if(selectedObject instanceof DrawableShapeObject)
            ((DrawableCourseShape) selectedObject).name = text;
        setObjectInformationChanged(true);
    }

    private void updateEditingObjectMessage(String text) {
        if(selectedObject instanceof DrawableRestrictedZone) {
            ((DrawableRestrictedZone) selectedObject).message = text;
            setObjectInformationChanged(true);
        }
    }

    private void updateEditingObjectColor(Color color, float alpha) {
        if(selectedObject instanceof DrawableCourseShape){
            int argb = color.getRGB() & 0xffffff | (int)(alpha*255) << 24;
            ((DrawableCourseShape) selectedObject).fillColor = new Color(argb, true);
            ((DrawableCourseShape) selectedObject).strokeColor = new Color(argb, true);
            setObjectInformationChanged(true);
            if(dashboard != null)
                dashboard.repaint();
        }
    }

    public void addShapePoint(DrawableCourseShape shape, Coordinates coord) {
        if(shape == selectedObject || shape == greenGridShape) {
            if(shape.getShapeCoordinates() == null)
                shape.shapeCoordinates = new ArrayList<Coordinates>();

            checkSelectedShapePointIndex(shape);

            shape.getShapeCoordinates().add(objectRenderer.getSelectedShapePointIndex() + 1, coord);
            setSelectedShapePointIndex(objectRenderer.getSelectedShapePointIndex() + 1);
            setObjectInformationChanged(true);

            if(dashboard != null)
                dashboard.repaint();
        }
	else
            throw new IllegalStateException("setShapePoint shape != DrawingTool.modifiedObject");
    }

    public void deleteSelectedShapePoint(DrawableCourseShape shape) {
        if(shape == selectedObject || shape == greenGridShape) {
            if(shape.getShapeCoordinates() != null) {
                checkSelectedShapePointIndex(shape);

                int index = objectRenderer.getSelectedShapePointIndex();
                if(index >= 0) {
                    shape.getShapeCoordinates().remove(index);
                    index = index > 0? index - 1 : shape.getShapeCoordinates().size() - 1;
                    setSelectedShapePointIndex(index);
                    setObjectInformationChanged(true);
                }

                if(dashboard != null)
                    dashboard.repaint();
            }
        }
	else
            throw new IllegalStateException("setShapePoint shape != DrawingTool.modifiedObject");
    }

    public void deleteShapePoint(DrawableCourseShape shape, int pointIndex) {
        if(shape == selectedObject || shape == greenGridShape) {
            if(shape.getShapeCoordinates() != null && pointIndex < shape.getShapeCoordinates().size()) {
                if(pointIndex >= 0) {
                    shape.getShapeCoordinates().remove(pointIndex);
                    int index = pointIndex > 0? pointIndex - 1 : shape.getShapeCoordinates().size() - 1;
                    setSelectedShapePointIndex(index);
                    setObjectInformationChanged(true);
                }

            if(dashboard != null)
                dashboard.repaint();
            }
        }
        else
            throw new IllegalStateException("setShapePoint shape != DrawingTool.modifiedObject");
    }

    public void setShapePoint(DrawableCourseShape shape, int pointIndex, Coordinates coord) {
	boolean editingGreenGrid = editingGreenGridPoints || editingGreenGridLines;

        if(editingGreenGrid && shape != greenGridShape)
            throw new IllegalStateException("setShapePoint editingGreenGridPoints = true and shape != greenGridPointsShape");
        else if(!editingGreenGrid && shape != selectedObject)
            throw new IllegalStateException("setShapePoint shape != DrawingTool.modifiedObject");
        else {
            if(shape.getShapeCoordinates() != null && pointIndex < shape.getShapeCoordinates().size()) {
                shape.getShapeCoordinates().set(pointIndex, coord);
                setSelectedShapePointIndex(pointIndex);
                setObjectInformationChanged(true);

                if(dashboard != null)
                    dashboard.repaint();
            }
        }
    }

    public void moveShape(DrawableCourseShape shape, int pointIndex, Coordinates coord) {
	if(shape.getShapeCoordinates() != null && pointIndex < shape.getShapeCoordinates().size()) {
	    shape.getShapeCoordinates().set(pointIndex, coord);
	    setSelectedShapePointIndex(pointIndex);
	    setObjectInformationChanged(true);

	    if(dashboard != null)
		dashboard.repaint();
	}
    }

    private void checkSelectedShapePointIndex(DrawableCourseShape shape) {
        if(objectRenderer.getSelectedShapePointIndex() >= shape.getShapeCoordinates().size())
                objectRenderer.setSelectedShapePointIndex(shape.getShapeCoordinates().size() - 1);
    }

    @Override
    public void setObjectPoint(GolfCourseObject object, Coordinates coord, boolean isAdjusting) {
        pickLocationButton.setSelected(false);
        if(object != selectedObject)
            throw new IllegalStateException("setObjectPoint object != DrawingTool.modifiedObject");
        else {
            if(selectedObject instanceof DrawableCoursePoint) {
                ((DrawableCoursePoint)selectedObject).coordinates = coord;
                locationField.setText(coord.toReadableString());
                setObjectInformationChanged(true);
            }
            else if(selectedObject instanceof DrawableCourseImage) {
                ((DrawableCourseImage)selectedObject).coordinates = coord;
                locationField.setText(coord.toReadableString());
                setObjectInformationChanged(true);
            }
            else if(selectedObject instanceof DrawableGreen) {
                DrawableGreen green = (DrawableGreen) selectedObject;
                if(frontOfGreenButton.isSelected())
                    green.front = coord;
                else if(middleOfGreenButton.isSelected())
                    green.middle = coord;
                else if(backOfGreenButton.isSelected())
                    green.back = coord;
                else if(gridCenterButton.isSelected()) {
                    Coordinates oldCenter = getGreenGridPoint(green, 0);
                    Coordinates oldDirection = getGreenGridPoint(green, 1);
                    setGreenGridPoint(green, 0, coord);
                    if(oldCenter != null && oldDirection != null && coord != null) {
                        setGreenGridPoint(green, 1, new Coordinates(
                                oldDirection.latitude + (coord.latitude - oldCenter.latitude),
                                oldDirection.longitude + (coord.longitude - oldCenter.longitude)));
                    }

                    calculateGreenGridAreas(green, green.lastDrawnShape, gridSystemNumberOfGrids,
                            dashboard.getMap(), dashboard.getScale(), dashboard.getImageRectangle());
                }
//                else if(gridDirectionButton.isSelected()) {
//                    setGreenGridPoint(green, 1, coord);
//                    calculateGreenGridAreas(green, green.lastDrawnShape, gridSystemNumberOfGrids,
//                            dashboard.getMap(), dashboard.getScale(), dashboard.getImageRectangle());
//                }

                setObjectInformationChanged(true);
            }

            if(!isAdjusting) {
                mouseListener.setOperation(DrawingToolMouseListener.Operation.NONE);
                pickLocationButton.setSelected(false);
                frontOfGreenButton.setSelected(false);
                middleOfGreenButton.setSelected(false);
                backOfGreenButton.setSelected(false);
                gridCenterButton.setSelected(false);
            }

            if(dashboard != null)
                dashboard.repaint();
        }
    }

    private void setGreenGridPoint(DrawableGreen green, int i, Coordinates coord) {
        if(green.gridPoints == null)
            green.gridPoints = new ArrayList<Coordinates>();

        if(i < 0)
            green.gridPoints.get(i);

        while(i >= green.gridPoints.size())
            green.gridPoints.add(null);

        green.gridPoints.set(i, coord);
    }

    private Coordinates getGreenGridPoint(DrawableGreen green, int i) {
        if(green.gridPoints == null)
            return null;

        if(i >= green.gridPoints.size())
            return null;

        return green.gridPoints.get(i);
    }

    private void calculateGreenGridAreas(DrawableGreen green, Shape greenShape,
	    int grids, BasicMapImage map, double scale, Rectangle2D imageRect) {
//        if(greenShape instanceof Polygon2D.Double == false)
//            return;
//
//        Polygon2D.Double poly = (Polygon2D.Double) greenShape;
//
//        if(green.gridPoints != null && green.gridPoints.size() >= 2
//                && green.gridPoints.get(0) != null && green.gridPoints.get(1) != null) {
//
//	    Point2D middle = dashboard.convertMapPointToView(
//		    dashboard.convertCoordinatesToMapPoint(green.gridPoints.get(0)));
//	    Point2D front = dashboard.convertMapPointToView(
//		    dashboard.convertCoordinatesToMapPoint(green.gridPoints.get(1)));
//
//            //double m = (front.y - middle.y)/(front.x - middle.x);
//            double angle = Math.atan2((front.getY() - middle.getY()), (front.getX() - middle.getX()));
//            List<UnitVector> polarVectors = new ArrayList<UnitVector>();
//
//            if(grids > 1 && grids < 9 && (grids == 3 || grids % 2 == 0)) {
//                UnitVector centerLine = new UnitVector(middle, angle);
//                if(grids % 2 == 0) //divide in half
//                    polarVectors.add(centerLine);
//                    //l.add(DrawingToolObjectRenderer.projectLineInPolygon(middle, m, greenShape));
//
//                //m = -1/m;
//                angle = angle + Math.PI / 2;
//                if(grids == 3 || grids > 4)//external division 1
//                    polarVectors.add(new UnitVector(front, angle));
//                    //l.add(DrawingToolObjectRenderer.projectLineInPolygon(front, m, greenShape));
//
//                if(grids % 3 != 0) //divide the other half
//                    polarVectors.add(new UnitVector(middle, angle));
//                    //l.add(DrawingToolObjectRenderer.projectLineInPolygon(middle, m, greenShape));
//
//
//                if(grids == 3 || grids > 4) {//external division 2
//                    Point2D frontMirror = new Point2D.Double(2*middle.getX() - front.getX(), 2*middle.getY() - front.getY());
//                    polarVectors.add(new UnitVector(frontMirror, angle));
//                    //l.add(DrawingToolObjectRenderer.projectLineInPolygon(frontMirror, m, greenShape));
//                }
//
//                green.gridPoints = new ArrayList<Coordinates>(green.gridPoints.subList(0, 2));
//                //calculo con línea central
//                if(grids > 2 && grids % 2 == 0) {
//                    int halfOfGrids = (grids/2);
//
//                    for (int i = 0; i < grids; i++) { //recorrido de 0 a n/2-1
//                        List<Point2D> area = null;//findArea(greenShape, polarVectors.get(0), polarVectors.get(1));
//
//                        if(i < halfOfGrids) {
//                            if (i == 0) //primer grid de linea0 a linea1
//                                area = findAreaPerpendicularLines(poly, polarVectors.get(0), polarVectors.get(1));
//                            else if(i == halfOfGrids - 1) //ultimo grid de linea_i a linea0
//                                area = findAreaPerpendicularLines(poly, polarVectors.get(i), polarVectors.get(0).invert());
//                            else //grid intermedio de linea_i a linea0 a linea_i+1
//                                area = findAreaPerpendicularLines(poly, polarVectors.get(i), polarVectors.get(0).invert(), polarVectors.get(i + 1));
//                        }
//                        else {
//                            int j = i - halfOfGrids;
//                            if (j == 0) //primer grid de linea0 a linea_n/2-1
//                                area = findAreaPerpendicularLines(poly, polarVectors.get(0).invert(), polarVectors.get(halfOfGrids - 1).invert());
//                            else if(j == halfOfGrids - 1) //ultimo grid de linea_i a linea0
//                                area = findAreaPerpendicularLines(poly, polarVectors.get(1).invert(), polarVectors.get(0));
//                            else //grid intermedio de linea_i a linea0 a linea_i+1
//                                area = findAreaPerpendicularLines(poly, polarVectors.get(halfOfGrids - j).invert(), polarVectors.get(0), polarVectors.get(halfOfGrids - j - 1).invert());
//                        }
//
//                        if(area != null) {
//                            Point2D pf = getPolygonCentroid(area);
//                            Rectangle2D bounds = getPolygonBounds(area);
//
//                            if(!bounds.contains(pf))
//                                pf = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
//
//			    Point2D mapPoint = dashboard.convertViewPointToMap(pf);
//			    Coordinates coord = dashboard.convertMapPointToCoordinates(mapPoint);
////			    Coordinates coord = CoordinateCalculations.getCoordinatesFromXYRelativeToMapPixel(
////                                    new Point2D.Double(pf.getX() - imageRect.getX(), pf.getY() - imageRect.getY()),
////                                    map, imageRect);
//
//                            green.gridPoints.add(coord);
//                        }
//                        else {
//                            green.gridPoints.add(green.gridPoints.get(0));
//                        }
//                    }
//                }
//                else if(grids == 3) { //Calculo sin línea central (3 grids)
//                    for (int i = 0; i < grids; i++) { //recorrido de 0 a n/2-1
//                        List<Point2D> area = null;//findArea(greenShape, polarVectors.get(0), polarVectors.get(1));
//
//                        if (i == 0) //primer grid de linea0 a linea1
//                            area = findAreaParalellLines(poly, polarVectors.get(0), centerLine);
//                        else if(i == 1) //ultimo grid de linea_i a linea0
//                            area = findAreaParalellLines(poly, polarVectors.get(0), centerLine.invert(), polarVectors.get(1));
//                        else //grid intermedio de linea_i a linea0 a linea_i+1
//                            area = findAreaParalellLines(poly, polarVectors.get(1), centerLine.invert());
//
//                        if(area != null) {
//                            Point2D pf = getPolygonCentroid(area);
//                            Rectangle2D bounds = getPolygonBounds(area);
//
//                            if(!bounds.contains(pf))
//                                pf = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
//
//			    Point2D mapPoint = dashboard.convertViewPointToMap(pf);
//			    Coordinates coord = dashboard.convertMapPointToCoordinates(mapPoint);
////			    Coordinates coord =
////                                    CoordinateCalculations.getCoordinatesFromXYRelativeToMapPixel(
////                                    new Point2D.Double(pf.getX() - imageRect.getX(), pf.getY() - imageRect.getY()),
////                                    map, imageRect);
//
//                            green.gridPoints.add(coord);
//                        }
//                        else {
//                            green.gridPoints.add(green.gridPoints.get(0));
//                        }
//                    }
//                }
//            }
//        }
    }

//    private List<Point2D> findAreaPerpendicularLines(Shape greenShape, UnitVector ... v) {
//        List<Point2D> area = new ArrayList<Point2D>();
//        if((v.length == 2 || v.length == 3 ) && greenShape instanceof Polygon2D.Double) {
//            Polygon2D.Double poly = (Polygon2D.Double) greenShape;
//
//            Point2D.Double shapeIntx1 = new Point2D.Double();
//            Point2D.Double shapeIntx2 = new Point2D.Double();
//            int index1 = DrawingToolCalculations.findIntersectionDirectional(v[0], poly, shapeIntx1);
//            int index2 = DrawingToolCalculations.findIntersectionDirectional(v[v.length == 2? 1 : 2], poly, shapeIntx2);
//
//            if(index1 >= 0 && index2 >= 0) {
//                Point2D linesIntx1 = DrawingToolCalculations.getPolarLinesIntersection(v[0], v[1]);
//                area.add(linesIntx1);
//                area.add(shapeIntx1);
//                //Encontrar todos los puntos intermedios del polígono que estan
//                //entre index1 e index2, en la direccion de v[0] a v[1]
//
//                //Punto de inicio del segmento del poligono que intersecta a la
//                //primer recta polar: poly.points[index1]
//                Point2D.Double intersectionPointStartIndex = new Point2D.Double(poly.xpoints[index1], poly.ypoints[index1]);
//                //Vector unitario desde la intersección hasta el punto del poligono poly.points[index1]
//                UnitVector shapeIntersectionToShapePoint =
//                        new UnitVector(shapeIntx1, Math.atan2(intersectionPointStartIndex.y - shapeIntx1.y, intersectionPointStartIndex.x - shapeIntx1.x));
//
//                //Si el producto punto de este vector con la segunda linea es
//                //negativo, este vector va en la direccion contraria a la segunda linea,
//                //por lo tanto incrementando el indice en el poligono, encontraremos
//                //el area en la direccion requerida, direction = true (positivo)
//                boolean increase = DrawingToolCalculations.dotProduct(shapeIntersectionToShapePoint, v[1]) < 0;
//
//                if(index1 != index2) {
//                    int index;
//                    for(int i = 0; i < poly.npoints; i++) {
//                        //Desde el punto siguiente (o previo, si direccion=falso),
//                        //hasta index2, dando la vuelta al poligono
//                        index = (index1 + poly.npoints + (increase? i + 1 :  -i)) % poly.npoints;
//
//                        if(i == 0) {
//                            if(!increase)
//                                area.add(new Point2D.Double(poly.xpoints[index], poly.ypoints[index]));
//                        }
//                        else if(index == index2) {
//                            if(increase)
//                                area.add(new Point2D.Double(poly.xpoints[index], poly.ypoints[index]));
//                        }
//                        else
//                            area.add(new Point2D.Double(poly.xpoints[index], poly.ypoints[index]));
//
//                        if(index == index2)
//                            break;
//                    }
//                }
//
//                area.add(shapeIntx2);
//
//                if(v.length == 3) {
//                    Point2D linesIntx2 = DrawingToolCalculations.getPolarLinesIntersection(v[1], v[2]);
//                    area.add(linesIntx2);
//                }
//
//                return area;
//            }
//        }
//        return null;
//    }
//
//    private List<Point2D> findAreaParalellLines(Shape greenShape, UnitVector ... v) {
//        List<Point2D> area = new ArrayList<Point2D>();
//        if((v.length == 2 || v.length == 3 ) && greenShape instanceof Polygon2D.Double) {
//            Polygon2D.Double poly = (Polygon2D.Double) greenShape;
//
//            Point2D.Double shapeIntx1 = new Point2D.Double();
//            Point2D.Double shapeIntx2 = new Point2D.Double();
//            Point2D.Double shapeIntx3 = new Point2D.Double();
//            Point2D.Double shapeIntx4 = new Point2D.Double();
//            int index1 = DrawingToolCalculations.findIntersectionDirectional(v[0], poly, shapeIntx1);
//            int index2 = DrawingToolCalculations.findIntersectionDirectional(v[v.length == 2? 1 : 2], poly, shapeIntx2);
//            int index3 = DrawingToolCalculations.findIntersectionDirectional(v[v.length == 2? 1 : 2].invert(), poly, shapeIntx3);
//            int index4 = DrawingToolCalculations.findIntersectionDirectional(v[0].invert(), poly, shapeIntx4);
//
//            if(index1 >= 0 && index4 >= 0) {
//                area.add(shapeIntx1);
//                //Encontrar todos los puntos intermedios del polígono que estan
//                //entre index1 e index2, en la direccion de v[0] a v[1]
//
//                //Punto de inicio del segmento del poligono que intersecta a la
//                //primer recta polar: poly.points[index1]
//                Point2D.Double intersectionPointStartIndex = new Point2D.Double(poly.xpoints[index1], poly.ypoints[index1]);
//                //Vector unitario desde la intersección hasta el punto del poligono poly.points[index1]
//                UnitVector shapeIntersectionToShapePoint =
//                        new UnitVector(shapeIntx1, Math.atan2(intersectionPointStartIndex.y - shapeIntx1.y, intersectionPointStartIndex.x - shapeIntx1.x));
//
//                //Si el producto punto de este vector con la segunda linea es
//                //negativo, este vector va en la direccion contraria a la segunda linea,
//                //por lo tanto incrementando el indice en el poligono, encontraremos
//                //el area en la direccion requerida, direction = true (positivo)
//                boolean increase = DrawingToolCalculations.dotProduct(shapeIntersectionToShapePoint, v[1]) < 0;
//                boolean addPoints = true;
//
//                if(index1 != index4) {
//                    int index;
//                    for(int i = 0; i < poly.npoints; i++) {
//                        //Desde el punto siguiente (o previo, si direccion=falso),
//                        //hasta index2, dando la vuelta al poligono
//                        index = (index1 + poly.npoints + (increase? i + 1 :  -i)) % poly.npoints;
//
//                        if(index == index2) {
//                            addPoints = false;
//                        }
//                        if(index == index3) {
//                            addPoints = true;
//                        }
//
//                        if(i == 0) {
//                            if(!increase)
//                                area.add(new Point2D.Double(poly.xpoints[index], poly.ypoints[index]));
//                        }
//                        else if(index == index4) {
//                            if(increase)
//                                area.add(new Point2D.Double(poly.xpoints[index], poly.ypoints[index]));
//                        }
//                        else if(addPoints)
//                            area.add(new Point2D.Double(poly.xpoints[index], poly.ypoints[index]));
//
//                        if(index == index4)
//                            break;
//                    }
//                }
//
//                area.add(shapeIntx4);
//
//                return area;
//            }
//        }
//        return null;
//    }

    // return the centroid of the polygon
    public static Point2D getPolygonCentroid(List<Point2D> poly) {
        double area = getPolygonArea(poly);

        double cx = 0.0, cy = 0.0;
        for (int i = 0; i < poly.size(); i++) {
            int j = i < poly.size() - 1? i+1 : 0;
            cx = cx + (poly.get(i).getX() + poly.get(j).getX())
		    * (poly.get(i).getY() * poly.get(j).getX() - poly.get(i).getX()
		    * poly.get(j).getY());
            cy = cy + (poly.get(i).getY() + poly.get(j).getY())
		    * (poly.get(i).getY() * poly.get(j).getX() - poly.get(i).getX()
		    * poly.get(j).getY());
        }
        double factor = cx < 0? -6 : 6;
        cx /= (factor * area);
        cy /= (factor * area);
        return new Point2D.Double(cx, cy);
    }

    // return the centroid of the polygon
    public static float getPolygonArea(List<Point2D> poly) {
        double area = 0.0;
        for (int i = 0; i < poly.size(); i++) {
            int j = i < poly.size() - 1? i+1 : 0;
            area = area + (poly.get(i).getX() * poly.get(j).getY())
		    - (poly.get(i).getY() * poly.get(j).getX());
        }
        area = Math.abs(0.5 * area);

        return (float) area;
    }

    public static Rectangle2D getPolygonBounds(List<Point2D> poly) {
        if(poly == null || poly.isEmpty())
            return null;

        Rectangle2D bounds = new Rectangle2D.Double(poly.get(0).getX(), poly.get(0).getY(), 0, 0);
        for (Point2D p: poly) {
            bounds.add(p);
        }

        return bounds;
    }

    //http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect/1201356#1201356
    // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines
    // intersect the intersection point may be stored in the floats i_x and i_y.
    public static Point2D.Double getLineIntersection(Point2D p0, Point2D p1,
        Point2D p2, Point2D p3)
    {
        double s1_x, s1_y, s2_x, s2_y;
        s1_x = p1.getX() - p0.getX();     s1_y = p1.getY() - p0.getY();
        s2_x = p3.getX() - p2.getX();     s2_y = p3.getY() - p2.getY();

        double s, t;
        s = (-s1_y * (p0.getX() - p2.getX()) + s1_x * (p0.getY() - p2.getY())) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p0.getY() - p2.getY()) - s2_y * (p0.getX() - p2.getX())) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            // Collision detected
            return new Point2D.Double(p0.getX() + (t * s1_x), p0.getY() + (t * s1_y));
        }

        return null; // No collision
    }

    public void setSelectedShapePointIndex(int selectingPointIndex) {
        objectRenderer.setSelectedShapePointIndex(selectingPointIndex);
    }

    @Action
    public void editShapePoints() {
        setEditingShapePoints(editShapeButton.isSelected());
    }

    @Action
    public void pickObjectLocation() {
        //objectRenderer.setEditingShape(addPointButton.isSelected() || editPointsButton.isSelected());
        if(mouseListener != null)
            mouseListener.setOperation(DrawingToolMouseListener.Operation.EDIT_POINT);
    }

    @Action
    public void pickFrontOfGreen(ActionEvent e) {
        pickGreenPointLocation(e);
    }

    @Action
    public void pickMiddleOfGreen(ActionEvent e) {
        pickGreenPointLocation(e);
    }

    @Action
    public void pickBackOfGreen(ActionEvent e) {
        pickGreenPointLocation(e);
    }

    @Action
    public void pickGridCenter(ActionEvent e) {
        setEditingGreenGridLines(gridCenterButton.isSelected());
    }

    @Action
    public void editGridPoints() {
        setEditingGreenGridPoints(editGridPointsButton.isSelected());
    }

    @Action
    public void clearShape() {
        if(selectedObject != null && selectedObject instanceof DrawableCourseShape) {
            DrawableCourseShape shape = (DrawableCourseShape) selectedObject;
            if(shape.shapeCoordinates != null && shape.shapeCoordinates.size() > 0) {
                if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Do you really want to delete all the points in this shape?", "Question", JOptionPane.YES_NO_OPTION)) {
                    shape.shapeCoordinates.clear();
		    setObjectInformationChanged(true);
                    editShapeButton.setSelected(false);
                    checkSelectedShapePointIndex(shape);
                }
            }
        }
    }

    @Action
    public void moveShape() {
	setMovingShape(moveShapeButton.isSelected());
    }

    private void setEditingShapePoints(boolean b) {
        if(b) {
            setEditingGreenGridPoints(false);
	    setMovingShape(false);
            frontOfGreenButton.setSelected(false);
            middleOfGreenButton.setSelected(false);
            backOfGreenButton.setSelected(false);
            gridCenterButton.setSelected(false);
        }
        else
            editShapeButton.setSelected(false);

        if(objectRenderer != null)
            objectRenderer.setEditingShape(b);
        if(mouseListener != null) {
            mouseListener.setOperation(b?
		DrawingToolMouseListener.Operation.EDIT_SHAPE:
		DrawingToolMouseListener.Operation.NONE);
        }
    }

    private void setEditingGreenGridLines(boolean b) {
	editingGreenGridPoints = false;
	editingGreenGridLines = b;
	objectRenderer.setEditingGridLines(editingGreenGridLines);
	setEditingGreenGridShape(b);
    }

    private void setEditingGreenGridPoints(boolean b) {
	editingGreenGridLines = false;
	editingGreenGridPoints = b;
	setEditingGreenGridShape(b);
    }

    public boolean isEditingGreenGridLines() {
	return editingGreenGridLines;
    }

    public boolean isEditingGreenGridPoints() {
	return editingGreenGridPoints;
    }

    private void setEditingGreenGridShape(boolean b) {
        if(editingGreenGridLines || editingGreenGridPoints) {
            setEditingShapePoints(false);
	    setMovingShape(false);
            frontOfGreenButton.setSelected(false);
            middleOfGreenButton.setSelected(false);
            backOfGreenButton.setSelected(false);

	    gridCenterButton.setSelected(editingGreenGridLines);
	    editGridPointsButton.setSelected(editingGreenGridPoints);

            if(selectedObject instanceof DrawableGreen) {
                DrawableGreen green = (DrawableGreen) selectedObject;
		List<Coordinates> points;
		ObjectType type;
		if(editingGreenGridPoints) {
		    if(green.gridPoints == null)
			green.gridPoints = new ArrayList<Coordinates>();
		    points = green.gridPoints;
		    type = ObjectType.PIN_LOCATIONS;
		}
		else {
		    if(green.gridLines == null)
			green.gridLines = new ArrayList<Coordinates>();
		    points = green.gridLines;
		    type = ObjectType.GREEN_GRID;
		}

                if(points != null) // && green.gridPoints.size() > 2)
                    greenGridShape = new DrawableCourseShape(null, null, green,  //ObjectType.GREEN_GRID, green.gridPoints.subList(2, green.gridPoints.size()),
			    type, points, true, 0, null, null);
                else
                    greenGridShape = null;
            }
            else
                greenGridShape = null;

	    mouseListener.setOperation(DrawingToolMouseListener.Operation.EDIT_SHAPE);
        }
        else {
	    gridCenterButton.setSelected(false);
            editGridPointsButton.setSelected(false);

            greenGridShape = null;
	    mouseListener.setOperation(DrawingToolMouseListener.Operation.NONE);
	    objectRenderer.setEditingShape(false);
	    objectRenderer.setEditingGridLines(false);
        }

    }

    private void setMovingShape(boolean b) {
        if(b) {
            setEditingGreenGridPoints(false);
	    setEditingShapePoints(false);
            frontOfGreenButton.setSelected(false);
            middleOfGreenButton.setSelected(false);
            backOfGreenButton.setSelected(false);
            gridCenterButton.setSelected(false);
        }
        else
            moveShapeButton.setSelected(false);

        if(mouseListener != null) {
            mouseListener.setOperation(b?
		DrawingToolMouseListener.Operation.MOVE_SHAPE:
		DrawingToolMouseListener.Operation.NONE);
        }
    }

    /**
     * Starts the selection of a Green point: front, middle, back, grid center
     * or grid direction, or none, depending on the selected button
     */
    public void pickGreenPointLocation(ActionEvent e) {
        if(selectedObject instanceof DrawableGreen == false)
            throw new IllegalStateException("pickGreenPointLocation when modifiedObject is not a Green");

        setEditingShapePoints(false);
        setEditingGreenGridPoints(false);
	setMovingShape(false);

        if(e.getSource() != frontOfGreenButton)
            frontOfGreenButton.setSelected(false);
        if(e.getSource() != middleOfGreenButton)
            middleOfGreenButton.setSelected(false);
        if(e.getSource() != backOfGreenButton)
            backOfGreenButton.setSelected(false);
        if(e.getSource() != gridCenterButton)
            gridCenterButton.setSelected(false);

        if(mouseListener != null)
            mouseListener.setOperation(
		    frontOfGreenButton.isSelected() ||
                    middleOfGreenButton.isSelected() ||
                    backOfGreenButton.isSelected() ||
                    gridCenterButton.isSelected()?
			DrawingToolMouseListener.Operation.EDIT_POINT:
			DrawingToolMouseListener.Operation.NONE);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editObjectPanel = new org.jdesktop.swingx.JXPanel();
        typeLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        editObjectSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        nameField = new javax.swing.JTextField() {
            //    public Color getForeground() {
                //        return Color.LIGHT_GRAY;
                //    }
        }
        ;
        typeCombo = new javax.swing.JComboBox();
        taskPaneContainer = new org.jdesktop.swingx.JXTaskPaneContainer();
        locationPanel = new org.jdesktop.swingx.JXTaskPane();
        locationPanel2 = new org.jdesktop.swingx.JXPanel();
        locationField = new javax.swing.JTextField();
        pickLocationButton = new javax.swing.JToggleButton();
        shapeEditPanel = new org.jdesktop.swingx.JXTaskPane();
        editShapeButton = new javax.swing.JToggleButton();
        moveShapeButton = new javax.swing.JToggleButton();
        clearShapeButton = new javax.swing.JButton();
        shapeEditPanel2 = new org.jdesktop.swingx.JXPanel();
        colorLabel = new javax.swing.JLabel();
        opacityLabel = new javax.swing.JLabel();
        opacitySlider = new javax.swing.JSlider();
        colorComboBox = new com.stayprime.ui.ColorPickerComboBox();
        messagePanel = new org.jdesktop.swingx.JXPanel();
        messageScrollPane = new javax.swing.JScrollPane();
        messageArea = new javax.swing.JTextArea() {
            //    public Color getForeground() {
                //        return Color.LIGHT_GRAY;
                //    }
        };
        messageSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        greenPointsTaskPane = new org.jdesktop.swingx.JXTaskPane();
        frontOfGreenButton = new javax.swing.JToggleButton();
        middleOfGreenButton = new javax.swing.JToggleButton();
        backOfGreenButton = new javax.swing.JToggleButton();
        gridPointsTaskPane = new org.jdesktop.swingx.JXTaskPane();
        gridCenterButton = new javax.swing.JToggleButton();
        editGridPointsButton = new javax.swing.JToggleButton();

        setName("Form"); // NOI18N

        editObjectPanel.setName("editObjectPanel"); // NOI18N

        typeLabel.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(DrawingToolEditorPanel.class);
        typeLabel.setText(resourceMap.getString("typeLabel.text")); // NOI18N
        typeLabel.setName("typeLabel"); // NOI18N

        nameLabel.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        nameLabel.setText(resourceMap.getString("nameLabel.text")); // NOI18N
        nameLabel.setName("nameLabel"); // NOI18N

        editObjectSeparator.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        editObjectSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editObjectSeparator.setTitle(resourceMap.getString("editObjectSeparator.title")); // NOI18N
        editObjectSeparator.setName("editObjectSeparator"); // NOI18N

        nameField.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        nameField.setName("nameField"); // NOI18N

        typeCombo.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        typeCombo.setName("typeCombo"); // NOI18N

        javax.swing.GroupLayout editObjectPanelLayout = new javax.swing.GroupLayout(editObjectPanel);
        editObjectPanel.setLayout(editObjectPanelLayout);
        editObjectPanelLayout.setHorizontalGroup(
            editObjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editObjectSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(editObjectPanelLayout.createSequentialGroup()
                .addGroup(editObjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(typeLabel)
                    .addComponent(nameLabel))
                .addGap(5, 5, 5)
                .addGroup(editObjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(typeCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nameField)))
        );
        editObjectPanelLayout.setVerticalGroup(
            editObjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editObjectPanelLayout.createSequentialGroup()
                .addComponent(editObjectSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editObjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editObjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        taskPaneContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 0, 0, 0));
        taskPaneContainer.setOpaque(false);
        taskPaneContainer.setName("taskPaneContainer"); // NOI18N
        org.jdesktop.swingx.VerticalLayout verticalLayout5 = new org.jdesktop.swingx.VerticalLayout();
        verticalLayout5.setGap(4);
        taskPaneContainer.setLayout(verticalLayout5);

        locationPanel.setTitle(resourceMap.getString("locationPanel.title")); // NOI18N
        locationPanel.setFont(getFont().deriveFont(getFont().getSize()-2f));
        locationPanel.setName("locationPanel"); // NOI18N

        locationPanel2.setName("locationPanel2"); // NOI18N

        locationField.setEditable(false);
        locationField.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        locationField.setName("locationField"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(DrawingToolEditorPanel.class, this);
        pickLocationButton.setAction(actionMap.get("pickObjectLocation")); // NOI18N
        pickLocationButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        pickLocationButton.setName("pickLocationButton"); // NOI18N

        javax.swing.GroupLayout locationPanel2Layout = new javax.swing.GroupLayout(locationPanel2);
        locationPanel2.setLayout(locationPanel2Layout);
        locationPanel2Layout.setHorizontalGroup(
            locationPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(locationField, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
            .addComponent(pickLocationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
        );
        locationPanel2Layout.setVerticalGroup(
            locationPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(locationPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(locationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pickLocationButton))
        );

        locationPanel.getContentPane().add(locationPanel2);

        taskPaneContainer.add(locationPanel);

        shapeEditPanel.setTitle(resourceMap.getString("shapeEditPanel.title")); // NOI18N
        shapeEditPanel.setFont(getFont().deriveFont(getFont().getSize()-2f));
        shapeEditPanel.setMinimumSize(new java.awt.Dimension(149, 211));
        shapeEditPanel.setName("shapeEditPanel"); // NOI18N

        editShapeButton.setAction(actionMap.get("editShapePoints")); // NOI18N
        editShapeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        editShapeButton.setName("editShapeButton"); // NOI18N
        shapeEditPanel.getContentPane().add(editShapeButton);

        moveShapeButton.setAction(actionMap.get("moveShape")); // NOI18N
        moveShapeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        moveShapeButton.setName("moveShapeButton"); // NOI18N
        shapeEditPanel.getContentPane().add(moveShapeButton);

        clearShapeButton.setAction(actionMap.get("clearShape")); // NOI18N
        clearShapeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        clearShapeButton.setName("clearShapeButton"); // NOI18N
        shapeEditPanel.getContentPane().add(clearShapeButton);

        shapeEditPanel2.setName("shapeEditPanel2"); // NOI18N

        colorLabel.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        colorLabel.setText(resourceMap.getString("colorLabel.text")); // NOI18N
        colorLabel.setName("colorLabel"); // NOI18N

        opacityLabel.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        opacityLabel.setText(resourceMap.getString("opacityLabel.text")); // NOI18N
        opacityLabel.setName("opacityLabel"); // NOI18N

        opacitySlider.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        opacitySlider.setName("opacitySlider"); // NOI18N
        opacitySlider.setPreferredSize(new java.awt.Dimension(0, 17));

        colorComboBox.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        colorComboBox.setName("colorComboBox"); // NOI18N

        javax.swing.GroupLayout shapeEditPanel2Layout = new javax.swing.GroupLayout(shapeEditPanel2);
        shapeEditPanel2.setLayout(shapeEditPanel2Layout);
        shapeEditPanel2Layout.setHorizontalGroup(
            shapeEditPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shapeEditPanel2Layout.createSequentialGroup()
                .addComponent(opacityLabel)
                .addGap(18, 18, 18)
                .addComponent(opacitySlider, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(shapeEditPanel2Layout.createSequentialGroup()
                .addComponent(colorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
        );
        shapeEditPanel2Layout.setVerticalGroup(
            shapeEditPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shapeEditPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(shapeEditPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colorLabel)
                    .addComponent(colorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shapeEditPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(opacitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(opacityLabel))
                .addContainerGap())
        );

        shapeEditPanel.getContentPane().add(shapeEditPanel2);

        messagePanel.setName("messagePanel"); // NOI18N

        messageScrollPane.setName("messageScrollPane"); // NOI18N

        messageArea.setColumns(1);
        messageArea.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        messageArea.setLineWrap(true);
        messageArea.setRows(3);
        messageArea.setWrapStyleWord(true);
        messageArea.setName("messageArea"); // NOI18N
        messageScrollPane.setViewportView(messageArea);

        messageSeparator.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        messageSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        messageSeparator.setTitle(resourceMap.getString("messageSeparator.title")); // NOI18N
        messageSeparator.setName("messageSeparator"); // NOI18N

        javax.swing.GroupLayout messagePanelLayout = new javax.swing.GroupLayout(messagePanel);
        messagePanel.setLayout(messagePanelLayout);
        messagePanelLayout.setHorizontalGroup(
            messagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(messageSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
            .addComponent(messageScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
        );
        messagePanelLayout.setVerticalGroup(
            messagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(messagePanelLayout.createSequentialGroup()
                .addComponent(messageSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(messageScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        shapeEditPanel.getContentPane().add(messagePanel);

        taskPaneContainer.add(shapeEditPanel);

        greenPointsTaskPane.setTitle(resourceMap.getString("greenPointsTaskPane.title")); // NOI18N
        greenPointsTaskPane.setFont(getFont().deriveFont(getFont().getSize()-2f));
        greenPointsTaskPane.setName("greenPointsTaskPane"); // NOI18N

        frontOfGreenButton.setAction(actionMap.get("pickFrontOfGreen")); // NOI18N
        frontOfGreenButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        frontOfGreenButton.setName("frontOfGreenButton"); // NOI18N
        greenPointsTaskPane.getContentPane().add(frontOfGreenButton);

        middleOfGreenButton.setAction(actionMap.get("pickMiddleOfGreen")); // NOI18N
        middleOfGreenButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        middleOfGreenButton.setName("middleOfGreenButton"); // NOI18N
        greenPointsTaskPane.getContentPane().add(middleOfGreenButton);

        backOfGreenButton.setAction(actionMap.get("pickBackOfGreen")); // NOI18N
        backOfGreenButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        backOfGreenButton.setName("backOfGreenButton"); // NOI18N
        greenPointsTaskPane.getContentPane().add(backOfGreenButton);

        taskPaneContainer.add(greenPointsTaskPane);

        gridPointsTaskPane.setTitle(resourceMap.getString("gridPointsTaskPane.title")); // NOI18N
        gridPointsTaskPane.setFont(getFont().deriveFont(getFont().getSize()-2f));
        gridPointsTaskPane.setName("gridPointsTaskPane"); // NOI18N

        gridCenterButton.setAction(actionMap.get("pickGridCenter")); // NOI18N
        gridCenterButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        gridCenterButton.setName("gridCenterButton"); // NOI18N
        gridPointsTaskPane.getContentPane().add(gridCenterButton);

        editGridPointsButton.setAction(actionMap.get("editGridPoints")); // NOI18N
        editGridPointsButton.setFont(getFont().deriveFont(getFont().getSize()+fontIncrease));
        editGridPointsButton.setName("editGridPointsButton"); // NOI18N
        gridPointsTaskPane.getContentPane().add(editGridPointsButton);

        taskPaneContainer.add(gridPointsTaskPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editObjectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(taskPaneContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(editObjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taskPaneContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton backOfGreenButton;
    private javax.swing.JButton clearShapeButton;
    private com.stayprime.ui.ColorPickerComboBox colorComboBox;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JToggleButton editGridPointsButton;
    private org.jdesktop.swingx.JXPanel editObjectPanel;
    private org.jdesktop.swingx.JXTitledSeparator editObjectSeparator;
    private javax.swing.JToggleButton editShapeButton;
    private javax.swing.JToggleButton frontOfGreenButton;
    private org.jdesktop.swingx.JXTaskPane greenPointsTaskPane;
    private javax.swing.JToggleButton gridCenterButton;
    private org.jdesktop.swingx.JXTaskPane gridPointsTaskPane;
    private javax.swing.JTextField locationField;
    private org.jdesktop.swingx.JXTaskPane locationPanel;
    private org.jdesktop.swingx.JXPanel locationPanel2;
    private javax.swing.JTextArea messageArea;
    private org.jdesktop.swingx.JXPanel messagePanel;
    private javax.swing.JScrollPane messageScrollPane;
    private org.jdesktop.swingx.JXTitledSeparator messageSeparator;
    private javax.swing.JToggleButton middleOfGreenButton;
    private javax.swing.JToggleButton moveShapeButton;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel opacityLabel;
    private javax.swing.JSlider opacitySlider;
    private javax.swing.JToggleButton pickLocationButton;
    private org.jdesktop.swingx.JXTaskPane shapeEditPanel;
    private org.jdesktop.swingx.JXPanel shapeEditPanel2;
    private org.jdesktop.swingx.JXTaskPaneContainer taskPaneContainer;
    private javax.swing.JComboBox typeCombo;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
	if(this.dashboard != null) {
	    this.dashboard.removeMouseListener(mouseListener);
	    this.dashboard.removeMouseMotionListener(mouseListener);
	}

        if(dashboard != null) {
            objectRenderer.setComponent(dashboard);
            objectRenderer.setGridSystemNumberOfGrids(gridSystemNumberOfGrids);

            mouseListener.setDashboard(dashboard);
            dashboard.addMouseListener(mouseListener);
            dashboard.addMouseMotionListener(mouseListener);
        }

	this.dashboard = dashboard;
    }

    public void setDashboardObjectRenderer(DashboardObjectRenderer objectRenderer) {
        this.objectRenderer.setObjectRenderer(objectRenderer);
    }

    public DrawingToolObjectRenderer getObjectRenderer() {
	return objectRenderer;
    }

    public DrawingToolMouseListener getMouseListener() {
	return mouseListener;
    }

    public void setGridSystemNumberOfGrids(int gridSystemNumberOfGrids) {
        this.gridSystemNumberOfGrids = gridSystemNumberOfGrids;

        if(objectRenderer != null)
            objectRenderer.setGridSystemNumberOfGrids(gridSystemNumberOfGrids);
    }

}
