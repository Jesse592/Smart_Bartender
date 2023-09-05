package com.jmkrijgsman.smartbartender.ui.recyclerViews;

import com.jmkrijgsman.smartbartender.datastorage.room.DrinkAmount;

public interface DrinkAmountCallback {
    void onDrinkAmountSliderChanged(DrinkAmount drinkAmount);
}
