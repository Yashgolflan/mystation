/*
 * 
 */
package com.stayprime.basestation2;

import com.alee.laf.WebLookAndFeel;
import com.stayprime.hibernate.entities.UserLogin;
import com.stayprime.storage.services.AssetSyncService;
import com.stayprime.storage.services.GolfClubLoader;
import com.stayprime.storage.util.LocalStorage;
import com.stayprime.storage.util.PersistenceUtil;
import com.stayprime.util.ClassUtilities;
import com.stayprime.util.ConfigUtil;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author benjamin
 */
@SpringBootApplication
@EnableJpaRepositories("com.stayprime.storage.repos")
@EntityScan("com.stayprime.hibernate.entities")
@ComponentScan(basePackages = "com.stayprime.basestation2")
@EnableScheduling
public class SpringApplication {
    private static final Logger log = LoggerFactory.getLogger(SpringApplication.class);
    public static String[] args;
    public static String workDir = Constant.BASESTATION_PATH;

    @Bean
    public BaseStation2App baseStation2App() {
        return BaseStation2App.getApplication();
    }

    @Bean
    public PropertiesConfiguration config() {
        findBasePath(args, 0);
        log.info("basePath: " + workDir);
        File configFile = new File(workDir, Constant.CONFIGURATION_FILENAME);
        return ConfigUtil.load(configFile, log);
    }

    @Bean
    LocalStorage localStorage() {
        return BaseStation2App.getApplication().getLocalStorage();
    }

    @Bean
    AssetSyncService assetSyncService() {
        return new AssetSyncService();
    }

    @Bean
    GolfClubLoader golfClubLoader() {
        return new GolfClubLoader();
    }

    @Bean
    public CourseSettingsManager courseSettingsManager() throws Exception{
        
            return BaseStation2App.getApplication().getCourseSettingsManager();
            
    }

    public static void main(final String[] args) throws Exception {
        SpringApplication.args = args;
        boolean db = args.length > 0 && ObjectUtils.equals(args[0], "db");
        boolean dbWithConf = args.length > 1 && ObjectUtils.equals(args[1], "db");

        if (db || dbWithConf) {
            runDbTask(dbWithConf);
        }
        else {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    WebLookAndFeel.setDecorateFrames(true);
                    Application.launch(BaseStation2App.class, args);
                }
            });

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    BaseStation2App app = BaseStation2App.getApplication();
                    app.runTask(new SpringStartupTask(app));
                }
            });
        }

    }

    public static String findBasePath(String[] args, int index) {
        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
            i++; //Skip arguments starting with -
        }
        workDir = getBasePath(args, i);
        return workDir;
    }

    private static String getBasePath(String[] args, int i) {
        if (i >= 0 && i < args.length && isDirectory(args[i])) {
            return args[i];
        }
        else if (isDirectory(Constant.BASESTATION_PATH)) {
            return Constant.BASESTATION_PATH;
        }
        else {
            return ClassUtilities.getCodeSource(BaseStation2App.class);
        }
    }

    private static boolean isDirectory(String path) {
        File file = new File(path);
        return file.isDirectory() && file.canRead();
    }

    private static void runDbTask(boolean dbWithConf) throws IOException {
        String basePath;
        if (dbWithConf) {
            basePath = getBasePath(args, 0);
        }
        else {
            basePath = getBasePath(args, -1);
        }

        File configFile = new File(basePath, Constant.CONFIGURATION_FILENAME);
        PropertiesConfiguration config = ConfigUtil.load(configFile, log);
        Properties props = PersistenceUtil.createDbPropertiesFromConfig(config);
        int dbArgsIndex = dbWithConf? 2 : 1;
        boolean create = args.length > dbArgsIndex && "create".equals(args[dbArgsIndex]);
        boolean update = args.length > dbArgsIndex && "update".equals(args[dbArgsIndex]);

        EntityManagerFactory emf = PersistenceUtil.runDbTask(args, dbArgsIndex, props);
        if ((create || update) && PersistenceUtil.confirmAction("Create default user?")) {
            System.out.println("Creating default user...");
            UserLogin u = new UserLogin("stayprime", "golfcart", "StayPrime", "", new Date());
            PersistenceUtil.createUser(emf, u);
        }
        PersistenceUtil.printCourseInfo(emf);
    }

}
