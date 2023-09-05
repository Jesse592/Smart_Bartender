package com.jmkrijgsman.smartbartender.connection;

import android.util.Log;

import com.jmkrijgsman.smartbartender.ui.BartenderCallback;
import com.jmkrijgsman.smartbartender.connection.commands.IncomingCommandsCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class TcpHandler {
    private static final String LOGTAG = "TcpHandler";

    private String hostname;
    private int port;

    private final BartenderCallback callback;
    private TcpClient client;

    public TcpHandler(BartenderCallback callback) {
        this.callback = callback;
    }

    public void run(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;

        client = new TcpClient(hostname, port, this::onProgressUpdate, this.callback);

        new Thread(() -> {
            while (true)
            {
                try {
                    client.run();
                } catch (IOException e) {
                    Log.e(LOGTAG, "Error TcpClient, retry connection" + e);
                }
            }
        }).start();
    }

    private void onProgressUpdate(String value) {
        Log.d(LOGTAG, "response " + value);

        try
        {
            JSONObject response = new JSONObject(value);

            String command = response.getString("command");
            JSONObject data = response.getJSONObject("data");

            if (IncomingCommandsCache.commandsMap.containsKey(command))
                Objects.requireNonNull(IncomingCommandsCache.commandsMap.get(command)).Handle(data, callback);
        } catch (JSONException e)
        {
            Log.w(LOGTAG, "Error in parsing json data: " + e);
        }
    }
}
