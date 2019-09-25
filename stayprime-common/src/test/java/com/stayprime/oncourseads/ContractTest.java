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
public class ContractTest {
    
    public ContractTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testIsActive() {
        System.out.println("isActive");
        Contract contract = new Contract(null, 1, null, null, 0);
        assertTrue(contract.isActive(0));
        assertFalse(contract.isExpired(0));

        contract.setStartDate(new Date(1000));
        assertFalse(contract.isActive(0));
        assertTrue(contract.isActive(1000));
        assertTrue(contract.isActive(1200));
        assertFalse(contract.isExpired(0));
        assertFalse(contract.isExpired(2000));

        contract.setEndDate(new Date(2000));
        assertFalse(contract.isActive(0));
        assertTrue(contract.isActive(1200));
        assertFalse(contract.isActive(2000));
        assertFalse(contract.isActive(2100));
        assertFalse(contract.isExpired(0));
        assertFalse(contract.isExpired(1000));
        assertTrue(contract.isExpired(2000));
        assertTrue(contract.isExpired(3000));
    }

    @Test
    public void testGetSponsoredHoleCount() {
        System.out.println("getSponsoredHoleCount");
        Contract contract = new Contract(null, 1, null, null, 0);
        assertEquals(0, contract.getSponsoredHoleCount());

        contract.setSponsoredHoles("");
        assertEquals(0, contract.getSponsoredHoleCount());

        contract.setSponsoredHoles("2:2,3,4;3:2,3,4");
        assertEquals(6, contract.getSponsoredHoleCount());

        contract.setSponsoredHoles("2:2,3,4;3:2,3,4;4;5:");
        assertEquals(6, contract.getSponsoredHoleCount());
    }

    @Test
    public void testIsForHole() {
        System.out.println("isForHole");

        Contract contract = new Contract(null, 1, null, null, 0);
        assertFalse(contract.isForHole(0, 0));
        assertFalse(contract.isForHole(1, 1));

        contract.setSponsoredHoles("");
        assertFalse(contract.isForHole(0, 0));
        assertFalse(contract.isForHole(1, 1));

        contract.setSponsoredHoles("2:2,3,4;3:2,3,4");

        assertFalse(contract.isForHole(1, 1));
        assertFalse(contract.isForHole(2, 1));
        assertTrue(contract.isForHole(2, 2));
        assertTrue(contract.isForHole(2, 3));
        assertTrue(contract.isForHole(2, 4));
        assertFalse(contract.isForHole(2, 5));

        assertFalse(contract.isForHole(3, 1));
        assertTrue(contract.isForHole(3, 2));
        assertTrue(contract.isForHole(3, 3));
        assertTrue(contract.isForHole(3, 4));
        assertFalse(contract.isForHole(3, 5));

        assertFalse(contract.isForHole(4, 1));
    }

}
