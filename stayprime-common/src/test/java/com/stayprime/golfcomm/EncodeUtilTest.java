/*
 * 
 */
package com.stayprime.golfcomm;

import com.stayprime.comm.encoder.EncodeUtil;
import com.stayprime.model.golf.Position;
import java.awt.geom.Point2D;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author benjamin
 */
public class EncodeUtilTest {
    
    public EncodeUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of encodeRelativePosition method, of class EncodeUtil.
     */
    @Test
    public void testEncodeRelativePosition() {
	System.out.println("encodeLocation");
	byte[] data = new byte[8];
	int offset = 0;

	Point2D srcPoint = null;
	Position destPoint = null;
	Point2D origin = null;
	Position originPos = null;

	EncodeUtil.encodeRelativePosition(data, offset, origin, srcPoint);
	destPoint = EncodeUtil.decodeRelativePosition(data, offset, originPos, destPoint);
	//System.out.println(ArrayUtils.toString(data));
	assertEquals(null, destPoint);
	
	srcPoint = new Point2D.Double(32.233243245d, 15.8432432432424d);
	
	EncodeUtil.encodeRelativePosition(data, offset, origin, srcPoint);
	destPoint = EncodeUtil.decodeRelativePosition(data, offset, originPos, destPoint);
	//System.out.println(ArrayUtils.toString(data));
	assertEquals(null, destPoint);
	
	origin = new Point2D.Double(32, 16);
	originPos = new Position(32, 16);
	destPoint = new Position();
	
	EncodeUtil.encodeRelativePosition(data, offset, origin, srcPoint);
	destPoint = EncodeUtil.decodeRelativePosition(data, offset, originPos, destPoint);
	//System.out.println(ArrayUtils.toString(data));
	assertEquals(srcPoint.getX(), destPoint.getX(), 0.0001);
	assertEquals(srcPoint.getY(), destPoint.getY(), 0.0001);
    }

    /**
     * Test of encodeBearing method, of class ShortLocationEncoder.
     */
    @Test
    public void testEncodeHeading() {
	System.out.println("encodeHeading");
	byte[] data = new byte[8];
	int offset = 4;

	Float heading = 0f;
	Float result;
	
	EncodeUtil.encodeBearing(heading, data, offset);
	//System.out.println(ArrayUtils.toString(data));
	result = EncodeUtil.decodeBearing(data, offset);
	assertEquals(heading, result);
	
	heading = 45f;
	
	EncodeUtil.encodeBearing(heading, data, offset);
	//System.out.println(ArrayUtils.toString(data));
	result = EncodeUtil.decodeBearing(data, offset);
	assertEquals(heading, result, 2f);
    }

    /**
     * Test of encodeSpeed method, of class ShortLocationEncoder.
     */
    @Test
    public void testEncodeSpeed() {
	System.out.println("encodeSpeed");
	float speed = 0;
	byte[] dest = new byte[8];
	int offset = 0;
	
	EncodeUtil.encodeSpeed(speed, dest, offset);
    }

    /**
     * Test of decodeSpeed method, of class ShortLocationEncoder.
     */
    @Test
    public void testDecodeSpeed() {
	System.out.println("decodeSpeed");
	byte[] dest = new byte[8];
	float expResult = 0f;
	float result = EncodeUtil.decodeSpeed(dest, 0);
	
	assertEquals(expResult, result, 1e-6f);
    }

    /**
     * Test of encodeRelativeCoord method, of class ShortLocationEncoder.
     */
    @Test
    public void testEncodeCoordToShort() {
	System.out.println("encodeCoordToShort");
	double origin = 0.0;
	double coord = 0.0;
	short expResult = 0;
	short result = EncodeUtil.encodeRelativeCoord(origin, coord);
	assertEquals(expResult, result);
    }

    /**
     * Test of decodeRelativeCoord method, of class ShortLocationEncoder.
     */
    @Test
    public void testDecodeShortToCoord() {
	System.out.println("decodeShortToCoord");
	double origin = 0.0;
	short encoded = 0;
	double expResult = 0.0;
	
	double result = EncodeUtil.decodeRelativeCoord(origin, encoded);
	assertEquals(expResult, result, 1e-6);
    }
}
