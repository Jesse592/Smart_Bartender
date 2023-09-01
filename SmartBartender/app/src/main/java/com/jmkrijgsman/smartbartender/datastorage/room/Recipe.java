package com.jmkrijgsman.smartbartender.datastorage.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.List;
import java.util.Map;

@Entity
public class Recipe {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Name")
    private final String name;

    @Ignore
    private List<DrinkAmount> drinkAmounts;

    public List<DrinkAmount> getDrinkAmounts() {
        return drinkAmounts;
    }

    public void setDrinkAmounts(List<DrinkAmount> drinkAmounts) {
        this.drinkAmounts = drinkAmounts;
    }

    public Recipe(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}