/*
 * 
 */
package com.stayprime.basestation.ui.site;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.hibernate.entities.CourseSettings;
import com.stayprime.hibernate.entities.Courses;
import java.util.List;
import javax.swing.JOptionPane;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class CourseSetupStorageTasks {
    private final CourseService courseService;
    private final GolfSiteSetupScreen callback;

    public CourseSetupStorageTasks(CourseService courseService, GolfSiteSetupScreen callback) {
        this.courseService = courseService;
        this.callback = callback;
    }

    private void execute(Task task) {
        BaseStation2App.getApplication().runTask(task);
    }

    public void loadGolfSite() {
        execute(new LoadTask());
    }

    public void saveCourseInfo(CourseInfo golfSite, List<Courses> courses, CourseSettings[] settings) {
        execute(new SaveGolfSiteTask(golfSite, courses, settings));
    }
   

    private class LoadTask extends Task<CourseInfo, Void> {
        List<Courses> courses;
        List<CourseSettings> settings;
        public LoadTask() {
            super(BaseStation2App.getApplication());
        }
        @Override
        protected CourseInfo doInBackground() throws Exception {
            CourseInfo courseInfo = courseService.getCourseInfo();
            if (courseInfo == null) {
                courseInfo = new CourseInfo();
                courseInfo.setCourseId(1);
            }
            courses = courseService.listCourses();
            settings = courseService.listSettings();
            return courseInfo;
        }

        @Override
        protected void succeeded(CourseInfo result) {
            callback.setGolfSite(result, courses, settings);
            //loadGolfSite();
        }

        @Override
        protected void failed(Throwable cause) {
//            callback.loadingFailed(cause);
            NotificationPopup.showErrorPopup("Course");
        }
    }

    private class SaveGolfSiteTask extends Task<CourseInfo, Void> {
        CourseInfo golfSite;
        List<Courses> courses;
        CourseSettings[] settings;
        public SaveGolfSiteTask(CourseInfo golfSite, List<Courses> courses, CourseSettings[] settings) {
            super(BaseStation2App.getApplication());
            this.golfSite = golfSite;
            this.courses = courses;
            this.settings = settings;
        }

        @Override
        protected CourseInfo doInBackground() throws Exception {
            courseService.saveCourses(courses);
            CourseInfo c = courseService.saveCourseInfo(golfSite);
            courseService.saveSettings(settings);
            courseService.getLocalStorage().saveGolfClub(courseService.getGolfClub());
           //
          
             // BaseStation2App.getApplication().getMainView().reloadCourse();
            return c;
        }

        @Override
        protected void succeeded(CourseInfo result) {
            callback.saveTaskDone(null);
            courseService.getLocalStorage().saveCourseInfo(golfSite);
            courseService.getLocalStorage().saveCourses(courses);
            BaseStation2App.getApplication().getMainView().reloadCourse();
          BaseStation2App.getApplication().syncGolfClub();
          BaseStation2App.getApplication().getMainView().getMainPanel().setLogoImage(golfSite.getLogoImage());    
        }

        @Override
        protected void failed(Throwable cause) {
//            callback.saveTaskDone(cause);
            
            NotificationPopup.showErrorPopup("Course");
        }
    }

}
