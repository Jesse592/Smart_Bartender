package com.jmkrijgsman.smartbartender.datastorage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jmkrijgsman.smartbartender.datastorage.room.AppDatabase;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;

import java.util.List;


public class AppDatabaseManager implements DataStorage{
    private static AppDatabaseManager databaseManager;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    public static AppDatabaseManager getInstance(Context context)
    {
        if (databaseManager == null) databaseManager = new AppDatabaseManager(context);

        return databaseManager;
    }

    public AppDatabaseManager(Context context)
    {
        database = Room.databaseBuilder(context, AppDatabase.class, "TOTP_Authenticator_DB")
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    private final AppDatabase database;

    @Override
    public List<Recipe> getRecipes() {
        List<Recipe> recipes = database.recipeDAO().getRecipes();
        recipes.forEach(r -> r.setDrinkAmounts(database.recipeDAO().getDrinkAmountsWithRecipe(r.getId())));
        return recipes;
    }

    @Override
    public void insertRecipe(Recipe recipe) {
        new Thread(() ->
        {
            long id = database.recipeDAO().insertRecipe(recipe);
            recipe.getDrinkAmounts().forEach(r -> {
                r.setRecipeId((int)id);
                database.recipeDAO().insertDrinkAmount(r);
            });
        }).start();
    }
}