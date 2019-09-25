/*
 *
 */
package com.stayprime.storage.services;

import com.stayprime.localservice.Constants;
import com.aeben.golfclub.GolfClub;
import com.stayprime.hibernate.entities.Clients;
import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.hibernate.entities.CourseSettings;
import com.stayprime.hibernate.entities.Courses;
import com.stayprime.hibernate.entities.HutsInfo;
import com.stayprime.hibernate.entities.MenuItems;
import com.stayprime.hibernate.entities.PinLocation;
import com.stayprime.storage.repos.CourseInfoRepo;
import com.stayprime.storage.repos.CourseSettingsRepo;
import com.stayprime.storage.repos.HutsInfoRepo;
import com.stayprime.storage.repos.PinLocationRepo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.stayprime.storage.repos.ClientsRepo;
import com.stayprime.storage.repos.CoursesRepo;
import com.stayprime.storage.repos.MenuItemsRepo;
import com.stayprime.storage.util.DomainUtil;
import com.stayprime.storage.util.LocalStorage;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author benjamin
 */
@Component
public class AssetSyncService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    LocalStorage localStorage;

    @Autowired
    GolfClubLoader golfClubLoader;

    @Autowired
    EntityManager entityManager;

    @Autowired
    CourseInfoRepo courseInfoRepo;

    @Autowired
    CoursesRepo coursesRepo;

    @Autowired
    PinLocationRepo pinLocationRepo;

    @Autowired
    CourseSettingsRepo courseSettingsRepo;

    @Autowired
    MenuItemsRepo menuItemRepo;

    @Autowired
    HutsInfoRepo hutsInfoRepo;

    @Autowired
    private ClientsRepo clientRepo;

    private long lastCourseSync;

    public long getLastCourseSync() {
        return lastCourseSync;
    }

    @Transactional
    public void fixCourseInfoId() {
        List<CourseInfo> list = DomainUtil.toList(courseInfoRepo.findAll());
        if (list.size() == 1) {
            CourseInfo c = list.get(0);
            if (c.getCourseId() == 0) {
                entityManager.detach(c);
                c.setCourseId(1);
                courseInfoRepo.save(c);
                courseInfoRepo.delete(0);
            }
        }
        else if (list.size() == 2) {
            int id0 = list.get(0).getCourseId();
            int id1 = list.get(1).getCourseId();
            if ((id0 == 0 || id1 == 0) && (id0 == 1 || id1 == 1)) {
                courseInfoRepo.delete(0);
            }
        }
    }

    public void syncAssets() {
        log.info("Syncing assets");
        checkCourseSettingsUpdated();
        checkCourseInfoUpdated();
        checkPinsUpdated();
        checkAdsUpdated();
        checkFnbUpdated();
    }

    private void checkCourseSettingsUpdated() {
        long localTimestamp = getLocalTimestamp(Constants.courseSettingsUpdated);
        long remoteTimestamp = getRemoteTimestamp(Constants.courseSettingsUpdated);
        log.info("Checking CourseSettingsUpdated timestamp local - {}, remote - {}, delta - {}", remoteTimestamp, 
                                            localTimestamp, remoteTimestamp - localTimestamp);
        if (remoteTimestamp == 0) {
            remoteTimestamp = initSettingTimestamp(Constants.courseSettingsUpdated);
        }
        if(localTimestamp < remoteTimestamp) {
            syncCourseSettings(remoteTimestamp);
        }
    }

    private void checkCourseInfoUpdated() {
        long remoteTimestamp = getCourseInfoRemoteTimestamp();
        long localTimestamp = getLocalTimestamp(Constants.courseUpdated);

        if(localTimestamp < remoteTimestamp || remoteTimestamp == 0) {
            syncGolfCourse(remoteTimestamp);
        }
    }

    private void checkPinsUpdated() {
        long localTimestamp = getLocalTimestamp(Constants.pinsUpdated);
        long remoteTimestamp = getRemoteTimestamp(Constants.pinsUpdated);
        log.info("Checking PinsUpdated timestamp local - {}, remote - {}, delta - {}", remoteTimestamp, 
                                               localTimestamp,  remoteTimestamp - localTimestamp);
        if(localTimestamp < remoteTimestamp) {
            syncPinLocations(remoteTimestamp);
        }
    }

    private void checkAdsUpdated() {
        long localTimestamp = getLocalTimestamp(Constants.adsUpdated);
        long remoteTimestamp = getRemoteTimestamp(Constants.adsUpdated);

        if(localTimestamp < remoteTimestamp) {
            syncAds(remoteTimestamp);
        }
    }

    private void checkFnbUpdated() {
        long localTimestamp = getLocalTimestamp(Constants.menuUpdated);
        long remoteTimestamp = getRemoteTimestamp(Constants.menuUpdated);

        if(localTimestamp < remoteTimestamp) {
            syncFnB(remoteTimestamp);
        }
    }

    private long initSettingTimestamp(String key) {
        long now = System.currentTimeMillis();
        String ts = Long.toString(now);
        CourseSettings cs = new CourseSettings(key, ts, new Date(now));
        courseSettingsRepo.save(cs);
        return now;
    }

    private void syncCourseSettings(long remoteTimestamp) {
        log.debug("syncCourseSettings()");
        List<CourseSettings> cs = DomainUtil.toList(courseSettingsRepo.findAll());
        localStorage.saveCourseSettings(cs);
        saveTimestamp(Constants.courseSettingsUpdated, remoteTimestamp);
    }

    private long getCourseInfoRemoteTimestamp() {
        long remoteTimestamp = getRemoteTimestamp(Constants.courseUpdated);
        if (remoteTimestamp == 0) {
            CourseInfo courseInfo = DomainUtil.getValidCourseInfo(courseInfoRepo.findAll());
            if (courseInfo != null) {
                return initSettingTimestamp(Constants.courseUpdated);
            }
        }
        return remoteTimestamp;
    }

    private long getPinsRemoteTimestamp() {
        long remoteTimestamp = getRemoteTimestamp(Constants.pinsUpdated);
        if (remoteTimestamp == 0) {
            CourseInfo courseInfo = DomainUtil.getValidCourseInfo(courseInfoRepo.findAll());
            if (courseInfo != null) {
                return initSettingTimestamp(Constants.pinsUpdated);
            }
        }
        return remoteTimestamp;
    }

    @Transactional
    private void syncGolfCourse(long remoteTimestamp) {
        log.debug("syncGolfCourse()");

        localStorage.saveCourseInfo(DomainUtil.getValidCourseInfo(courseInfoRepo.findAll()));
        List<Courses> courses = DomainUtil.toList(coursesRepo.findAll());
        localStorage.saveCourses(courses);

        log.debug("loadFullGolfClubDefinition()");
        localStorage.saveGolfClub(golfClubLoader.loadFullGolfClubDefinition());

        updateGolfClubPinLocations();

        saveTimestamp(Constants.courseUpdated, remoteTimestamp);
        lastCourseSync = System.currentTimeMillis();
    }

    private void syncPinLocations(long remoteTimestamp) {
        log.debug("syncPinLocations()");
        List<PinLocation> pinLocations = DomainUtil.toList(pinLocationRepo.findAll());
        localStorage.savePinLocations(pinLocations);
        saveTimestamp(Constants.pinsUpdated, remoteTimestamp);
        updateGolfClubPinLocations();
        lastCourseSync = System.currentTimeMillis();
    }

    private void updateGolfClubPinLocations() {
        GolfClub gc = localStorage.getGolfClub();
        PinLocation.loadPinLocation(gc, localStorage.listPinLocations());
        localStorage.saveGolfClub(gc);
    }

    private void syncAds(long remoteTimestamp) {
        log.debug("syncAds()");
        List<Clients> clients = DomainUtil.toList(clientRepo.findAll());
        localStorage.saveClients(clients);
        saveTimestamp(Constants.adsUpdated, remoteTimestamp);
    }

    private void syncFnB(long remoteTimestamp) {
        log.debug("syncFnB()");
        List<MenuItems> menuItems = DomainUtil.toList(menuItemRepo.findAll());
        List<HutsInfo> hutsInfo = DomainUtil.toList(hutsInfoRepo.findAll());
        localStorage.saveMenuItems(menuItems);
        localStorage.saveHutsInfo(hutsInfo);
        saveTimestamp(Constants.menuUpdated, remoteTimestamp);
    }

    /*
     * Timestamps
     * Defined Timestamps.json for local timestamp storage of assets.
     */

    private void saveTimestamp(String key, Long value) {
        Map<String, String> timestamps = localStorage.getTimestamps();
        timestamps = timestamps != null? timestamps : new HashMap<String, String>();
        timestamps.put(key, value == null? null : value.toString());
        localStorage.saveTimestamps(timestamps);
    }


    private long getLocalTimestamp(String key) {
        return localStorage.getTimestamp(key);
    }

    private long getRemoteTimestamp(String key) {
        CourseSettings cs = courseSettingsRepo.findOne(key);
        String value = cs == null ? null : cs.getValue();
        return value == null? 0 : NumberUtils.toLong(value);
    }
    
    public void syncInstantly(String assetType) {
        checkCourseSettingsUpdated();
        if (assetType.equalsIgnoreCase(Constants.menuUpdated)) {
            checkFnbUpdated();
        }
    }

}
