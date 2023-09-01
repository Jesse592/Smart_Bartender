package com.jmkrijgsman.smartbartender.connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {
    private static final String LOGTAG = "TCP-Client";

    private final String hostname;
    private final int port;

    private TcpMessageListener messageListener;
    private boolean isRunning = false;

    private PrintWriter outBuffer;
    private BufferedReader inBuffer;

    public TcpClient(String hostname, int port, TcpMessageListener listener)
    {
        this.hostname = hostname;
        this.port = port;
        this.messageListener = listener;
    }

    public void run() {
        isRunning = true;

        try {
            Log.d("TCP Client", "C: Connecting...");

            InetAddress serverAddress = InetAddress.getByName(this.hostname);

            try (Socket socket = new Socket(serverAddress, this.port)) {
                outBuffer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                inBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (isRunning) {
                    String message = inBuffer.readLine();

                    if (message != null && messageListener != null)
                        messageListener.OnMessageReceived(message);
                }

                Log.d(LOGTAG, "Received Message: '" + messageListener + "'");

            } catch (Exception e) {
                Log.e(LOGTAG, "Error in client", e);
            }
        } catch (Exception e) {
            Log.e(LOGTAG, "Error in client", e);
        } finally {
            close();
        }

    }

    public void sendMessage(final String message) {
        Runnable runnable = () -> {
            if (outBuffer != null) {
                Log.d(LOGTAG, "Sending: " + message);
                outBuffer.println(message);
                outBuffer.flush();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void close() {
        isRunning = false;

        if (outBuffer != null) {
            outBuffer.flush();
            outBuffer.close();
        }

        messageListener = null;
        inBuffer = null;
        outBuffer = null;
    }

}
