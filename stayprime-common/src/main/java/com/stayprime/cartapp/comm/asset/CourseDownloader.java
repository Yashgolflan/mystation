/*
 *
 */
package com.stayprime.cartapp.comm.asset;

import com.stayprime.localservice.CartUnitCommunication;
import com.stayprime.localservice.Constants;
import com.stayprime.util.file.FileLocator;
import com.stayprime.util.task.Task;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;
import com.stayprime.golf.course.storage.GolfClubLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class CourseDownloader extends Downloader<Site> implements Task {

    private static final Logger log = LoggerFactory.getLogger(CourseDownloader.class);

    private final GolfClubLoader golfClubLoader;
    private Properties loadedSettings;
    private Site golfClub;

    private final List<Observer<Site>> pinObservers;
    private final List<Observer<Properties>> settingsObservers;

    public CourseDownloader(CartUnitCommunication comm, GolfClubLoader loader, FileLocator fileLocator, DownloadingFileLocator fileDownloader) {
        super(comm, fileLocator, fileDownloader);
        this.golfClubLoader = loader;
        pinObservers = new ArrayList<Observer<Site>>();
        settingsObservers = new ArrayList<Observer<Properties>>();
    }

    public Site getGolfClub() {
        return golfClub;
    }

    void setGolfClub(Site golfClub) {
        this.golfClub = golfClub;
        setDownloadInProgress(false);
    }

    @Override
    public void checkAndLoadFromStorage() {
        log.debug("checkAndLoadFromStorage()");
        try {
            notifyDownloading();
            Site gc = checkCourseDefinition(fileLocator, null);
            setGolfClub(gc);
        }
        finally {
            notifyFinished();
        }
    }

    @Override
    public void startTask() {
    }

    @Override
    public void runTask() throws Exception {
        if (shouldUpdateCourse()) {
            setDownloadInProgress(true);
            setProgress(0);
            notifyDownloading();

            try {
                fileDownloader.reset();
                Site gc = checkCourseDefinition(fileLocator, fileDownloader);
                fileDownloader.commitDownloadedFiles();

                setGolfClub(gc);
            }
            finally {
                setDownloadInProgress(false);
                notifyFinished();
            }
        }
        else {
            checkPinLocations();
        }
    }

    boolean shouldUpdateCourse() {
        String remoteVersion = comm.getAssetVersion(Constants.courseUpdated);

        if (remoteVersion == null) {
            log.debug("Server returned null: don't update");
            return false;
        }

        if (golfClub == null) {
            log.info("GolfClubImpl is null: update from server");
            return true;
        }

        String localVersion = golfClub.getVersion();
        if (ObjectUtils.notEqual(localVersion, remoteVersion)) {
            log.info("GolfClubImpl out of date: update form server");
            return true;
        }

        log.info("GolfClubImpl is up to date");
        return false;
    }

    //For testing
    Site checkCourseDefinition() {
        return checkCourseDefinition(fileLocator, fileDownloader);
    }

    protected Site checkCourseDefinition(FileLocator fileLocator, DownloadingFileLocator downloader) {
        FileLocator fl;
        if (downloader != null) {
            fl = downloader;
            golfClubLoader.setFileLocator(downloader);
        }
        else {
            fl = fileLocator;
            golfClubLoader.setFileLocator(fl);
        }

        totalBytesDownloaded = 0;

        Site gc = golfClubLoader.loadGolfClubDefinition();
        setProgress(2);

        fl.getFile(gc.getLogo());
        assetsDownloaded |= fl.isFileDownloaded();
        totalBytesDownloaded += fl.getTotalBytes();

        BasicMapImage gcMap = gc.getMapImage();
        if (gcMap != null && gcMap.getImageAddress() != null) {
            if (downloader != null) {
                String localName = new File(gcMap.getImageAddress()).getName();
                downloader.getFile(localName, gcMap.getImageAddress());
                assetsDownloaded |= fl.isFileDownloaded();
                totalBytesDownloaded += fl.getTotalBytes();
            }
        }
        setProgress(4);

        Properties config = gc.getSiteConfig();
        fl.getFile(config.getProperty(Site.welcomeImage));
        assetsDownloaded |= fileLocator.isFileDownloaded();
        totalBytesDownloaded += fl.getTotalBytes();
        setProgress(6);

        fl.getFile(config.getProperty(Site.thankyouImage));
        assetsDownloaded |= fileLocator.isFileDownloaded();
        totalBytesDownloaded += fl.getTotalBytes();

//	fl.getFile(gc.getWelcomeImage());
//	imagesDownloaded |= fl.isFileDownloaded();
//	setProgress(6);
//
//	fl.getFile(gc.getThankyouImage());
//	imagesDownloaded |= fl.isFileDownloaded();
        setProgress(8);

        golfClubLoader.loadCourses(gc);

        setProgress(10);

        int totalHoles = 0, currentHole = 0;
        for (GolfCourse c : gc.getCourses()) {
            totalHoles += c.getHoleCount();
        }

        for (GolfCourse course : gc.getCourses()) {
            for (int n = 1; n <= course.getHoleCount(); n++) {
                GolfHole hole = golfClubLoader.loadHole(gc, course, n);
                course.setHole(n, hole);
                currentHole++;

                if (hole.getMapImage() != null) {
                    fl.getFile(hole.getMapImage().getImageAddress());
                    assetsDownloaded |= fl.isFileDownloaded();
                    totalBytesDownloaded += fl.getTotalBytes();
                }

                fl.getFile(hole.getFlyoverVideo());
                assetsDownloaded |= fl.isFileDownloaded();
                totalBytesDownloaded += fl.getTotalBytes();

                setProgress(10 + Math.round(90f * currentHole / totalHoles));
            }
        }

        checkPinLocations(gc, fl);

        return gc;
    }

//    private void loadSettingsFromGolfClubXml() {
//        try {
//            //Workaround to load configurations from GolfCourse.xml
//            PropertiesConfiguration config = configManager.getAppConfig();
//            loadedSettings = golfClubLoader.loadGolfClubSettings();
//
//            for (String key : loadedSettings.stringPropertyNames()) {
//                config.setProperty(key, loadedSettings.getProperty(key));
//            }
//
//            if (application != null) {
//                application.reloadConfig();
//            }
//        }
//        catch (Exception ex) {
//            log.error(ex.toString());
//        }
//    }
    public void addSettingsObserver(Observer<Properties> observer) {
        settingsObservers.add(observer);
    }

    public void removeSettingsObserver(Observer<Properties> observer) {
        settingsObservers.remove(observer);
    }

    /*
     * PinDownloader class to update only the PinLocations XML file.
     */
    public void addPinObserver(Observer<Site> observer) {
        pinObservers.add(observer);
    }

    public void removePinObserver(Observer<Site> observer) {
        pinObservers.remove(observer);
    }

    private void checkPinLocations() {
        if (shouldUpdatePins()) {
//	    setDownloadInProgress(true);
//	    setProgress(0, pinObservers);
//	    notifyDownloading(pinObservers);

            try {
                fileDownloader.reset();
                checkPinLocations(golfClub, fileDownloader);
                fileDownloader.commitDownloadedFiles();
            }
            finally {
                notifyPinsLoaded();
            }
        }
    }

    private void checkPinLocations(Site gc, FileLocator fileLocator) {
        golfClubLoader.setFileLocator(fileLocator);
        golfClubLoader.loadPinLocation(gc);
    }

    boolean shouldUpdatePins() {
        if (golfClub == null) {
            log.debug("GolfClubImpl is null: can't update pins");
            return false;
        }

        String remoteVersion = comm.getAssetVersion(Constants.pinsUpdated);

        if (remoteVersion == null) {
            log.debug("Server returned null: don't update");
            return false;
        }

        String localVersion = golfClub.getPinsVersion();
        if (ObjectUtils.notEqual(localVersion, remoteVersion)) {
            log.debug("Pin locations out of date: update form server");
            return true;
        }

        log.trace("Pin locations up to date");
        return false;
    }

    private void notifyPinsLoaded() {
        for (Observer<Site> o : pinObservers) {
            o.definitionLoaded(golfClub, 0);
        }
    }

    @Override
    protected void notifyFinished() {
        for (Observer<Properties> o : settingsObservers) {
            try {
                o.definitionLoaded(loadedSettings, 0);
            }
            catch (Exception ex) {
                log.error(ex.toString());
                log.debug(ex.toString(), ex);
            }
        }

        for (Observer<Site> o : observers) {
            try {
                o.definitionLoaded(golfClub, 0);
            }
            catch (Exception ex) {
                log.error(ex.toString());
                log.debug(ex.toString(), ex);
            }
        }
    }

    public void notifyPinUpdateFromServer() {
        try {
        checkPinLocations(golfClub, fileLocator);
        }
        finally {
            notifyPinsLoaded();
        }
    }

}
