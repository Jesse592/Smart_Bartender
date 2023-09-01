package com.jmkrijgsman.smartbartender.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.jmkrijgsman.smartbartender.BartenderCallback;
import com.jmkrijgsman.smartbartender.connection.commands.IncomingCommandsCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class TcpHandler {
    private static final String LOGTAG = "TcpHandler";

    private String hostname;
    private int port;

    private BartenderCallback callback;
    private TcpClient client;

    public TcpHandler(BartenderCallback callback) {
        this.callback = callback;
    }

    public void run(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;

        new TcpClientTask().execute("");
    }

    public class TcpClientTask extends AsyncTask<String, String, TcpClient> {
        @Override
        protected TcpClient doInBackground(String... messages) {
            client = new TcpClient(hostname, port, this::publishProgress);
            client.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(LOGTAG, "response " + values[0]);

            try
            {
                JSONObject response = new JSONObject(values[0]);

                String command = response.getString("command");
                JSONObject data = response.getJSONObject("data");

                if (IncomingCommandsCache.commandsMap.containsKey(command))
                    Objects.requireNonNull(IncomingCommandsCache.commandsMap.get(command)).Handle(data, callback);

            } catch (JSONException e)
            {
                Log.w(LOGTAG, "Error in parsing data to json " + e);
            }
        }
    }
}
