package com.jmkrijgsman.smartbartender.connection;

import android.util.Log;

import com.jmkrijgsman.smartbartender.ui.BartenderCallback;
import com.jmkrijgsman.smartbartender.ui.ConnectionCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {
    private static final String LOGTAG = "TCP-Client";

    private final String hostname;
    private final int port;

    private TcpMessageListener messageListener;
    private final ConnectionCallback callback;
    private boolean isRunning = false;

    private PrintWriter outBuffer;
    private BufferedReader inBuffer;

    public TcpClient(String hostname, int port, TcpMessageListener listener, ConnectionCallback callback)
    {
        this.hostname = hostname;
        this.port = port;
        this.messageListener = listener;
        this.callback = callback;
    }

    public void run() throws IOException {
        isRunning = true;

        try {
            Log.d("TCP Client", "C: Connecting...");

            InetSocketAddress serverAddress = new InetSocketAddress(InetAddress.getByName(this.hostname), this.port);

            Socket socket = new Socket();
            socket.connect(serverAddress, 5_000);

            outBuffer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            inBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            callback.OnConnectionChanged(true);

            while (isRunning) {
                String message = inBuffer.readLine();

                if (message != null && messageListener != null)
                    messageListener.OnMessageReceived(message);
            }

            socket.close();
        } catch (UnknownHostException e) {
            Log.e(LOGTAG, "Error in client", e);
        } finally {
            callback.OnConnectionChanged(false);
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

        inBuffer = null;
        outBuffer = null;
    }

}
