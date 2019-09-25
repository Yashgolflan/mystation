/*
 * 
 */
package com.stayprime.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Properties;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class EmailUtilTest {
    
    public EmailUtilTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testGetEmailAddresses() throws AddressException {
        System.out.println("getInetAddresses");
        String toAddr = "a@stayprime.com";
        InternetAddress[] expResult = new InternetAddress[] {
            new InternetAddress("a@stayprime.com")
        };
        InternetAddress[] result = EmailUtil.getEmailAddresses(toAddr);
        assertArrayEquals(expResult, result);

        toAddr = "a@stayprime.com b@stayprime.com";
        expResult = new InternetAddress[] {
            new InternetAddress("a@stayprime.com"),
            new InternetAddress("b@stayprime.com")
        };
        result = EmailUtil.getEmailAddresses(toAddr);
        assertArrayEquals(expResult, result);
    }
    
}
