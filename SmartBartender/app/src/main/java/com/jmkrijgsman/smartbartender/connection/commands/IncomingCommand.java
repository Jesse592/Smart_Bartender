package com.jmkrijgsman.smartbartender.connection.commands;

import com.jmkrijgsman.smartbartender.ui.BartenderCallback;
import com.jmkrijgsman.smartbartender.ui.ConnectionCallback;

import org.json.JSONObject;

public interface IncomingCommand {
    String getCommand();
    void Handle(JSONObject data, ConnectionCallback callback);
}