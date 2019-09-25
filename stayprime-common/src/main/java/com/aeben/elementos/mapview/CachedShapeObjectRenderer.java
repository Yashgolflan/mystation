/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

/**
 *
 * @author benjamin
 */
public class CachedShapeObjectRenderer {
//	extends BasicObjectRenderer {
//    protected Map<DrawableObject, Pair<Coordinates, Shape>> objectsShapes;
//    protected MapImage oldMap;
//
//    public CachedShapeObjectRenderer() {
//        this(null);
//    }
//
//    public CachedShapeObjectRenderer(ImageConverter imageConverter) {
//        super(imageConverter);
//
//        objectsShapes = new HashMap<DrawableObject, Pair<Coordinates, Shape>>();
//    }
//
//    @Override
//    public void renderObjects(Graphics g, Projection p, boolean quickDraw) {
//	if(!p.getMap().equals(oldMap)) {
//	    objectsShapes.clear();
//	    oldMap = p.getMap();
//	}
//
//	super.renderObjects(g, p, quickDraw);
//    }
////    @Override
////    public void renderObjects(Graphics g, MapImage map, double scale, Rectangle2D imageRect, Dimension canvasSize, boolean quickDraw) {
////        if(courseObjects != null) {
////            Graphics2D g2d = (Graphics2D) g.create();
////
////            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
////                    quickDraw? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
////                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
////            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
////                    quickDraw? RenderingHints.VALUE_ANTIALIAS_OFF :
////                        RenderingHints.VALUE_ANTIALIAS_ON);
////
////            //TODO: pasar un imageRect desescalado y hacer la affinetransform
////            //desde aqui, pero tendría que ser desde BasicObjectRenderer y
////            //los strokeWidths serian escalados todos
////            for(DrawableObject object: courseObjects) {
////                renderCourseObject(object, g2d, map, scale, imageRect, canvasSize);
////            }
////
////            g2d.dispose();
////        }
////    }
//
//    @Override
//    public Shape renderCourseObject(DrawableObject object, Graphics2D g, Projection p) {
//        if(object instanceof DrawableShapeObject)
//            return renderCachedShapeObject((DrawableShapeObject) object, g, p);
//        else
//            return super.renderCourseObject(object, g, p);
//    }
//
//    public Shape renderCachedShapeObject(DrawableShapeObject object, Graphics2D g2d,
//	    Projection p) {
//
//	if(object.getShapeCoordinates() != null && object.getShapeCoordinates().size() > 0) {
//            Polygon2D.Float shape = getShape(object, p);
//
//            if(shape != null) {
////                AffineTransform originalTransform = g2d.getTransform();
//                try {
//		    Polygon2D.Float drawnShape = getTransformedShape(object, shape, scale, imageRect);
//
//                    g2d.setStroke(getStroke(object, 1));
//
//                    Color color;
//
//                    if(object.isClosedShape()) {
//                        color = getFillColor(object);
//                        if(color != null) {
//                            g2d.setColor(color);
//                            g2d.fill(drawnShape);
//                        }
//                    }
//
//                    color = getStrokeColor(object);
//                    if(color != null) {
//                        g2d.setColor(color);
//                        g2d.draw(drawnShape);
//                    }
//
//                    return drawnShape;
//                }
//                finally {
//                    //g2d.setTransform(originalTransform);
//                }
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public void setDrawableObjects(List<DrawableObject> courseObjects) {
//	objectsShapes.clear();
//        super.setDrawableObjects(courseObjects);
//    }
//
//    @Override
//    public void reset() {
//	objectsShapes.clear();
//	super.reset();
//    }
//
//    protected Polygon2D.Float getShape(DrawableShapeObject object, Projection p) {
//	Pair<Coordinates,Shape> coordShape = objectsShapes.get(object);
//	Polygon2D.Float shape;
//
//	if(coordShape == null || !(coordShape.secondItem instanceof Polygon2D.Float)) {
//	    if(object.isClosedShape())
//		shape = new Polygon2D.Float();
//	    else
//		shape = new PolyLine2D.Float();
////                        new Path2D.Float(Path2D.WIND_NON_ZERO, object.getShapeCoordinates().size());
//
////                Point2D point0 = CoordinateCalculations.getXYRelativeToMapPixelCoordinates(
////                        object.getShapeCoordinates().get(0),
////                        map.getTopLeftCoordinates(), map.getBottomRightCoordinates(),
////                        imageRect.getWidth()/scale, imageRect.getHeight()/scale);
////                shape.moveTo(point0.getX(), point0.getY());
//
//	    for(int i = 0; i < object.getShapeCoordinates().size(); i++) {
//		Rectangle2D rect = new Rectangle2D.Double(0, 0,
//			imageRect.getWidth()/scale, imageRect.getHeight()/scale);
//
//		Point2D point = CoordinateCalculations.getXYRelativeToMapPixelCoordinates(
//			object.getShapeCoordinates().get(i), map, rect);
//
//		shape.add(point);
////                      shape.lineTo(point.getX(), point.getY());
//	    }
//
//	    if(object.isClosedShape());
//	    //    shape.closePath();
//
//	    coordShape = new Pair<Coordinates, Shape>(object.getShapeCoordinates().get(0), shape);
//	    objectsShapes.put(object, coordShape);
//	}
//	else {
//	    shape = (Polygon2D.Float) coordShape.secondItem;
//	}
//
//	return shape;
//    }
//
//    public static Polygon2D.Float getTransformedShape(DrawableShapeObject object, Polygon2D.Float shape, double scale, Rectangle2D imageRect) {
//	Polygon2D.Float transformedShape;
//
//	AffineTransform transform = AffineTransform.getTranslateInstance(imageRect.getX(), imageRect.getY());
//	transform.concatenate(AffineTransform.getScaleInstance(scale, scale));
//
//	transformedShape = object.isClosedShape()?
//		new Polygon2D.Float() : new PolyLine2D.Float();
//	for(int i = 0; i < shape.npoints; i++) {
//	    Point2D.Float p = new Point2D.Float(shape.xpoints[i], shape.ypoints[i]);
//	    transformedShape.add(transform.transform(p, p));
//	}
//
//	return transformedShape;
//    }
}