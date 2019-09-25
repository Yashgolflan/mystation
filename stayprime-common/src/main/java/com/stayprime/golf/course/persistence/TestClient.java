/*
 * 
 */
package com.stayprime.golf.course.persistence;

import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author benjamin
 */
public class TestClient {
    private EntityManagerFactory emf;
    private EntityManager em;

    public static void main(String[] args) {
// Start EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("DAOProjectPU");
// First unit of work
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Site site = new Site("Test site");
        em.persist(site);

        GolfCourse golfCourse = new GolfCourse(site, "course 1", 1);
        golfCourse.setHoleCount(9);
        golfCourse.trimHoleCount();
        site.addCourse(golfCourse);
        em.persist(golfCourse);

        tx.commit();
        em.close();
// Shutting down the application
        emf.close();
    }

}
