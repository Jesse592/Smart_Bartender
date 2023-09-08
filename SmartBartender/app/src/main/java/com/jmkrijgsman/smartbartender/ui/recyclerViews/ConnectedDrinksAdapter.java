package com.jmkrijgsman.smartbartender.ui.recyclerViews;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;
import com.jmkrijgsman.smartbartender.R;
import com.jmkrijgsman.smartbartender.connection.PumpConfiguration;
import com.jmkrijgsman.smartbartender.datastorage.room.DrinkAmount;

import java.util.List;
import java.util.Locale;

public class ConnectedDrinksAdapter extends RecyclerView.Adapter<ConnectedDrinksAdapter.ConnectedDrinksViewHolder> {
    private final List<PumpConfiguration> pumpConfigurations;
    private ConnectedDrinkCallback callback;

    public ConnectedDrinksAdapter(List<PumpConfiguration> pumpConfigurations, ConnectedDrinkCallback callback) {
        this.pumpConfigurations = pumpConfigurations;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ConnectedDrinksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_drink_list_item, parent, false);
        return new ConnectedDrinksViewHolder(viewItem);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ConnectedDrinksViewHolder holder, int position) {
        PumpConfiguration cfg = pumpConfigurations.get(position);

        if (cfg.getDrink().equals("null")) holder.drinkNameTextView.setText("");
        else holder.drinkNameTextView.setText(cfg.getDrink());

        holder.pumpNameTextView.setText(cfg.getName());
        holder.cleanButton.setOnTouchListener((view, motionEvent) ->
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                this.callback.onCleanButtonPressed(cfg, true);
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                this.callback.onCleanButtonPressed(cfg, false);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return pumpConfigurations.size();
    }

    static class ConnectedDrinksViewHolder extends RecyclerView.ViewHolder {
        TextView pumpNameTextView;
        TextView drinkNameTextView;
        Button cleanButton;

        public ConnectedDrinksViewHolder(@NonNull View itemView) {
            super(itemView);
            pumpNameTextView = itemView.findViewById(R.id.connected_drinks_pump_name);
            drinkNameTextView = itemView.findViewById(R.id.connected_drinks_drink_name);
            cleanButton = itemView.findViewById(R.id.connected_drinks_clean_button);
        }
    }

}
