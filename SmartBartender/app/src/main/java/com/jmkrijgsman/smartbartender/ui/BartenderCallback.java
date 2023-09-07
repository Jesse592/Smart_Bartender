package com.jmkrijgsman.smartbartender.ui;

import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;

public interface BartenderCallback {
    void OnRecipesChanged();
    void OnRecipeDeleted(Recipe recipe);
}