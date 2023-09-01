package com.jmkrijgsman.smartbartender.datastorage.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDAO {
    @Query("SELECT * FROM Recipe")
    List<Recipe> getRecipes();

    @Query("SELECT * FROM DrinkAmount WHERE RecipeName =:recipeName")
    List<DrinkAmount> getDrinkAmountsWithRecipe(String recipeName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDrinkAmount(DrinkAmount drink);
}
