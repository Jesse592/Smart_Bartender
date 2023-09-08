package com.jmkrijgsman.smartbartender.ui.recyclerViews;

import com.jmkrijgsman.smartbartender.connection.PumpConfiguration;

public interface ConnectedDrinkCallback {
    void onCleanButtonPressed(PumpConfiguration cfg, boolean runClean);
}
