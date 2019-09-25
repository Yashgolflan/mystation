/*
 *
 */
package com.stayprime.comm.io;

import com.stayprime.spe.SPEConst;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class MicrocomIO extends CommandIOStream {
    private static final Logger log = LoggerFactory.getLogger(MicrocomIO.class);

    private String deviceName;
    private String deviceFile;

    private int baudRate;
    private File lockFile;

    private OutputStream out;

    public MicrocomIO(String deviceName, int baudRate) {
        this.deviceName = deviceName;
        this.baudRate = baudRate;

        deviceFile = SPEConst.dev + deviceName;
        setCommand(SPEConst.microcomCommand + baudRate + " " + deviceFile);
        lockFile = new File(SPEConst.lockFilePrefix + deviceName);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceFile() {
        return deviceFile;
    }

    public int getBaudRate() {
        return baudRate;
    }

    @Override
    public void start() throws IOException {
        checkLockFile();
        super.start();
        if (super.getOutputStream() != null) {
            this.out = new PausedOutputStream(super.getOutputStream(), 2);
        }
    }

    @Override
    public void stop() {
        super.stop();
        lockFile.delete();
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    public void checkLockFile() {
        try {
            if (lockFile.exists()) {
                String process = FileUtils.readFileToString(lockFile);
                File commandFile = new File(SPEConst.proc + process + SPEConst.proc_comm);
                if (commandFile.exists() == false) {
                    lockFile.delete();
                }

                // not required anymore as we are deleting the lockfile if it exists
//                String command = FileUtils.readFileToString(commandFile);
//                if (ObjectUtils.equals(command, SPEConst.microcom)) {
//                    String killProcessCommand = SPEConst.killCommand + process;
//                    Runtime.getRuntime().exec(killProcessCommand).waitFor();
//                }
            }
        }
//        catch (InterruptedException ex) {
//        }
        catch (IOException ex) {
            log.warn(ex.toString());
        }
    }
}
