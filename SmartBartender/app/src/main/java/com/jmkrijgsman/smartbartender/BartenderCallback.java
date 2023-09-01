package com.jmkrijgsman.smartbartender;

import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;

public interface BartenderCallback {
    void OnConnectionChanged(boolean isConnected);
    void OnRecipeChanged(boolean isProcessing , Recipe recipe);
}
