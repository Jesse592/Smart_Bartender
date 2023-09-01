package com.jmkrijgsman.smartbartender.connection.commands;

import com.jmkrijgsman.smartbartender.BartenderCallback;
import org.json.JSONObject;

public interface IncomingCommand {
    String getCommand();
    void Handle(JSONObject data, BartenderCallback callback);
}