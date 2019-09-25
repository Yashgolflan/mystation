/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.comm;

/**
 *
 * @author stayprime
 */
public interface Encodable {

    BytePacket encode(int siteId);

}
