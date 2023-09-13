package com.jmkrijgsman.smartbartender.ui.recyclerViews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private Activity activity;

    public ConnectedDrinksAdapter(List<PumpConfiguration> pumpConfigurations, ConnectedDrinkCallback callback, Activity activity) {
        this.pumpConfigurations = pumpConfigurations;
        this.callback = callback;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ConnectedDrinksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_drink_list_item, parent, false);

        ConnectedDrinksViewHolder holder = new ConnectedDrinksViewHolder(viewItem);
        holder.editText = new EditText(this.activity);
        return holder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ConnectedDrinksViewHolder holder, int position) {
        PumpConfiguration cfg = pumpConfigurations.get(position);

        if (cfg.getDrink().equals("null")) holder.drinkNameTextView.setText("");
        else holder.drinkNameTextView.setText(cfg.getDrink());

        holder.nameEditButton.setOnClickListener(v -> editDrinkName(cfg, holder));

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

    private void editDrinkName(PumpConfiguration cfg, ConnectedDrinksViewHolder holder) {
        holder.editText.setHint(cfg.getDrink());

        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle("Update drink in " + cfg.getName())
                .setView(holder.editText)
                .setPositiveButton("Save", (dialogInterface, i) -> updateConnectedDrink(cfg, holder))
                .setNegativeButton("Cancel", (d,i) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateConnectedDrink(PumpConfiguration cfg, ConnectedDrinksViewHolder holder) {
        if (cfg.getDrink().contentEquals(holder.editText.getText()))
            return;

        holder.drinkNameTextView.setText(holder.editText.getText());

        cfg.setDrink(String.valueOf(holder.editText.getText()));
        callback.updateConnectedDrink(cfg);
    }

    @Override
    public int getItemCount() {
        return pumpConfigurations.size();
    }

    static class ConnectedDrinksViewHolder extends RecyclerView.ViewHolder {
        TextView pumpNameTextView;
        TextView drinkNameTextView;
        Button cleanButton;
        ImageButton nameEditButton;
        EditText editText;

        public ConnectedDrinksViewHolder(@NonNull View itemView) {
            super(itemView);
            pumpNameTextView = itemView.findViewById(R.id.connected_drinks_pump_name);
            drinkNameTextView = itemView.findViewById(R.id.connected_drinks_drink_name);
            cleanButton = itemView.findViewById(R.id.connected_drinks_clean_button);
            nameEditButton = itemView.findViewById(R.id.connected_drinks_name_edit_button);
        }
    }

}
