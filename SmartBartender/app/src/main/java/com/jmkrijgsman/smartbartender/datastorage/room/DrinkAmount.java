package com.jmkrijgsman.smartbartender.datastorage.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.jmkrijgsman.smartbartender.connection.PumpConfiguration;

@Entity(primaryKeys = {"RecipeId","DrinkName"})
public class DrinkAmount {

    @ColumnInfo(name = "RecipeId")
    @NonNull
    private int recipeId;

    @ColumnInfo(name = "DrinkName")
    @NonNull
    private String drinkName;

    @ColumnInfo(name = "Amount")
    private int amountInMilliliters;

    public DrinkAmount(int recipeId, String drinkName, int amountInMilliliters) {
        this.recipeId = recipeId;
        this.drinkName = drinkName;
        this.amountInMilliliters = amountInMilliliters;
    }

    @Ignore
    public DrinkAmount(Recipe recipe, PumpConfiguration pumpConfiguration)
    {
        this.recipeId = recipe.getId();
        this.drinkName = pumpConfiguration.getDrink();
        this.amountInMilliliters = 0;
    }


    public int getRecipeId() {
        return this.recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
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
