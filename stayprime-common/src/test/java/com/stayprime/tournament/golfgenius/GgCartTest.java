/*
 * 
 */
package com.stayprime.tournament.golfgenius;

import java.util.Date;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class GgCartTest {
    
    public GgCartTest() {
    }

    @Test
    public void testSetPlayers() {
        System.out.println("setPlayers");
        String players = "11224034,Byers Logan,true,true;11224033,Goodwin Mike,true,true;";

        GgCart cart = new GgCart();
        cart.setPlayers(players);

        assertEquals(2, cart.getPlayerList().size());
    }

}
