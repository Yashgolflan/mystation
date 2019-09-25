/*
 * 
 */

package com.stayprime.basestation2.instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ApplicationInstanceManager {
    private static final Logger log = LoggerFactory.getLogger(ApplicationInstanceManager.class);

    private static ApplicationInstanceListener subListener;

    /** Randomly chosen, but static, high socket number */
    public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 44331;

    /** Must end with newline */
    public static final String SINGLE_INSTANCE_SHARED_KEY = "$$NewInstance$$\n";
    private static ServerSocket socket;

    /**
     * Registers this instance of the application.
     * 
     * @return true if first instance, false if not.
     */
    public static void stop() {
	try {
            if (socket != null) {
                socket.close();
            }
	}
	catch (IOException ex) {
	    log.warn(ex.toString());
	}
    }

    public static boolean registerInstance() {
        return registerInstance(SINGLE_INSTANCE_NETWORK_SOCKET);
    }

    public static boolean registerInstance(int socketPort) {
        // returnValueOnError should be true if lenient (allows app to run on network error) or false if strict.
        boolean returnValueOnError = true;
        // try to open network socket
        // if success, listen to socket for new instance message, return true
        // if unable to open, connect to existing and send new instance message, return false
        try {
            socket = new ServerSocket(socketPort, 10, InetAddress
                    .getLocalHost());
            log.debug("Listening for application instances on socket " + socketPort);
            Thread instanceListenerThread = new Thread(new Runnable() {
                public void run() {
                    boolean socketClosed = false;
                    while (!socketClosed) {
                        if (socket.isClosed()) {
                            socketClosed = true;
                        } else {
                            try {
                                Socket client = socket.accept();
                                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                String message = in.readLine();
                                if (SINGLE_INSTANCE_SHARED_KEY.trim().equals(message.trim())) {
                                    log.debug("Shared key matched - new application instance found");
                                    fireNewInstance();
                                }
                                in.close();
                                client.close();
                            } catch (IOException e) {
                                socketClosed = true;
                            }
                        }
                    }
                }
            });
	    instanceListenerThread.setDaemon(true);
            instanceListenerThread.start();
            // listen
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            return returnValueOnError;
        } catch (IOException e) {
            log.debug("Port is already taken.  Notifying first instance.");
            try {
                Socket clientSocket = new Socket(InetAddress.getLocalHost(), socketPort);
                OutputStream out = clientSocket.getOutputStream();
                out.write(SINGLE_INSTANCE_SHARED_KEY.getBytes());
                out.close();
                clientSocket.close();
                log.debug("Successfully notified first instance.");
                return false;
            } catch (UnknownHostException e1) {
                log.error(e.getMessage(), e);
                return returnValueOnError;
            } catch (IOException e1) {
                log.error("Error connecting to local port for single instance notification");
                log.error(e1.getMessage(), e1);
                return returnValueOnError;
            }

        }
        return true;
    }

    public static void setApplicationInstanceListener(ApplicationInstanceListener listener) {
        subListener = listener;
    }

    private static void fireNewInstance() {
      if (subListener != null) {
        subListener.newInstanceCreated();
      }
  }

}
