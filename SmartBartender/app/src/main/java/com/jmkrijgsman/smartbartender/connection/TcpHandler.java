package com.jmkrijgsman.smartbartender.connection;

import android.util.Log;

import com.google.gson.Gson;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.connection.commands.IncomingCommandsCache;
import com.jmkrijgsman.smartbartender.ui.ConnectionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TcpHandler implements ConnectionCallback {
    private static final String LOGTAG = "TcpHandler";

    private String hostname;
    private int port;

    private final List<ConnectionCallback> callbacks;
    private TcpClient client;

    public TcpHandler() {
        this.callbacks = new ArrayList<>();
    }

    public void addCallback(ConnectionCallback connectionCallback)
    {
        this.callbacks.add(connectionCallback);
    }

    public void removeCallback(ConnectionCallback connectionCallback)
    {
        this.callbacks.remove(connectionCallback);
    }

    public void run(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;

        client = new TcpClient(hostname, port, this::onProgressUpdate, this);

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

    public void startRecipe(Recipe recipe)
    {
        try {
            JSONObject command = new JSONObject();
            JSONObject data = new JSONObject(new Gson().toJson(recipe));

            command.put("command", "StartRecipe");
            command.put("data", data);

            client.sendMessage(command.toString());
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    public void cleanPump(PumpConfiguration cfg, boolean runCleaning) {
        try {
            JSONObject command = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject pump = new JSONObject(new Gson().toJson(cfg));

            data.put("running", runCleaning);
            data.put("pump", pump);

            command.put("command", "RunPump");
            command.put("data", data);

            client.sendMessage(command.toString());
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private void onProgressUpdate(String value) {
        Log.d(LOGTAG, "response " + value);

        try
        {
            JSONObject response = new JSONObject(value);

            String command = response.getString("command");
            JSONObject data = response.getJSONObject("data");

            if (IncomingCommandsCache.commandsMap.containsKey(command))
                Objects.requireNonNull(IncomingCommandsCache.commandsMap.get(command)).Handle(data, this);
        } catch (JSONException e)
        {
            Log.w(LOGTAG, "Error in parsing json data: " + e);
        }
    }

    @Override
    public void OnConnectionChanged(boolean i) {
        this.callbacks.forEach(c -> c.OnConnectionChanged(i));
    }

    @Override
    public void OnRecipeChanged(boolean i, int p, Recipe r) {
        this.callbacks.forEach(c -> c.OnRecipeChanged(i, p, r));
    }
}
