/*
 *
 */

package com.aeben.golfcourse.menu;

import com.stayprime.cartapp.menu.MenuItem;
import com.stayprime.cartapp.menu.Hut;
import com.stayprime.cartapp.menu.Menu;
import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.Site;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author benjamin
 */
public class MenuTest {

    public MenuTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of loadMenu method, of class Menu.
     */
    @Test
    public void testLoadMenu() {
    }

    /**
     * Test of isEnabledForHole method, of class Menu.
     */
    @Test
    public void testIsEnabledForHole() {
        System.out.println("isEnabledForHole");

        Site golfClub = new Site(null);
        ArrayList<GolfCourse> courses = new ArrayList<GolfCourse>();

        for(int c = 1; c <= 3; c++) {
            GolfCourse course = new GolfCourse(golfClub, null, c);
            course.setHoleCount(18);
            course.trimHoleCount();
            golfClub.addCourse(course);

            //Done automatically by setHoleCount:
//            for(int n = 1; n <= 18; n++)
//                course.setHole(n, new GolfHole(golfClub, course, n));
        }

        Menu menu = new Menu();
        menu.getDrinks().add(new MenuItem());

        Hut hut1 = new Hut(1);
        menu.getHuts().add(hut1);
        int h = 1;
        hut1.setForHole(h++);
        hut1.setForHole(h++);
        hut1.setForHole(h++);
        hut1.setForHole(h++);

        h = 48;
        Hut hut2 = new Hut(2);
        menu.getHuts().add(hut2);
        hut2.setForHole(h++);
        hut2.setForHole(h++);
        hut2.setForHole(h++);
        hut2.setForHole(h++);
        hut2.setForHole(h++);

        for(int i = 0; i < 54; i++) {
            int n = i + 1;
            boolean enabledHut1 = n >= 1 && n <= 4;
            boolean enabledHut2 = n >= 48 && n <= 52;

            System.out.println("Hole " + n + " " + enabledHut1 + " " + enabledHut2);

            boolean result = menu.isEnabledForHole(golfClub.getCourse(i/18+1).getHole(i%18+1));
            assertEquals(enabledHut1 || enabledHut2, result);
        }
    }

}
