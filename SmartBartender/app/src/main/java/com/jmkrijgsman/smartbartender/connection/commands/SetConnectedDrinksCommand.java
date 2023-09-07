package com.jmkrijgsman.smartbartender.connection.commands;

import com.jmkrijgsman.smartbartender.connection.PumpConfiguration;
import com.jmkrijgsman.smartbartender.connection.PumpConfigurationCache;
import com.jmkrijgsman.smartbartender.ui.BartenderCallback;
import com.jmkrijgsman.smartbartender.ui.ConnectionCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetConnectedDrinksCommand implements IncomingCommand{
    public static final String command = "ConnectedDrinks";

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void Handle(JSONObject data, ConnectionCallback callback) {
        List<PumpConfiguration> configurations = new ArrayList<>();

        data.keys().forEachRemaining(p -> {
            JSONObject o = data.optJSONObject(p);
            if (o != null) configurations.add(new PumpConfiguration(o));
        });

        PumpConfigurationCache.getInstance().setPumpConfigurations(configurations);
    }
}
