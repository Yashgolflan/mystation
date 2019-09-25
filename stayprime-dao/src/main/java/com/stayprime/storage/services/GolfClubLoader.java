/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.storage.services;

import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.DrawableCoursePoint;
import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.DrawablePinLocation;
import com.aeben.golfclub.DrawableRestrictedZone;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.HoleDefinition;
import com.aeben.golfclub.TeeBox;
import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.hibernate.entities.CourseSettings;
import com.stayprime.hibernate.entities.Courses;
import com.stayprime.hibernate.entities.DefaultTees;
import com.stayprime.hibernate.entities.HoleObjects;
import com.stayprime.hibernate.entities.Holes;
import com.stayprime.hibernate.entities.HolesId;
import com.stayprime.hibernate.entities.PinLocation;
import com.stayprime.hibernate.entities.TeeBoxes;
import com.stayprime.localservice.Constants;
import com.stayprime.storage.repos.CourseInfoRepo;
import com.stayprime.storage.repos.CourseSettingsRepo;
import com.stayprime.storage.repos.CoursesRepo;
import com.stayprime.storage.repos.DefaultTeesRepo;
import com.stayprime.storage.repos.HoleObjectsRepo;
import com.stayprime.storage.repos.HolesRepo;
import com.stayprime.storage.repos.PinLocationRepo;
import com.stayprime.storage.util.DomainUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author stayprime
 */
@Component
public class GolfClubLoader {

    @Autowired
    CourseInfoRepo courseInfoRepo;

    @Autowired
    CourseSettingsRepo courseSettingsRepo;

    @Autowired
    CoursesRepo coursesRepo;

    @Autowired
    DefaultTeesRepo defaultTeesRepo;

    @Autowired
    HolesRepo holesRepo;

    @Autowired
    HoleObjectsRepo holeObjectsRepo;

    @Autowired
    PinLocationRepo pinLocationRepo;

    public GolfClub loadFullGolfClubDefinition() {
        GolfClub golfClub = loadGolfClubDefinition();
        golfClub.setCourses(loadCourseDefinitions(golfClub));

        for (CourseDefinition course : golfClub.getCourses()) {
            List<HoleDefinition> holes = loadHoleDefinitions(course);
            for (HoleDefinition hole : holes) {
                if (hole.number <= course.getHoleCount())
                    course.setHoleNumber(hole.number, hole);
            }
        }

        loadPinLocation(golfClub);

        return golfClub;
    }

    public GolfClub loadGolfClubDefinition() {
        GolfClub golfClub = new GolfClub("");

        CourseInfo courseInfo = DomainUtil.getValidCourseInfo(courseInfoRepo.findAll());
        List<CourseSettings> settings = DomainUtil.toList(courseSettingsRepo.findAll());

        if(courseInfo == null) {
            return golfClub;
        }
        if (courseInfo.getSiteId() > 0) {
            golfClub.setSiteId(Integer.toString(courseInfo.getSiteId()));
        }
        if (StringUtils.isNotBlank(courseInfo.getName())) {
            golfClub.setName(courseInfo.getName());
        }
        if (StringUtils.isNotBlank(courseInfo.getLogoImage())) {
            golfClub.setLogoImage(courseInfo.getLogoImage());
        }

        //checking if any of the cordinates null
        if (StringUtils.isNotBlank(courseInfo.getMapImage())
                && StringUtils.isNotBlank(courseInfo.getCornerTopLeft())
                && StringUtils.isNotBlank(courseInfo.getCornerTopRight())
                && StringUtils.isNotBlank(courseInfo.getCornerBottomLeft())
                && StringUtils.isNotBlank(courseInfo.getCornerBottomRight())) {
            //Setting courseImage
            golfClub.setCourseImage(new BasicMapImage(courseInfo.getMapImage(),
                    new Coordinates(courseInfo.getCornerTopLeft()), new Coordinates(courseInfo.getCornerTopRight()),
                    new Coordinates(courseInfo.getCornerBottomLeft()), new Coordinates(courseInfo.getCornerBottomRight())));
        }
        else {
            golfClub.setCourseImage(null);
        }

        golfClub.setVersion(CourseSettings.findValue(settings, Constants.courseUpdated));
        golfClub.setWelcomeImage(CourseSettings.findValue(settings, Constants.welcomeImage));
        golfClub.setThankyouImage(CourseSettings.findValue(settings, Constants.thankyouImage));

        int units = courseInfo.getUnits();
        golfClub.setUnits(DistanceUnits.get(units & 1));
        golfClub.setUnitsSelectable((units & 2) > 0);
        golfClub.setCourseDescription(courseInfo.getDescription());
        golfClub.setContactInfo(courseInfo.getContactInfo());

        return golfClub;
    }

    public List<CourseDefinition> loadCourseDefinitions(GolfClub golfClub) {
        
        List<CourseDefinition> courses = new ArrayList();
        List<Courses> coursesList = DomainUtil.toList(coursesRepo.findAll());

        if(coursesList == null)
            return courses;

        for (Courses c : coursesList)
            courses.add(loadCourseDefinitionFromObjects(c, golfClub));

        return courses;
    }

    public CourseDefinition loadCourseDefinition(GolfClub golfClub, int courseNumber) {

        Courses courses = coursesRepo.findOne(courseNumber);

        if(courses == null)
            return null;

        return loadCourseDefinitionFromObjects(courses, golfClub);
    }

    private CourseDefinition loadCourseDefinitionFromObjects(Courses c, GolfClub golfClub) {

        CourseDefinition course = new CourseDefinition(golfClub, c.getNumber(), c.getName(), c.getHoles());
        List<TeeBox> teeBoxes = new ArrayList();
        List<DefaultTees> list = defaultTeesRepo.findById_Course(course.getCourseNumber());

        for (DefaultTees defaultTees : list) {
            teeBoxes.add(new TeeBox((int) defaultTees.getId().getNumber(), null, defaultTees.getName(), new Color(defaultTees.getColor()),""));
        }
        course.setTeeBoxes(teeBoxes);
        return course;
    }

    public List<HoleDefinition> loadHoleDefinitions(CourseDefinition course) {

        if(course == null)
            return null;

        List<HoleDefinition> holes = new ArrayList();
        List<Holes> holesList = holesRepo.findById_Course(course.getCourseNumber());

        if(holesList == null)
            return holes;

        for (Holes h : holesList) {
            HoleDefinition hole = loadHoleDefinition(course, h.getId().getHole());
            holes.add(hole);
        }
        return holes;
    }

    public HoleDefinition loadHoleDefinition(CourseDefinition course, int holeNumber) {
        Holes h = holesRepo.findOne(new HolesId(course.getCourseNumber(), holeNumber));

        HoleDefinition hole = loadHoleDefinition(course, holeNumber, h);
        loadHoleObjects(hole);
        loadTeeBoxes(hole, h);
        return hole;
    }

    private HoleDefinition loadHoleDefinition(CourseDefinition course, int holeNumber, Holes h) {
        HoleDefinition hole = new HoleDefinition(course, holeNumber);
        if (hole.number != h.getId().getHole())
            throw new IllegalArgumentException("Hole number is different to the hole definition");

        hole.par = h.getPar();
        hole.paceSeconds = h.getPaceOfPlay();
        hole.cartPathOnly = h.isCartPathOnly();
        hole.proTips = h.getProTips();
        hole.flyover = h.getFlyoverImage();
        hole.lastUpdated = h.getUpdated();
        hole.strokeIndex = h.getStrokeIndex();

        if (h.getMapImage() != null) {
            hole.map = new BasicMapImage(h.getMapImage(),
                    new Coordinates(h.getCornerTopLeft()),
                    new Coordinates(h.getCornerTopRight()),
                    new Coordinates(h.getCornerBottomLeft()),
                    new Coordinates(h.getCornerBottomRight()));
        }
        else {
            hole.map = null;
        }
            return hole;
    }

    private void loadHoleObjects(HoleDefinition hole) {

        List<HoleObjects> holeObjectsList = holeObjectsRepo.findById_CourseAndId_Hole(hole.course.getCourseNumber(), hole.number);

        for (HoleObjects holeObject : holeObjectsList) {

            GolfCourseObject.ObjectType type = GolfCourseObject.ObjectType.getType(holeObject.getType());

            String name = holeObject.getName();

            Integer id = holeObject.getId().getId();

            switch (type) {
                case CLUBHOUSE_ZONE:
                case CART_BARN:
                case HOLE_ZONE:
                case HOLE_OUTLINE:
                case AD_ZONE:
                //TODO: special object for this, that identifies the TeeBox??
                case GREEN:
                case APPROACH_LINE:
                case GREEN_GRID:
                case PIN_LOCATIONS:
                case ACTION_ZONE:
                case RESTRICTED_ZONE:
                case CARTH_PATH:
                    DrawableCourseShape object;
                    if (type == GolfCourseObject.ObjectType.ACTION_ZONE
                            || type == GolfCourseObject.ObjectType.RESTRICTED_ZONE) {
                        DrawableRestrictedZone actionZone = new DrawableRestrictedZone(name, id, hole);
                        actionZone.message = holeObject.getHitMessage();
                        object = actionZone;
                    }
                    else if (type == GolfCourseObject.ObjectType.GREEN) {
                        object = new DrawableGreen("Green #" + hole.number, 1, hole,
                                new ArrayList<Coordinates>(), true, 2f,
                                new Color(0x8800FF00, true), new Color(0x8800FF00, true));
                    }
                    else if (type == GolfCourseObject.ObjectType.APPROACH_LINE) {
                        object = new DrawableCourseShape("Approach #" + hole.number, 7, hole,
                                GolfCourseObject.ObjectType.APPROACH_LINE,
                                new ArrayList<Coordinates>(), false, 2f,
                                new Color(0x8800FF00, true), new Color(0x8800FF00, true));
                    }
                    else if (type == GolfCourseObject.ObjectType.HOLE_OUTLINE) {
                        object = new DrawableCourseShape("Hole #" + hole.number, id, hole,
                                GolfCourseObject.ObjectType.HOLE_OUTLINE,
                                new ArrayList<Coordinates>(), true, 2f,
                                new Color(0x88000000, true), new Color(0x44000000, true));
                    }
                    else {
                        object = new DrawableCourseShape(name, id, hole);
                    }

                    object.type = type;

                    if (holeObject.getColor() != null) {
                        object.fillColor = new Color(holeObject.getColor(), true);
                        if (holeObject.getShapeAlpha() != null) {
                            double shapeAlpha = holeObject.getShapeAlpha();
                            int argb = object.fillColor.getRGB() & 0xffffff | (int) (shapeAlpha * 255) << 24;
                            object.fillColor = new Color(argb, true);
                            object.strokeColor = object.fillColor;
                        }
                    }

                    String shapeString = holeObject.getShape();
                    object.shapeCoordinates = new ArrayList();
                    if (shapeString != null && shapeString.length() > 0) {
                        String coords[] = shapeString.split(";");
                        if (coords.length > 0) {
                            for (String coord : coords) {
                                object.shapeCoordinates.add(new Coordinates(coord));
                            }
                        }
                    }

                    if (type == GolfCourseObject.ObjectType.CARTH_PATH) {
                        object.strokeColor = object.fillColor;
                        object.strokeWidth = 8;
                        object.closedShape = false;
                        hole.cartPath = object;
                    }
                    else if (type == GolfCourseObject.ObjectType.HOLE_OUTLINE) {
                        hole.setHoleOutline(object);
                    }
                    else if (type == GolfCourseObject.ObjectType.GREEN) {
                        hole.green = (DrawableGreen) object;
                    }
                    else if (type == GolfCourseObject.ObjectType.APPROACH_LINE) {
                        hole.setApproachLine((DrawableCourseShape) object);
                    }
                    else if (type == GolfCourseObject.ObjectType.GREEN_GRID) {
                        if (hole.green != null)
                            hole.green.gridLines = object.shapeCoordinates;
                    }
                    else if (type == GolfCourseObject.ObjectType.PIN_LOCATIONS) {
                        if (hole.green != null)
                            hole.green.gridPoints = object.shapeCoordinates;
                    }
                    else {
                        hole.getShapes().add(object);
                    }
                    break;
                case BACK_GREEN:
                case FRONT_GREEN:
                case MIDDLE_GREEN:
                    if (hole.green == null) {
                        hole.green = new DrawableGreen("Green #" + hole.number, 1, hole,
                                new ArrayList<Coordinates>(), true, 2f,
                                new Color(0x8800FF00, true), new Color(0x8800FF00, true));
                    }

                    Coordinates location = null;
                    if (holeObject.getLocation() != null)
                        location = new Coordinates(holeObject.getLocation());

                    if (type == GolfCourseObject.ObjectType.FRONT_GREEN)
                        hole.green.front = location;
                    else if (type == GolfCourseObject.ObjectType.MIDDLE_GREEN)
                        hole.green.middle = location;
                    else if (type == GolfCourseObject.ObjectType.BACK_GREEN)
                        hole.green.back = location;
                    break;
                case HAZARD:
                case REFERENCE:
                    DrawableCoursePoint point = new DrawableCoursePoint(name, id, hole);
                    point.type = type;
                    if (holeObject.getLocation() != null)
                        point.coordinates = new Coordinates(holeObject.getLocation());
                    hole.getPoints().add(point);
                    break;
                case PINFLAG:
                    DrawablePinLocation flag = new DrawablePinLocation("Pin " + hole.number, hole.number, hole);
                    flag.type = GolfCourseObject.ObjectType.PINFLAG;
                    if (holeObject.getLocation() != null)
                        flag.coordinates = new Coordinates(holeObject.getLocation());
                    hole.pinLocation = flag;
            }
        }
    }

    private void loadTeeBoxes(HoleDefinition hole, Holes h) {

        List<TeeBoxes> list = h.getTeeBoxes();

        if (hole.teeBoxes == null)
            hole.teeBoxes = new ArrayList();
        else
            hole.teeBoxes.clear();

        if (list == null || list.isEmpty())
               return;

        for (TeeBoxes tee : list) {
            int number = tee.getNumber();
            String name = String.valueOf(number);
            int color = tee.getColor();
            String distance = tee.getDistance();
           // int distance=tee.getDistance();

            //TODO need to check name:
            TeeBox teeBox = new TeeBox("Tee box " + number, number, hole);
            teeBox.color = new Color(color);
            teeBox.distanceToHole = distance;
            hole.teeBoxes.add(teeBox);

          }
    }

    public boolean loadPinLocation(GolfClub courseInfo) {
        List<PinLocation> pinLocationList = DomainUtil.toList(pinLocationRepo.findAll());
        return PinLocation.loadPinLocation(courseInfo, pinLocationList);
    }

}
