package com.jmkrijgsman.smartbartender.ui;

import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;

public interface BartenderCallback {
    void OnConnectionChanged(boolean isConnected);
    void OnRecipeChanged(boolean isProcessing , Recipe recipe);
}