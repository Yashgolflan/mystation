/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.services;

import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.DrawableCoursePoint;
import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.DrawableRestrictedZone;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.HoleDefinition;
import com.stayprime.geo.Coordinates;
import com.stayprime.hibernate.entities.Clients;
import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.hibernate.entities.CourseSettings;
import com.stayprime.hibernate.entities.Courses;
import com.stayprime.hibernate.entities.FnbOrder;
import com.stayprime.hibernate.entities.HoleObjects;
import com.stayprime.hibernate.entities.HoleObjectsId;
import com.stayprime.hibernate.entities.HutsInfo;
import com.stayprime.hibernate.entities.MenuItems;
import com.stayprime.hibernate.entities.PinLocation;
import com.stayprime.hibernate.entities.UserLogin;
import com.stayprime.localservice.Constants;
import com.stayprime.storage.repos.ClientsRepo;
import com.stayprime.storage.repos.CourseInfoRepo;
import com.stayprime.storage.repos.CourseSettingsRepo;
import com.stayprime.storage.repos.CoursesRepo;
import com.stayprime.storage.repos.FnbOrderRepo;
import com.stayprime.storage.repos.HoleObjectsRepo;
import com.stayprime.storage.repos.HutsInfoRepo;
import com.stayprime.storage.repos.MenuItemsRepo;
import com.stayprime.storage.repos.PinLocationRepo;
import com.stayprime.storage.repos.UserLoginRepo;
import com.stayprime.storage.util.LocalStorage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author benjamin
 */
@Service
public class CourseService {

    @Autowired
    LocalStorage localStorage;

    @Autowired
    UserLoginRepo userLoginRepo;

    @Autowired
    ClientsRepo clientsRepo;

    @Autowired
    HutsInfoRepo hutsInfoRepo;

    @Autowired
    MenuItemsRepo menuItemsRepo;

    @Autowired
    CourseInfoRepo courseInfoRepo;

    @Autowired
    CoursesRepo coursesRepo;

    @Autowired
    PinLocationRepo pinLocationRepo;

    @Autowired
    HoleObjectsRepo holeObjectsRepo;

    @Autowired
    CourseSettingsRepo courseSettingsRepo;
    
    @Autowired
    private InstantSyncService instantSyncService;
    
    @Autowired
    private FnbOrderRepo fnbOrderRepo;
    
    public UserLogin authUser(String user, String pass) {
        return userLoginRepo.findOneByUserNameAndPassword(user, pass);
    }

    public void saveUserLogin(UserLogin login) {
        userLoginRepo.save(login);
    }

    public GolfClub getGolfClub() {
        return localStorage.getGolfClub();
    }

    public CourseInfo getCourseInfo() {
        return localStorage.getCourseInfo();
    }

    public CourseInfo saveCourseInfo(CourseInfo courseInfo) {
        boolean delete0 = courseInfo.getCourseId() == 0;
        courseInfo.setCourseId(1);
        Date now = new Date();
        courseInfo.setUpdated(now);
        CourseInfo course = courseInfoRepo.save(courseInfo);
        if (delete0) courseInfoRepo.delete(0);
        assetUpdated(Constants.courseUpdated, now);
        return course;
    }

    public List<Courses> listCourses() {
        List<Courses> listCourses = localStorage.listCourses();
        return listCourses == null ? Collections.EMPTY_LIST : listCourses;
    }

    public void saveCourses(List<Courses> courses) {
//        List<Courses> oldCoursesList = localStorage.listCourses();
//        List<Courses> coursesToBeDeleted = new ArrayList<>();
//        for (Courses oldCourse : oldCoursesList) {
//            boolean found = false;
//            for (Courses course : courses) {
//                if (oldCourse.getNumber() == course.getNumber()) {
//                    found = true;
//                    break;
//                }
//            }
//            if (!found) {
//                coursesToBeDeleted.add(oldCourse);
//            }
//        }
//        if (!coursesToBeDeleted.isEmpty()) {
//            coursesRepo.delete(coursesToBeDeleted);
//        }        
        coursesRepo.save(courses);
        assetUpdated(Constants.courseUpdated, new Date());
    }

    public Date getPinsLastUpdated() {
        return localStorage.getAssetUpdated(Constants.pinsUpdated);
    }

    public void savePinLocations(List<PinLocation> pins) {
        pinLocationRepo.save(pins);
        assetUpdated(Constants.pinsUpdated, new Date());
    }

    public List<Clients> listClients() {
        return localStorage.listClients();
    }

    public void saveClients(ArrayList<Clients> clients) {
        clientsRepo.save(clients);
        assetUpdated(Constants.adsUpdated, new Date());
    }

    public void deleteClient(Clients client) {
        clientsRepo.delete(client);
        assetUpdated(Constants.adsUpdated, new Date());
    }

    public List<MenuItems> listMenuItems() {
        return localStorage.listMenuItems();
    }

    public void saveMenuItems(List<MenuItems> menuItems) {
        List<MenuItems> oldMenuItemsList = localStorage.listMenuItems();
        List<MenuItems> menuItemsToBeDeleted = new ArrayList<>();
        for (MenuItems oldMenuItem : oldMenuItemsList) {
            boolean found = false;
            for (MenuItems menuItem : menuItems) {
                if (menuItem.getItemId() == oldMenuItem.getItemId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                menuItemsToBeDeleted.add(oldMenuItem);
            }
        }
        if (!menuItemsToBeDeleted.isEmpty()) {
            menuItemsRepo.delete(menuItemsToBeDeleted);
        }
        menuItemsRepo.save(menuItems);
        assetUpdated(Constants.menuUpdated, new Date());
    }

    public List<HutsInfo> listHutsInfo() {
        return localStorage.listHutsInfo();
    }

    public void saveHutsInfo(List<HutsInfo> hutsList) {
        List<HutsInfo> oldHutsInfoList = localStorage.listHutsInfo();
        List<HutsInfo> hutsInfosToBeDeleted = new ArrayList<>();
        for (HutsInfo oldHutsInfo : oldHutsInfoList) {
            boolean found = false;
            for (HutsInfo hutsInfo : hutsList) {
                if (hutsInfo.getId() == oldHutsInfo.getId()) {
                    found = true;
                    break;
                } 
            }
            if (!found) {
                hutsInfosToBeDeleted.add(oldHutsInfo);
            }
        }
        if (!hutsInfosToBeDeleted.isEmpty()) {
            hutsInfoRepo.delete(hutsInfosToBeDeleted);
        }
        hutsInfoRepo.save(hutsList);
        assetUpdated(Constants.menuUpdated, new Date());
    }

    public List<CourseSettings> listSettings() {
        return localStorage.listCourseSettings();
    }

    public String getSettingValue(String key) {
//        CourseSettings cs = courseSettingsRepo.findOne(key);
//        return cs == null? null : cs.getValue();
        return localStorage.getSettingValue(key);
    }

    public void saveSetting(String key, String value) {
        saveSettings(new CourseSettings(key, value, new Date()));
    }

    @Transactional
    public void saveSettings(CourseSettings ... settings) {
        courseSettingsRepo.save(Arrays.asList(settings));
        // We save the updated timestamps for assets in CourseSettings table,
        // including courseSettingsUpdated.
        Date now = new Date();
        assetUpdated(Constants.courseSettingsUpdated, now);
    }

    private void assetUpdated(String asset, Date when) {
        String time = Long.toString(when.getTime());
        courseSettingsRepo.save(new CourseSettings(asset, time, when));
        if (!asset.equalsIgnoreCase(Constants.courseSettingsUpdated)) {
            courseSettingsRepo.save(new CourseSettings(Constants.courseSettingsUpdated, time, when));
        }       
    }

    public void createHoleObject(HoleDefinition hole, GolfCourseObject object) {
//        if(object.getType() == GolfCourseObject.ObjectType.CARTH_PATH && object != hole.cartPath)
//            throw new IllegalArgumentException("Cart path hole object must be == hole.cartPath");
        if (object.getType() == GolfCourseObject.ObjectType.CARTH_PATH ^ object.getId() == 0)
            throw new IllegalArgumentException("Hole object with id = 0 must be of type CART_PATH");
        if (object.getType() == GolfCourseObject.ObjectType.GREEN ^ object.getId() == 1)
            throw new IllegalArgumentException("Hole object with id = 1 must be of type GREEN");
        if (object.getType() == GolfCourseObject.ObjectType.FRONT_GREEN ^ object.getId() == 2)
            throw new IllegalArgumentException("Hole object with id = 2 is reserved for FRONT_GREEN");
        if (object.getType() == GolfCourseObject.ObjectType.MIDDLE_GREEN ^ object.getId() == 3)
            throw new IllegalArgumentException("Hole object with id = 3 is reserved for MIDDLE_GREEN");
        if (object.getType() == GolfCourseObject.ObjectType.BACK_GREEN ^ object.getId() == 4)
            throw new IllegalArgumentException("Hole object with id = 4 is reserved for BACK_GREEN");
        if (object.getType() == GolfCourseObject.ObjectType.GREEN_GRID ^ object.getId() == 5)
            throw new IllegalArgumentException("Hole object with id = 5 is reserved for GREEN_GRID");
        if (object.getType() == GolfCourseObject.ObjectType.PIN_LOCATIONS ^ object.getId() == 6)
            throw new IllegalArgumentException("Hole object with id = 6 is reserved for PIN_LOCATIONS");
        if (object.getType() == GolfCourseObject.ObjectType.APPROACH_LINE ^ object.getId() == 7)
            throw new IllegalArgumentException("Hole object with id = 7 is reserved for APPROACH_LINE");

        HoleObjects holeObject = new HoleObjects();
        HoleObjectsId holeObjectId = new HoleObjectsId();

        CourseDefinition course = (CourseDefinition) hole.getParentObject();
        holeObjectId.setCourse((short) course.getCourseNumber());   //course
        holeObjectId.setHole((short) hole.number);           //hole
        holeObjectId.setId(object.getId());        //id
        holeObject.setId(holeObjectId);
        holeObject.setType((short) object.getType().key);  //type
        holeObject.setName(object.getName());   //name

        switch (object.getType()) {
            case CARTH_PATH:
            case HOLE_ZONE:
            case HOLE_OUTLINE:
            case AD_ZONE:
            case CLUBHOUSE_ZONE:
            case CART_BARN:
            case ACTION_ZONE:
            case RESTRICTED_ZONE:
            case GREEN:
            case APPROACH_LINE:
            case GREEN_GRID:
            case PIN_LOCATIONS:
                if (object instanceof DrawableCourseShape) {
                    DrawableCourseShape shape = (DrawableCourseShape) object;

                    String shapeString = getShapeString(shape);
                    double alpha = shape.getFillColor() == null ? 0
                            : shape.getFillColor().getAlpha() / 255d;
                    int color = shape.fillColor == null ? 0 : shape.fillColor.getRGB();
                    boolean closedShape = shape.closedShape;

                    holeObject.setShape(shapeString);   //shape
                    holeObject.setShapeAlpha(alpha);         //shape_alpha (compatibility)
                    holeObject.setShowDistance(false);        //show_distance
                    holeObject.setShowShape(false);        //show_shape
                    holeObject.setClosedShape(closedShape);  //closed_shape
                    holeObject.setColor(color);            //color
                }
                else
                    throw new IllegalArgumentException("Object type " + object.getType()
                            + " must be an instance of DrawableCourseShape");

                if (object instanceof DrawableRestrictedZone) {
                    DrawableRestrictedZone actionZone = (DrawableRestrictedZone) object;
                    holeObject.setHitMessage(actionZone.message);    //hitmessage
                }
                else if (object instanceof DrawableGreen) {
                    DrawableGreen green = (DrawableGreen) object;
                    updateHoleObject(hole, 2, new DrawableCoursePoint("FrontGreen", 2, green.getParentObject(),
                            GolfCourseObject.ObjectType.FRONT_GREEN, green.front));
                    updateHoleObject(hole, 3, new DrawableCoursePoint("MiddleGreen", 3, green.getParentObject(),
                            GolfCourseObject.ObjectType.MIDDLE_GREEN, green.middle));
                    updateHoleObject(hole, 4, new DrawableCoursePoint("BackGreen", 4, green.getParentObject(),
                            GolfCourseObject.ObjectType.BACK_GREEN, green.back));
                    updateHoleObject(hole, 5, new DrawableCourseShape("GreenGrid", 5, green.getParentObject(),
                            GolfCourseObject.ObjectType.GREEN_GRID, green.gridLines, true, 0, null, null));
                    updateHoleObject(hole, 6, new DrawableCourseShape("PinLocations", 6, green.getParentObject(),
                            GolfCourseObject.ObjectType.PIN_LOCATIONS, green.gridPoints, true, 0, null, null));
                }
                break;
            case HAZARD:
            case FRONT_GREEN:
            case MIDDLE_GREEN:
            case BACK_GREEN:
            case REFERENCE:
                if (object instanceof DrawableCoursePoint) {
                    DrawableCoursePoint point = (DrawableCoursePoint) object;

                    String coords = point == null || point.coordinates == null
                            ? null : point.coordinates.toString();
                    boolean showShape = point == null ? false : point.getType() == GolfCourseObject.ObjectType.HAZARD;

                    holeObject.setLocation(coords);         //location
                    holeObject.setShowDistance(showShape);    //show_distance
                }
                else
                    throw new IllegalArgumentException("Object type " + object.getType()
                            + " must be an instance of DrawableCoursePoint");
                break;
            default:
                throw new IllegalArgumentException("Object type " + object.getType()
                        + " can't be a hole object");
        }
//            statement.executeUpdate();
        holeObjectsRepo.save(holeObject);
        assetUpdated(Constants.courseUpdated, new Date());
    }

    public void updateHoleObject(HoleDefinition hole, int objectId, GolfCourseObject object) {
//        if(objectId == 0 ^ object.getId() == 0)
//            throw new IllegalArgumentException("Can't update object. id = 0 is reserved for the hole cart path");
        if (object.getType() == GolfCourseObject.ObjectType.CARTH_PATH ^ object.getId() == 0)
            throw new IllegalArgumentException("Hole object with id = 0 must be of type CART_PATH");
        if (object.getType() == GolfCourseObject.ObjectType.GREEN ^ object.getId() == 1)
            throw new IllegalArgumentException("Hole object with id = 1 must be of type GREEN");
        if (object.getType() == GolfCourseObject.ObjectType.FRONT_GREEN ^ object.getId() == 2)
            throw new IllegalArgumentException("Hole object with id = 2 is reserved for FRONT_GREEN");
        if (object.getType() == GolfCourseObject.ObjectType.MIDDLE_GREEN ^ object.getId() == 3)
            throw new IllegalArgumentException("Hole object with id = 3 is reserved for MIDDLE_GREEN");
        if (object.getType() == GolfCourseObject.ObjectType.BACK_GREEN ^ object.getId() == 4)
            throw new IllegalArgumentException("Hole object with id = 4 is reserved for BACK_GREEN");
        if (object.getType() == GolfCourseObject.ObjectType.GREEN_GRID ^ object.getId() == 5)
            throw new IllegalArgumentException("Hole object with id = 5 is reserved for GREEN_GRID");
        if (object.getType() == GolfCourseObject.ObjectType.PIN_LOCATIONS ^ object.getId() == 6)
            throw new IllegalArgumentException("Hole object with id = 6 is reserved for PIN_LOCATIONS");
        if (object.getType() == GolfCourseObject.ObjectType.APPROACH_LINE ^ object.getId() == 7)
            throw new IllegalArgumentException("Hole object with id = 7 is reserved for APPROACH_LINE");

        deleteHoleObject(hole, objectId, true);
        createHoleObject(hole, object);
    }

    public void deleteHoleObject(HoleDefinition hole, int objectId) {
        deleteHoleObject(hole, objectId, false);
    }

    public void deleteHoleObject(HoleDefinition hole, int objectId, boolean updating) {
        if (objectId == 0 && updating == false)
            throw new IllegalArgumentException("Can't delete object. id = 0 is reserved for the hole cart path");

        CourseDefinition course = (CourseDefinition) hole.getParentObject();

        HoleObjectsId id = new HoleObjectsId();
        id.setCourse((short) course.getCourseNumber());
        id.setHole((short) hole.number);
        id.setId(objectId);

        HoleObjects holeObject = holeObjectsRepo.findOne(id);
        if (holeObject != null) {
            holeObjectsRepo.delete(holeObject);
            assetUpdated(Constants.courseUpdated, new Date());
        }
    }

    public static String getShapeString(DrawableCourseShape object) {
        if (object != null)
            return getShapeString(object.getShapeCoordinates());
        else
            return null;
    }

    public static String getShapeString(List<Coordinates> shapeCoordinates) {
        String shapeString = null;
        if (shapeCoordinates != null) {
            StringBuilder builder = new StringBuilder();
            for (Coordinates coord : shapeCoordinates) {
                builder.append(coord.toString());
                builder.append(';');
            }
            shapeString = builder.toString();
        }

        return shapeString;
    }

    public InstantSyncService getInstantSyncService() {
        return instantSyncService;
    }
    
    public List<FnbOrder> getFnbOrders() {
        return (List<FnbOrder>) fnbOrderRepo.findAll();
    }

}
