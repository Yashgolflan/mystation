/*
 * 
 */

package com.stayprime.basestation2.ui.modules;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.ApplicationTask;
import com.stayprime.hibernate.entities.Clients;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskService;

/**
 *
 * @author benjamin
 */
public class OnCourseAdsStorageTasks {
    private OnCourseAdsScreen screen;

    private List<Clients> clients;
    private CourseService courseService;

    public OnCourseAdsStorageTasks(OnCourseAdsScreen screen) {
        this.screen = screen;
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void load() {
        BaseStation2App.getApplication().runTask(new LoadTask());
    }

    private class LoadTask extends ApplicationTask<Void> {
        @Override
        protected Void performTask() {
            clients = courseService.listClients();
//            categories = categoriesDAO.listCategories();
            return null;
        }
        
        @Override
        protected void succeeded() {
            screen.setClientList(clients);
//            screen.setCategoriesList(categories);
        }
    }

    public void saveClients(List<Clients> clients) {
        this.clients = clients;
        ApplicationContext context = Application.getInstance().getContext();
        TaskService taskService = context.getTaskService();
	taskService.execute(new SaveClientTask());
    }

    public SaveClientTask getSaveClientsTask(List<Clients> clients) {
        Clients.setParents(clients);
        this.clients = clients;
        return new SaveClientTask();
    }

    private class SaveClientTask extends ApplicationTask<Void> {
        @Override
        protected Void performTask() {
            courseService.saveClients(new ArrayList(clients));
//      Not calling adsUpdated() now as we are not setting the adsUpdated from frontend now.
//            adsUpdated();
            return null;
        }

        @Override
        protected void succeeded() {
            load();
        }

    }

    public void deleteClient(Clients deleteClient) {
        ApplicationContext context = Application.getInstance().getContext();
        TaskService taskService = context.getTaskService();
	taskService.execute(new DeleteClientTask(deleteClient));
    }

    public DeleteClientTask getDeleteClientTask(Clients deleteClient) {
        return new DeleteClientTask(deleteClient);
    }

    private class DeleteClientTask extends ApplicationTask<Void> {
	Clients deleteClient;

        DeleteClientTask(Clients deleteClient) {
	    this.deleteClient = deleteClient;
        }

        @Override
        protected Void performTask() {
            courseService.deleteClient(deleteClient);
//            adsUpdated();
            return null;
        }

        @Override
        protected void succeeded() {
            load();
        }
    }

}
