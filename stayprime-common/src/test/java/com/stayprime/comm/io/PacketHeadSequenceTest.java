/*
 * 
 */
package com.stayprime.comm.io;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class PacketHeadSequenceTest {
    
    public PacketHeadSequenceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of isSequenceChar method, of class PacketHeadSequenceTest.
     */
    @Test
    public void testIsSequenceChar() {
	System.out.println("isSequenceChar");
	IPHeadSequence instance = new IPHeadSequence();
	String sequence = "192.168.1.010:8080,16,";
	instance.reset();
	
	int expResult = sequence.length();
	int result = feedSequence(instance, sequence.getBytes());
	assertEquals(expResult, result);

	sequence = "0.168.1.010:8080,16,";
	instance.reset();
	expResult = sequence.length();
	result = feedSequence(instance, sequence.getBytes());
	assertEquals(expResult, result);
	
	assertFalse(instance.isSequenceChar('0'));

	sequence = "1234.168.1.010:8080,16,";
	instance.reset();
	expResult = 3;
	result = feedSequence(instance, sequence.getBytes());
	assertEquals(expResult, result);

	sequence = "2.1680.1.010:8080,16,";
	instance.reset();
	expResult = 5;
	result = feedSequence(instance, sequence.getBytes());
	assertEquals(expResult, result);

	sequence = "90.01..1:8080,16,";
	instance.reset();
	expResult = 6;
	result = feedSequence(instance, sequence.getBytes());
	assertEquals(expResult, result);

	sequence = ".1..1:8080,16,";
	instance.reset();
	expResult = 0;
	result = feedSequence(instance, sequence.getBytes());
	assertEquals(expResult, result);
    }
    
    private int feedSequence(IPHeadSequence test, byte sequence[]) {
	int i;
	for(i = 0; i < sequence.length; i++) {
	    if(test.isSequenceChar(sequence[i]) == false)
		break;
	}

	return i;
    }

    /**
     * Test of isSequence method, of class PacketHeadSequenceTest.
     */
    @Test
    public void testIsSequence() {
	System.out.println("isSequence");
	IPHeadSequence instance = new IPHeadSequence();

	String sequence = "192.168.1.010:8080,";
	instance.reset();
	feedSequence(instance, sequence.getBytes());
	assertFalse(instance.isSequence());

	sequence = "192.168.1.010:8080,16,";
	instance.reset();
	feedSequence(instance, sequence.getBytes());
	assertTrue(instance.isSequence());

	assertArrayEquals(new byte[] {(byte)192,(byte)168,(byte)1,(byte)10}, instance.getIp());
	assertEquals(8080, instance.getPort());
	assertEquals(16, instance.getBytes());

	assertTrue(instance.isSequence());

	instance.isSequenceChar('0');
	assertFalse(instance.isSequence());

	sequence = "0.168.0.010:8080,16,";
	instance.reset();
	feedSequence(instance, sequence.getBytes());
	assertTrue(instance.isSequence());

	sequence = "0.168..010:8080,16,";
	instance.reset();
	feedSequence(instance, sequence.getBytes());
	assertFalse(instance.isSequence());

	sequence = "1234.168.1.010:8080,16,";
	instance.reset();
	assertFalse(instance.isSequence());
    }

    /**
     * Test of getIp method, of class PacketHeadSequenceTest.
     */
    @Test
    public void testGetIp() {
	System.out.println("getIp");
	IPHeadSequence instance = new IPHeadSequence();
	byte[] expResult = null;
	byte[] result = instance.getIp();
	//assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getPort method, of class PacketHeadSequenceTest.
     */
    @Test
    public void testGetPort() {
	System.out.println("getPort");
	IPHeadSequence instance = new IPHeadSequence();
	int expResult = 0;
	int result = instance.getPort();
	//assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getBytes method, of class PacketHeadSequenceTest.
     */
    @Test
    public void testGetBytes() {
	System.out.println("getBytes");
	IPHeadSequence instance = new IPHeadSequence();
	int expResult = 0;
	int result = instance.getBytes();
	//assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getSequence method, of class PacketHeadSequenceTest.
     */
    @Test
    public void testGetSequence() {
	System.out.println("getSequence");
	IPHeadSequence instance = new IPHeadSequence();
	String expResult = "";
	String result = instance.getSequence();
	//assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
    }
}
