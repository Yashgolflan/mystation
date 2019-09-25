/*
 *
 */
package com.stayprime.oncourseads;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class AdInformationTest {

    public AdInformationTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testIsActiveForHole() {
        System.out.println("isActiveForHole");
        Ad ad = new Ad(1, 1, "");
        AdInformation instance = new AdInformation(ad);
        long time = 1000;

        assertFalse(instance.isActiveForHole(1, 1, time));

        Contract c = new Contract(ad, 1, null, null, 0);
        ad.getContracts().add(c);
        testActive(instance, time, false, false, false);

        c = new Contract(ad, 1, new Date(1000), new Date(2000), 0);
        c.setSponsoredHoles("1:1,18;2:1,18");
        ad.getContracts().add(c);
        //Test the first contract
        time = 500;
        testTimeInterval(instance, time, true, false, true);

        c = new Contract(ad, 2, new Date(3000), new Date(4000), 0);
        c.setSponsoredHoles("1:9;2:9");
        ad.getContracts().add(c);
        //Test the second contract
        time = 2500;
        testTimeInterval(instance, time, false, true, false);

        //Test the first contract again
        time = 500;
        testTimeInterval(instance, time, true, false, true);
    }

    private void testTimeInterval(AdInformation instance, long startTime, boolean hole1, boolean hole9, boolean hole18) {
        long time = startTime;
        testActive(instance, time, false, false, false);
        time += 500;
        testActive(instance, time, hole1, hole9, hole18);
        time += 500;
        testActive(instance, time, hole1, hole9, hole18);
        time += 500;
        testActive(instance, time, false, false, false);
        time += 500;
        testActive(instance, time, false, false, false);
    }

    private void testActive(AdInformation instance, long time, boolean hole1, boolean hole9, boolean hole18) {
        assertEquals(hole1, instance.isActiveForHole(1, 1, time));
        assertEquals(hole9, instance.isActiveForHole(1, 9, time));
        assertEquals(hole18, instance.isActiveForHole(1, 18, time));
        assertEquals(hole1, instance.isActiveForHole(2, 1, time));
        assertEquals(hole9, instance.isActiveForHole(2, 9, time));
        assertEquals(hole18, instance.isActiveForHole(2, 18, time));
    }

}
