package com.jmkrijgsman.smartbartender.ui.recyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;
import com.jmkrijgsman.smartbartender.R;
import com.jmkrijgsman.smartbartender.datastorage.room.DrinkAmount;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.ui.MainActivity;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DrinkAmountAdapter extends RecyclerView.Adapter<DrinkAmountAdapter.DrinkAmountViewHolder> {
    private final List<DrinkAmount> drinkAmounts;

    public DrinkAmountAdapter(List<DrinkAmount> drinkAmounts) {
        this.drinkAmounts = drinkAmounts;
    }

    @NonNull
    @Override
    public DrinkAmountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.drink_amount_list_item, parent, false);
        return new DrinkAmountViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkAmountViewHolder holder, int position) {
        DrinkAmount selectedDrinkAmount = drinkAmounts.get(position);

        int totalMilliliters = drinkAmounts.stream().mapToInt(DrinkAmount::getAmountInMilliliters).sum();
        int currentMilliliters = selectedDrinkAmount.getAmountInMilliliters();

        holder.recipe = selectedDrinkAmount;
        holder.drinkNameTextView.setText(selectedDrinkAmount.getDrinkName());
        holder.drinkAmountTextView.setText(String.format(Locale.getDefault(), "%dml", selectedDrinkAmount.getAmountInMilliliters()));
        holder.drinkPercentageTextView.setText(String.format(Locale.getDefault(),"%d%%", (int)(((double)totalMilliliters / (double)currentMilliliters) * 100)));
        holder.amountSlider.setValue(selectedDrinkAmount.getAmountInMilliliters());
        holder.amountSlider.addOnChangeListener((s, v, b) -> selectedDrinkAmount.setAmountInMilliliters((int)v));
    }

    @Override
    public int getItemCount() {
        return drinkAmounts.size();
    }

    static class DrinkAmountViewHolder extends RecyclerView.ViewHolder {
        DrinkAmount recipe;

        TextView drinkNameTextView;
        TextView drinkAmountTextView;
        TextView drinkPercentageTextView;
        Slider amountSlider;

        public DrinkAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            drinkNameTextView = itemView.findViewById(R.id.drink_amount_select_item_name);
            drinkAmountTextView = itemView.findViewById(R.id.drink_amount_select_item_amount);
            drinkPercentageTextView = itemView.findViewById(R.id.drink_amount_select_item_percentage);
            amountSlider = itemView.findViewById(R.id.drink_amount_select_item_slider);
        }
    }

}
