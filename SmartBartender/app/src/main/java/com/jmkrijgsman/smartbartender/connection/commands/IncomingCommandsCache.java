package com.jmkrijgsman.smartbartender.connection.commands;

import java.util.HashMap;
import java.util.Map;

public class IncomingCommandsCache {
    public static Map<String, IncomingCommand> commandsMap = createMap();

    private static Map<String, IncomingCommand> createMap() {
        HashMap<String, IncomingCommand> map = new HashMap<>();
        commandsMap.put(SetConnectedDrinksCommand.command, new SetConnectedDrinksCommand());

        return map;
    }

}
