/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.hibernate.entities;

import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class HolesTest {

    public HolesTest() {
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
     * Test of setMap method, of class Holes.
     */
    @Test
    public void testSetMap() {
        System.out.println("setMap");
        Holes h = new Holes();
        h.setMap(new BasicMapImage("path", c(1), c(2), c(3), c(4)));
        assertEquals(h.getCornerTopLeft(), c(1).toString());
        assertEquals(h.getCornerTopRight(), c(2).toString());
        assertEquals(h.getCornerBottomLeft(), c(3).toString());
        assertEquals(h.getCornerBottomRight(), c(4).toString());
        h.setMap(null);
        assertNull(h.getMapImage());
        assertNull(h.getCornerTopLeft());
        assertNull(h.getCornerTopRight());
        assertNull(h.getCornerBottomLeft());
        assertNull(h.getCornerBottomRight());
    }

    private Coordinates c(double d) {
        return new Coordinates(d, d);
    }

}
