/*
 * 
 */
package com.stayprime.comm;

import java.net.SocketAddress;

/**
 *
 * @author benjamin
 */
public interface PacketSender {

    /**
     * Send a packet to a predefined address (implementation dependent).
     * The implementation may not support this version of sendPacket and
     * return false.
     * @param packet the packet to send
     * @return true if the packet was sent successfully
     */
    public boolean sendPacket(BytePacket packet);

    /**
     * Send a packet to a specified address.
     * The implementation may not support this version of sendPacket and
     * return false.
     * @param packet the packet to send
     * @param address the address to send the packet to
     * @return true if the packet was sent successfully
     */
    public boolean sendPacket(BytePacket packet, SocketAddress address);

}
