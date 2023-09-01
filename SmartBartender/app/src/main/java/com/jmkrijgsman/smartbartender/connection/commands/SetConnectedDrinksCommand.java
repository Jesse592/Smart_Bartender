package com.jmkrijgsman.smartbartender.connection.commands;

import com.jmkrijgsman.smartbartender.BartenderCallback;

import org.json.JSONObject;

public class SetConnectedDrinksCommand implements IncomingCommand{
    public static final String command = "ConnectedDrinks";

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void Handle(JSONObject data, BartenderCallback callback) {
        //TODO: handle
    }
}
