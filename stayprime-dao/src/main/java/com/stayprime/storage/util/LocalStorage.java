package com.stayprime.storage.util;

import com.aeben.golfclub.GolfClub;
import com.google.gson.reflect.TypeToken;
import com.stayprime.storage.domain.ReportedUnit;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.hibernate.entities.CartUnit;
import com.stayprime.hibernate.entities.Clients;
import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.hibernate.entities.CourseSettings;
import com.stayprime.hibernate.entities.Courses;
import com.stayprime.hibernate.entities.FnbOrder;
import com.stayprime.hibernate.entities.HutsInfo;
import com.stayprime.hibernate.entities.MenuItems;
import com.stayprime.hibernate.entities.PinLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Keeps local cache of database entities and pending ReportedUnits.
 *
 * @author benjamin
 */
public class LocalStorage {

    static final TypeToken<List<CartInfo>> cartInfoToken = new TypeToken<List<CartInfo>>() {
    };
    static final TypeToken<List<CartUnit>> cartUnitToken = new TypeToken<List<CartUnit>>() {
    };
    static final TypeToken<List<ReportedUnit>> reportedUnitToken = new TypeToken<List<ReportedUnit>>() {
    };

    static final TypeToken<List<Courses>> coursesToken = new TypeToken<List<Courses>>() {
    };
    static final TypeToken<List<PinLocation>> pinLocationsToken = new TypeToken<List<PinLocation>>() {
    };
    static final TypeToken<List<HutsInfo>> hutsInfoToken = new TypeToken<List<HutsInfo>>() {
    };
    static final TypeToken<List<FnbOrder>> fnbOrderToken = new TypeToken<List<FnbOrder>>() {
    };
    static final TypeToken<List<MenuItems>> menuItemsToken = new TypeToken<List<MenuItems>>() {
    };
    static final TypeToken<List<Clients>> clientsToken = new TypeToken<List<Clients>>() {
    };
    static final TypeToken<List<CourseSettings>> courseSettingToken = new TypeToken<List<CourseSettings>>() {
    };
    static final TypeToken<Map<String, String>> localTimestamps = new TypeToken<Map<String, String>>() {
    };

    private JsonStorage jsonStorage;

    public LocalStorage(String workDir) {
        jsonStorage = new JsonStorage(workDir);
        jsonStorage.mkDirs();
    }

    /*
     * Course settings and asset versions
     */
    public List<CourseSettings> listCourseSettings() {
        return jsonStorage.getList(CourseSettings.class, courseSettingToken);
    }

    public CourseSettings getCourseSetting(String name) {
        List<CourseSettings> list = jsonStorage.getList(CourseSettings.class, courseSettingToken);
        return list == null ? null : CourseSettings.findSetting(list, name);
    }

    public String getSettingValue(String name) {
        CourseSettings setting = getCourseSetting(name);
        return setting == null ? null : setting.getValue();
    }

    public long getSettingTimestamp(String name) {
        return NumberUtils.toLong(getSettingValue(name));
    }

    public Date getAssetUpdated(String key) {
        CourseSettings cs = getCourseSetting(key);
        return cs == null ? null : CourseSettings.getDateFromUnixTimestamp(cs);
    }

    public void saveCourseSettings(List<CourseSettings> cs) {
        jsonStorage.storeList(CourseSettings.class, cs);
    }

    /*
     * Site assets
     */
    public CourseInfo getCourseInfo() {
        return jsonStorage.getRoot(CourseInfo.class);
    }

    public GolfClub getGolfClub() {
        GolfClub gc = jsonStorage.getRoot(GolfClub.class);
        if (gc != null) {
            gc.setParents();
        }
        return gc;
    }

    public void saveCourseInfo(CourseInfo courseInfo) {
        jsonStorage.storeRoot(courseInfo);
    }

    public void saveGolfClub(GolfClub golfClub) {
        jsonStorage.storeRoot(golfClub);
    }

    /**
     * Lists golf courses.
     * @return a list of Courses, or EMPTY_LIST if no courses are found
     */
    public List<Courses> listCourses() {
        List<Courses> courses = jsonStorage.getList(Courses.class, coursesToken);
        if (courses == null) {
            return Collections.EMPTY_LIST;
        }
        for (Courses c : courses) {
            c.setParents();
        }
        return courses;
    }

    public void saveCourses(List<Courses> courses) {
        jsonStorage.storeList(Courses.class, courses);
    }

    public List<PinLocation> listPinLocations() {
        return jsonStorage.getList(PinLocation.class, pinLocationsToken);
    }

    public void savePinLocations(List<PinLocation> pinLocations) {
        jsonStorage.storeList(PinLocation.class, pinLocations);
    }

    /*
     * Ads
     */
    public List<Clients> listClients() {
        return jsonStorage.getList(Clients.class, clientsToken);
    }

    public void saveClients(List<Clients> clients) {
        System.out.println("Saving to JSON");
        jsonStorage.storeList(Clients.class, clients);
    }

    /*
     * F&B
     */
    public List<MenuItems> listMenuItems() {
        return jsonStorage.getList(MenuItems.class, menuItemsToken);
    }

    public void saveMenuItems(List<MenuItems> menuItems) {
        jsonStorage.storeList(MenuItems.class, menuItems);
    }

    public MenuItems getMenuItem(int itemId) {
        List<MenuItems> list = listMenuItems();
        return list == null ? null : MenuItems.findById(list, itemId);
    }

    public List<HutsInfo> listHutsInfo() {
        return jsonStorage.getList(HutsInfo.class, hutsInfoToken);
    }

    public void saveHutsInfo(List<HutsInfo> hutsInfo) {
        jsonStorage.storeList(HutsInfo.class, hutsInfo);
    }

    /*
     * CartInfo
     */
    public List<CartInfo> listCarts() {
        return jsonStorage.getList(CartInfo.class, cartInfoToken);
    }

    public CartInfo getCartInfo(String mac) {
        List<CartInfo> list = jsonStorage.getList(CartInfo.class, cartInfoToken);
        return CartInfo.findByMacAddress(list, mac);
    }

    public void saveCarts(List<CartInfo> carts) {
        jsonStorage.storeList(CartInfo.class, carts);
    }

    /*
     * CartUnit
     */
    public List<CartUnit> listCartUnits() {
        return jsonStorage.getList(CartUnit.class, cartUnitToken);
    }

    public CartUnit getCartUnit(String mac) {
        List<CartUnit> list = jsonStorage.getList(CartUnit.class, cartUnitToken);
        return CartUnit.find(list, mac);
    }

    public CartUnit findCartUnitByIpAddress(String ip) {
        List<CartUnit> list = jsonStorage.getList(CartUnit.class, cartUnitToken);
        return CartUnit.findByIpAddress(list, ip);
    }

    public void saveCartUnit(CartUnit unit) {
        jsonStorage.addToList(CartUnit.class, cartUnitToken, unit, true);
    }

    public void saveCartUnits(List<CartUnit> units) {
        jsonStorage.storeList(CartUnit.class, units);
    }

    /*
     * ReportedUnit
     */
    public List<ReportedUnit> listReportedUnits() {
        List<ReportedUnit> list = jsonStorage.getList(ReportedUnit.class, reportedUnitToken);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public ReportedUnit getReportedUnit(String mac) {
        List<ReportedUnit> list = jsonStorage.getList(ReportedUnit.class, reportedUnitToken);
        ReportedUnit ru = list == null ? null : ReportedUnit.findByMac(list, mac);
        return ru != null ? ru : new ReportedUnit(mac);
    }

    public void saveReportedUnit(ReportedUnit ru) {
        jsonStorage.addToList(ReportedUnit.class, reportedUnitToken, ru, true);
    }

    public void removeReportedUnit(ReportedUnit ru) {
        jsonStorage.removeFromList(ReportedUnit.class, reportedUnitToken, ru);
    }

    /*
     * Timestamps for sync
     */
    public long getTimestamp(String key) {
        Map<String, String> ts = getTimestamps();
        return ts == null ? 0 : NumberUtils.toLong(ts.get(key));
    }

    public Map<String, String> getTimestamps() {
        return jsonStorage.getList("Timestamps", LocalStorage.localTimestamps);
    }

    public void saveTimestamps(Map<String, String> timestamps) {
        jsonStorage.storeObject("Timestamps", timestamps);
    }

}
