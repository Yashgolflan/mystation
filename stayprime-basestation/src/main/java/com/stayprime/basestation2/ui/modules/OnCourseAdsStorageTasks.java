/*
 * 
 */
package com.stayprime.basestation2.ui.modules;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.ApplicationTask;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.Clients;
import java.net.URL;
import java.net.URLConnection;
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
//    int clientCount = 0;
//    int updatedClientCount = 0;
//    boolean isFirstTime = true;

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
//            clientCount = clients.size();
//            if (clientCount != updatedClientCount && !isFirstTime) {
//                load();
//            }
//            if(isFirstTime){
//                isFirstTime = false;
//                updatedClientCount = clients.size();
//            }
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
        int internet=checkNetworkConnectivity();
        if(internet==1){
            return new SaveClientTask();
        }
        
        return null;
    }
private int checkNetworkConnectivity() {
     try { 
            URL url = new URL("https://www.google.com/"); 
            URLConnection connection = url.openConnection(); 
            connection.connect(); 
            
            System.out.println("Connection Successful"); 
            return 1;
        } 
        catch (Exception e) { 
            NotificationPopup.showErrorPopup();
            System.out.println("Internet Not Connected"); 
            return 0;
        } 
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private class SaveClientTask extends ApplicationTask<Void> {

        @Override
        protected Void performTask() {
            courseService.saveClients(new ArrayList(clients));
//            updatedClientCount = clients.size();
//      Not calling adsUpdated() now as we are not setting the adsUpdated from frontend now.
//           adsUpdated();
            return null;
        }

        @Override
        protected void succeeded() {
           System.out.println("------------success called---------");
            courseService.getLocalStorage().saveClients(new ArrayList(clients));
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
            
            courseService.deleteClient(deleteClient, new ArrayList(clients));
//            courseService.deleteClient(deleteClient);
//            updatedClientCount = clientCount - 1;
//            adsUpdated();
            return null;
        }

        @Override
        protected void succeeded() {
            clients.remove(deleteClient);
          courseService.getLocalStorage().saveClients(clients);
          screen.setClientList(clients);
            load();
            
        }
    }

}
