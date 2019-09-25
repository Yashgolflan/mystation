/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2;

import com.stayprime.basestation2.util.NotificationPopup;
import org.jdesktop.application.Task;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 *
 * @author benjamin
 */
public class SpringStartupTask extends Task<Void, Void> {
    
    public SpringStartupTask(BaseStation2App app) {
        super(app);
        if (app.isReady() == false && app.startupError != null) {
            startupError(app.startupError);
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        setMessage("Initializing application...");
        try {
            SpringApplicationBuilder springApp = new SpringApplicationBuilder(SpringApplication.class).headless(false).web(false);
            springApp.run(SpringApplication.args);
            setMessage("Starting application services...");
          //  BaseStation2App.getApplication().start();
        } catch (Exception ex) {
                System.out.println("Failed to start Spring Application");
                NotificationPopup.showErrorPopup();
        }
        BaseStation2App.getApplication().start();
        return null;
    }

    @Override
    protected void failed(Throwable cause) {
        startupError(new RuntimeException("Application setup failed.", cause));
    }

    private void startupError(Throwable cause) {
//        TaskDialogs.showException(cause);
//        System.exit(1);
            NotificationPopup.showErrorPopup();
    }
    
}
