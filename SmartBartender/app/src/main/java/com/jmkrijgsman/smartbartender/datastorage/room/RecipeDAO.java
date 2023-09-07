package com.jmkrijgsman.smartbartender.datastorage.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDAO {
    @Query("SELECT * FROM Recipe")
    List<Recipe> getRecipes();

    @Query("SELECT * FROM DrinkAmount WHERE RecipeId =:recipeId")
    List<DrinkAmount> getDrinkAmountsWithRecipe(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRecipe(Recipe recipe);

    @Query("DELETE FROM DrinkAmount WHERE RecipeId =:recipeId")
    void removeDrinkAmounts(int recipeId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDrinkAmount(DrinkAmount drink);
}
