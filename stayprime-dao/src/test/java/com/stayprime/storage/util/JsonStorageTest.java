/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.storage.util;

import com.stayprime.storage.util.JsonStorage;
import com.google.gson.reflect.TypeToken;
import com.stayprime.storage.domain.ReportedUnit;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author priyanshu
 */
public class JsonStorageTest {

    static JsonStorage instance;

    public JsonStorageTest() {
        instance = new JsonStorage(".");
        instance.mkDirs();
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
     * Test of addToList method, of class JsonStorage.
     */
    @Test
    public void testAddToList() {
        System.out.println("addToList");

        TypeToken listType = new TypeToken<List<ReportedUnit>>() {};

        ReportedUnit rui = new ReportedUnit("mac");

//        instance.addToList(ReportedUnit.class, listType, ru, true);

//        String macAddress = "MAC10";
//        String simIccid = "changed simiccid but same mac";
//        String simIpAddress = "same mac";

//        CloudDbCartService x = new CloudDbCartService();
//        ReportedUnit rui = x.findByMacAddressOrCreate(macAddress);
//
//        rui.putAssetUpdated(50, new Date());

        try {
            instance.addToList(ReportedUnit.class, listType, rui, true);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
//        instance.removeFromList(ReportedUnit.class, listType, ru);

        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of removeFromList method, of class JsonStorage.
     */
//    @Test
//    public void testRemoveFromList() {
//        System.out.println("removeFromList");
//
//        TypeToken listType = CloudDbCartService.reportedUnitToken;
//
//        ReportedUnit ru = new ReportedUnit("mac");
//
//        instance.removeFromList(ReportedUnit.class, listType, ru);
//
//        String macAddress = "macadd";
//
//        ru = new ReportedUnit(macAddress, "simIccid", "simIpAddress");
//
//        instance.removeFromList(ReportedUnit.class, listType, ru);
//
//        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }

/*    @Test
    public void testAddToList() {
        System.out.println("addToList");

        TypeToken listType = CloudDbCartService.reportedUnitToken;

        String macAddress = "macadd";
        String simIccid = "simiccid";
        String simIpAddress = "ipadd";

        ReportedUnit ru = new ReportedUnit(macAddress, simIccid, simIpAddress);

        instance.addToList(ReportedUnit.class, listType, ru, true);

        instance.addToList(ReportedUnit.class, listType, ru, true);

        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
*/
/*    @Test
    public void testRemoveFromList() {
        System.out.println("removeFromList");

        TypeToken listType = CloudDbCartService.reportedSimInformationToken;

        String macAddress = "macadd";

        ReportedSimInformation rui = new ReportedSimInformation(macAddress, "simIccid", "simIpAddress", new Date());

        instance.removeFromList(ReportedSimInformation.class, listType, rui);

        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
*/
}
