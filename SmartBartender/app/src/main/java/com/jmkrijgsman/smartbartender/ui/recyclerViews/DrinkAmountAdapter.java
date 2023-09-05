package com.jmkrijgsman.smartbartender.ui.recyclerViews;

import android.util.Log;
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
    private DrinkAmountCallback callback;

    public DrinkAmountAdapter(List<DrinkAmount> drinkAmounts, DrinkAmountCallback callback) {
        this.drinkAmounts = drinkAmounts;
        this.callback = callback;
    }

    @NonNull
    @Override
    public DrinkAmountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.drink_amount_list_item, parent, false);
        return new DrinkAmountViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkAmountViewHolder holder, int position) {
        holder.drinkAmount = drinkAmounts.get(position);

        holder.drinkAmountTextView.setText(String.format(Locale.getDefault(), "%dml", holder.drinkAmount.getAmountInMilliliters()));
        holder.drinkNameTextView.setText(holder.drinkAmount.getDrinkName());
        holder.amountSlider.setValue(holder.drinkAmount.getAmountInMilliliters());

        holder.amountSlider.addOnChangeListener((slider, v, b) -> {
            holder.amountSlider.setValue(holder.drinkAmount.getAmountInMilliliters());
            holder.drinkAmountTextView.setText(String.format(Locale.getDefault(), "%dml", holder.drinkAmount.getAmountInMilliliters()));
            holder.drinkAmount.setAmountInMilliliters((int)v);

            callback.onDrinkAmountSliderChanged(holder.drinkAmount);
        });
    }

    public void updateHolderPercentage(RecyclerView.ViewHolder holder, int position)
    {
        DrinkAmountViewHolder drinkAmountViewHolder = (DrinkAmountViewHolder)holder;

        int totalMilliliters = drinkAmounts.stream().mapToInt(DrinkAmount::getAmountInMilliliters).sum();
        int currentMilliliters =  drinkAmounts.get(position).getAmountInMilliliters();

        if (totalMilliliters != 0)
            drinkAmountViewHolder.drinkPercentageTextView.setText(String.format(Locale.getDefault(),"%d%%", (int)(((double)currentMilliliters / (double)totalMilliliters) * 100)));
    }

    @Override
    public int getItemCount() {
        return drinkAmounts.size();
    }

    static class DrinkAmountViewHolder extends RecyclerView.ViewHolder {
        DrinkAmount drinkAmount;

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
