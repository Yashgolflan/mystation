/*
 * 
 */
package com.stayprime.comm.encoder;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author benjamin
 */
public class PacketTypeTest {

    public PacketTypeTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test of values method, of class PacketType.
     */
    @Test
    public void testValues() {
        System.out.println("values");

        for (PacketType type : PacketType.values()) {
            switch (type) {
                case GAMESTAT:
                case CARTAHEAD:
                case STATUS:
                case PRE_MSG:
                    testOtherTypesDontMatchThisId(type, type.id);
                    break;
                default:
                    testThisTypeDoesntMatchOthers(type);
                    testOtherTypesDontMatchThisId(type, type.id);
            }
        }

        int allLocationIds = PacketType.GAMESTAT.id + PacketType.CARTAHEAD.id
                + PacketType.STATUS.id + PacketType.PRE_MSG.id;
        assertEquals(0x01, PacketType.GAMESTAT.id);
        assertEquals(0x0f, allLocationIds);

        for (int locationIds = PacketType.GAMESTAT.id; locationIds <= allLocationIds; locationIds++) {
            Assert.assertTrue(PacketType.LOCATION.test(PacketType.LOCATION.id + locationIds));
            testOtherTypesDontMatchThisId(PacketType.LOCATION, PacketType.LOCATION.id + locationIds);
        }
    }

    private void testThisTypeDoesntMatchOthers(PacketType type) {
        System.out.println(type.name());
        Assert.assertTrue(type.test(type.id));

        //Make sure this packet type test doesn't succeed for other types ids
        for (PacketType other : PacketType.values()) {
            if (type != other) {
                Assert.assertFalse(type.test(other.id));
            }
        }
    }

    private void testOtherTypesDontMatchThisId(PacketType type, int id) {
        Assert.assertTrue(type.test(id));

        //Make sure other packet type test doesn't succeed for this id
        for (PacketType other : PacketType.values()) {
            if (type != other) {
                Assert.assertFalse(other.test(id));
            }
        }
    }

}
