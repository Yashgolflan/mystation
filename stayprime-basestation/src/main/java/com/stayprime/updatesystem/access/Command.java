package com.stayprime.updatesystem.access;

import java.io.IOException;

public interface Command {

    public String getCommand();

    public String getStdout() throws IOException;

    public String getStderr() throws IOException;

    public void writeStdin(String data) throws IOException;
    
    public Integer getExitStatus() throws IOException;

    public String getExitSignal() throws IOException;

}
