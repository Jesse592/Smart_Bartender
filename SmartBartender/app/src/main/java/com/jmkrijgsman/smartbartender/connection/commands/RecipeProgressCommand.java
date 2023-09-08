package com.jmkrijgsman.smartbartender.connection.commands;

import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.ui.ConnectionCallback;

import org.json.JSONObject;

public class RecipeProgressCommand implements IncomingCommand {
    public static final String command = "RecipeChanged";

    @Override
    public String getCommand() { return command; }

    @Override
    public void Handle(JSONObject data, ConnectionCallback callback) {
        JSONObject recipeData = data.optJSONObject("recipe");

        if (recipeData != null)
            callback.OnRecipeChanged(data.optBoolean("isProcessing"), data.optInt("progress", 0), new Recipe(recipeData));
    }
}
