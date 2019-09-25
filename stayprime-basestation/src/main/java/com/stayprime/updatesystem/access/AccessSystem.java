/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem.access;

import java.io.IOException;

/**
 *
 * @author benjamin
 */
public interface AccessSystem {
    public boolean test();
    public void getFile(String remoteFile, String localDirectory) throws IOException;
    public void sendFile(String localFile, String remoteDirectory) throws IOException;
    public void stop();
    public Command runCommand(String command) throws IOException;
}
