/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.storage.util;

import com.stayprime.hibernate.entities.CourseInfo;
import com.stayprime.hibernate.entities.UserLogin;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;

/**
 *
 * @author benjamin
 */
public class PersistenceUtil {
    public static String daoProjectPu = "DAOProjectPU";
    public static String golfgeniusPu = "GolfGeniusPU";

    public static String javax_persistence_jdbc_password = "javax.persistence.jdbc.password";
    public static String javax_persistence_jdbc_user = "javax.persistence.jdbc.user";
    public static String javax_persistence_jdbc_url = "javax.persistence.jdbc.url";

    public static String hibernate_hbm2ddl_auto = "hibernate.hbm2ddl.auto";
    public static String hbm2ddl_validate = "validate";
    
    public static String config_dbHostName = "dbHostName";
    public static String config_dbPort = "dbPort";
    public static String config_dbName = "dbName";
    public static String config_dbUser = "dbUser";
    public static String config_dbPassword = "dbPassword";
    public static String config_dbTimeZone = "dbTimeZone";
    public static String config_hbm2ddl = "hbm2ddl";

    private static void printPersistenceUnitInfo(String pu, Map props) {
        List<ParsedPersistenceXmlDescriptor> units = PersistenceXmlParser.locatePersistenceUnits(Collections.EMPTY_MAP);
        for (ParsedPersistenceXmlDescriptor unit : units) {
            if (pu.equals(unit.getName())) {
                if (props.containsKey(PersistenceUtil.javax_persistence_jdbc_url)) {
                    System.out.println("Database url: " + props.get(PersistenceUtil.javax_persistence_jdbc_url));
                }
                else if (unit.getProperties().containsKey(PersistenceUtil.javax_persistence_jdbc_url)) {
                    System.out.println("Database url: " + unit.getProperties().getProperty(PersistenceUtil.javax_persistence_jdbc_url));
                }
                else {
                    System.out.println("Database url not defined");
                }
            }
        }
    }

    private static EntityManagerFactory createEntityManager(String pu, String mode, Map props) {
        System.out.println("Running hbm2ddl for " + pu);
        Properties p = new Properties();
        p.putAll(props);
        System.out.println("hibernate.hbm2ddl.auto=" + mode);
        p.setProperty("hibernate.hbm2ddl.auto", mode);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(pu, p);
        System.out.println("Done.");
        return emf;
    }

    public static Properties createDbPropertiesFromConfig(PropertiesConfiguration config) {
        Properties props = new Properties();
        String dbName = config.getString(PersistenceUtil.config_dbName, "stayprime");
        String hostName = config.getString(PersistenceUtil.config_dbHostName, "localhost");
        String port = config.getString(PersistenceUtil.config_dbPort, "3306");
        String user = config.getString(PersistenceUtil.config_dbUser, "root");
        String password = config.getString(PersistenceUtil.config_dbPassword, "golfcart");
        String timeZone = config.getString(PersistenceUtil.config_dbTimeZone, null);
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName + "?zeroDateTimeBehavior=convertToNull&useLegacyDatetimeCode=false" + (timeZone == null ? "" : "&serverTimezone=" + timeZone);
        props.put(PersistenceUtil.javax_persistence_jdbc_url, url);
        props.put(PersistenceUtil.javax_persistence_jdbc_user, user);
        props.put(PersistenceUtil.javax_persistence_jdbc_password, password);
        String hbm2ddl = config.getString(PersistenceUtil.config_hbm2ddl, null);
        if (hbm2ddl != null) {
            props.put(PersistenceUtil.hibernate_hbm2ddl_auto, hbm2ddl);
        }
        return props;
    }

    public static void printCourseInfo(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        System.out.println("Course: ");
        em.getTransaction().begin();
        CourseInfo courseInfo = getCourseInfo(em);
        em.getTransaction().commit();
        System.out.println(courseInfo);
        em.close();
    }

    public static CourseInfo getCourseInfo(EntityManager em) {
        CourseInfo courseInfo = em.find(CourseInfo.class, 1);
        if (courseInfo == null) {
            courseInfo = em.find(CourseInfo.class, 0);
        }
        return courseInfo;
    }

    /**
     * Creates a new user if it doesn't already exist.
     * @param emf EntityManagerFactory to use
     * @param user UserLogin entity to create
     */
    public static void createUser(EntityManagerFactory emf, UserLogin user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        String userName = user.getUserName();
        String findUser = "SELECT u FROM UserLogin u WHERE u.userName = :un";
        Query query = em.createQuery(findUser).setParameter("un", userName);
        List userList = query.setMaxResults(1).getResultList();

        if (userList.isEmpty()) {
            em.persist(user);
        }

        em.getTransaction().commit();
        em.close();
    }

    public static EntityManagerFactory runDbTask(String[] args, int argIndex, Map properties) throws IOException {
        Map props = properties != null ? properties : new Properties();
        if (args.length > argIndex) {
            String action = args[argIndex];
            boolean golfgenius = args.length > (argIndex + 1) && args[argIndex + 1].equalsIgnoreCase("golfgenius");
            String name = golfgenius ? "golfgenius" : "main";
            String persistenceUnit = golfgenius ? golfgeniusPu : daoProjectPu;
            runDdlTasks(action, name, persistenceUnit, props);
        }
        props.put("hibernate.hbm2ddl.auto", "validate");
        return Persistence.createEntityManagerFactory(PersistenceUtil.daoProjectPu, properties);
    }

    public static void runDdlTasks(String action, String name, String persistenceUnit, Map props) throws IOException {
        if (ObjectUtils.equals(action, "create")) {
            if (confirmDbAction("Create", name, persistenceUnit, props)) {
                printPersistenceUnitInfo(persistenceUnit, props);
                createEntityManager(persistenceUnit, action, props).close();
            }
        }
        else if (ObjectUtils.equals(action, "update")) {
            if (confirmDbAction("Update", name, persistenceUnit, props)) {
                printPersistenceUnitInfo(persistenceUnit, props);
                createEntityManager(persistenceUnit, action, props).close();
            }
        }
        else if (ObjectUtils.equals(action, "validate")) {
            printPersistenceUnitInfo(persistenceUnit, props);
            createEntityManager(persistenceUnit, action, props).close();
        }
    }

    /**
     * Ask for user confirmation before trying to alter database.
     * For example: confirmDbAction("create", "main", props)
     * @param action Human readable action name
     * @param puName Human readable persistence unit name
     * @param pu Persistence unit name
     * @param props Persistence unit creation properties
     * @return true if user confirms to proceed
     * @throws IOException
     */
    private static boolean confirmDbAction(String action, String puName, String pu, Map props) throws IOException {
        System.out.println(action + " " + puName + " database");
        printPersistenceUnitInfo(pu, props);
        return confirmAction("Really " + action + " " + puName + " database? This may destroy existing data:");
    }

    public static boolean confirmAction(String query) throws IOException {
        System.out.print(query);
        System.out.print(' ');
        System.out.flush();
        Scanner s = new Scanner(System.in);
        return "y".equalsIgnoreCase(s.nextLine());
    }

}
