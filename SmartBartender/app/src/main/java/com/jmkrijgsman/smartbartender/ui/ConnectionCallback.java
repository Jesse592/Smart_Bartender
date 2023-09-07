package com.jmkrijgsman.smartbartender.ui;

import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;

public interface ConnectionCallback {
    void OnConnectionChanged(boolean isConnected);
    void OnRecipeChanged(boolean isProcessing, int progress, Recipe recipe);
}
