package com.jmkrijgsman.smartbartender.datastorage.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Recipe.class, DrinkAmount.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDAO recipeDAO();
}
