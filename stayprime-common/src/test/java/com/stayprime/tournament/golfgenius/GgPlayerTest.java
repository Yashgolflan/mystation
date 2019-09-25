/*
 * 
 */
package com.stayprime.tournament.golfgenius;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author benjamin
 */
public class GgPlayerTest {
    
    public GgPlayerTest() {
    }

    @Test
    public void testListToString() {
        System.out.println("listToString");

        ArrayList<GgPlayer> l1 = new ArrayList<GgPlayer>();
        l1.add(new GgPlayer(34, "Bb", true, false));
        l1.add(new GgPlayer(35, "Cc", false, true));
        Assert.assertEquals(2, l1.size());

        ArrayList<GgPlayer> l2 = new ArrayList<GgPlayer>();
        GgPlayer.addToListFromString(l2, "34,Bb,true,false;35,Cc,false,true;");
        Assert.assertEquals(2, l2.size());
        Assert.assertArrayEquals(l1.toArray(), l2.toArray());

        ArrayList<GgPlayer> l3 = new ArrayList<GgPlayer>();
        String listToString = GgPlayer.listToString(l1);
        System.out.println(listToString);
        GgPlayer.addToListFromString(l3, listToString);
        Assert.assertEquals(2, l3.size());
        Assert.assertArrayEquals(l1.toArray(), l3.toArray());
    }

}
