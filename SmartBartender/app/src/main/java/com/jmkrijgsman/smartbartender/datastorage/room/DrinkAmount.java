package com.jmkrijgsman.smartbartender.datastorage.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

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
