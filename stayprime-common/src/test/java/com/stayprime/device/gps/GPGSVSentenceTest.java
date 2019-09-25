/*
 *
 */
package com.stayprime.device.gps;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author benjamin
 */
public class GPGSVSentenceTest {

    public GPGSVSentenceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of reset method, of class GPGSVSentences.
     */
    @Test
    public void testReset() {
	System.out.println("reset");
	GPGSVSentence instance = new GPGSVSentence();
	instance.reset();
	// TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of parseSentence method, of class GPGSVSentences.
     */
    @Test
    public void testParseSentence() {
	System.out.println("parseSentence");
	GPGSVSentence instance = new GPGSVSentence();

	String line = "$GPGSV,3,1,11,03,03,111,00,04,15,270,00,06,01,010,00,13,06,292,00*74";
	int totalSVs = 11;
	int svs = 4;

	boolean sentenceParsed = instance.parseSentence(line);
	assertTrue(sentenceParsed);
	assertEquals(totalSVs, instance.getTotalSVs());
	assertEquals(svs, instance.getReadSVs());
	assertFalse(instance.isFullyRead());

	line = "$GPGSV,3,2,11,14,25,170,00,16,57,208,39,18,67,296,40,19,40,246,00*74";
	svs = 8;

	sentenceParsed = instance.parseSentence(line);
	assertTrue(sentenceParsed);
	assertEquals(totalSVs, instance.getTotalSVs());
	assertEquals(svs, instance.getReadSVs());
	assertFalse(instance.isFullyRead());

	line = "$GPGSV,3,3,11,22,42,067,42,24,14,311,43,27,05,244,00,,,,*4D";
	svs = 11;

	sentenceParsed = instance.parseSentence(line);
	assertTrue(sentenceParsed);
	assertEquals(totalSVs, instance.getTotalSVs());
	assertEquals(svs, instance.getReadSVs());
	assertTrue(instance.isFullyRead());

	assertEquals(41f, instance.getMeanSNR(), 0.0001);

	instance.reset();
	line = "$GPGSV,1,1,0*4D";

	sentenceParsed = instance.parseSentence(line);
	assertTrue(sentenceParsed);
	assertEquals(0, instance.getTotalSVs());
	assertEquals(0, instance.getReadSVs());
	//TODO Review this behavior:
	assertFalse(instance.isFullyRead());
    }
}
