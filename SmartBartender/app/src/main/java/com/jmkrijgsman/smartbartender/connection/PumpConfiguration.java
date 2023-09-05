package com.jmkrijgsman.smartbartender.connection;

import org.json.JSONObject;

public class PumpConfiguration {
    private String name;
    private String drink;
    private int key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDrink() {
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public PumpConfiguration(String name, String drink, int key) {
        this.name = name;
        this.drink = drink;
        this.key = key;
    }

    public PumpConfiguration(JSONObject json)
    {
        this.name = json.optString("name");
        this.drink = json.optString("value");
        this.key = json.optInt("pin");
    }
}
