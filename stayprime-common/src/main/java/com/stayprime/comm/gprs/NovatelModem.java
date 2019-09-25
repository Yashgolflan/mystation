/*
 * 
 */
package com.stayprime.comm.gprs;

import com.stayprime.comm.io.MicrocomIO;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author benjamin
 */
public class NovatelModem extends GsmAtModem {
    private final File deviceFile;
    private final MicrocomIO ioStream;

    public NovatelModem(MicrocomIO ioStream) {
        super(ioStream);
        this.ioStream = ioStream;
        deviceFile = new File(ioStream.getDeviceFile());
    }

    @Override
    protected boolean sendInitializeCommands() throws IOException {
        boolean ok = super.sendInitializeCommands();

        AtOutputReader outputReader = getOutputReader();
        writeLine(AtConst.AT_NWICCID_RD);
        ok &= outputReader.waitForOk(AtConst.DELAY1);

        return ok;
    }

    @Override
    public synchronized boolean start() throws Exception {
        loadModemDrivers();
        if (deviceFile.exists()) {
            return super.start();
        }
        return false;
    }

    private void loadModemDrivers() {
        if (deviceFile.exists() == false) {
            try {
                log.info("Loading modem drivers");
                Process ps = Runtime.getRuntime().exec("modprobe option");
                String output = IOUtils.toString(ps.getInputStream());
                log.debug(output);
            }
            catch (IOException ex) {
                log.error(ex.toString());
                log.debug(ex.toString());
            }
        }
    }

}
