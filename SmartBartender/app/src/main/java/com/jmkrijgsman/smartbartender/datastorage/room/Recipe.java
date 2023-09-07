package com.jmkrijgsman.smartbartender.datastorage.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @ColumnInfo(name = "Name")
    private String name;

    @Ignore
    private List<DrinkAmount> drinkAmounts;

    public List<DrinkAmount> getDrinkAmounts() {
        return drinkAmounts;
    }

    public void setDrinkAmounts(List<DrinkAmount> drinkAmounts) {
        this.drinkAmounts = drinkAmounts;
    }

    @Ignore
    public Recipe() {
        this.drinkAmounts = new ArrayList<>();
    }

    public Recipe(String name) {
        this.name = name;
        this.drinkAmounts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalAmount()
    {
        return drinkAmounts.stream().mapToInt(DrinkAmount::getAmountInMilliliters).sum();
    }

    public int getId() {
        return id;
    }
}