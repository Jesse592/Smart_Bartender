package com.jmkrijgsman.smartbartender.datastorage.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.jmkrijgsman.smartbartender.connection.PumpConfiguration;

@Entity(primaryKeys = {"RecipeName","DrinkName"})
public class DrinkAmount {

    @ColumnInfo(name = "RecipeName")
    @NonNull
    private String recipeName;

    @ColumnInfo(name = "DrinkName")
    @NonNull
    private String drinkName;

    @ColumnInfo(name = "Amount")
    private int amountInMilliliters;

    public DrinkAmount(String recipeName, String drinkName, int amountInMilliliters) {
        this.recipeName = recipeName;
        this.drinkName = drinkName;
        this.amountInMilliliters = amountInMilliliters;
    }

    @Ignore
    public DrinkAmount(Recipe recipe, PumpConfiguration pumpConfiguration)
    {
        this.recipeName = recipe.getName();
        this.drinkName = pumpConfiguration.getDrink();
        this.amountInMilliliters = 0;
    }


    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public int getAmountInMilliliters() {
        return amountInMilliliters;
    }

    public void setAmountInMilliliters(int amountInMilliliters) {
        this.amountInMilliliters = amountInMilliliters;
    }
}
