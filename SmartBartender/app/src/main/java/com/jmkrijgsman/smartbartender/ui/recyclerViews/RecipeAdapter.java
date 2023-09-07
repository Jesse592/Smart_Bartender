package com.jmkrijgsman.smartbartender.ui.recyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jmkrijgsman.smartbartender.R;
import com.jmkrijgsman.smartbartender.datastorage.room.DrinkAmount;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.ui.MainActivity;
import com.jmkrijgsman.smartbartender.ui.RecipeFragment;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private static final String LOGTAG = "RecipeAdapter";

    private final List<Recipe> recipes;
    private final MainActivity mainActivity;

    public RecipeAdapter(List<Recipe> recipes, MainActivity mainActivity) {
        this.recipes = recipes;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_account_list_item, parent, false);
        return new RecipeViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe selectedRecipe = recipes.get(position);
        holder.recipe = selectedRecipe;
        holder.recipeNameTextView.setText(selectedRecipe.getName());
        holder.recipeDrinksTextView.setText(selectedRecipe.getDrinkAmounts().stream().map(DrinkAmount::getDrinkName).collect(Collectors.joining(", ")));
        holder.recipeAmountTextView.setText(String.format(Locale.getDefault(), "%dml", selectedRecipe.getTotalAmount()));

        holder.itemView.setOnClickListener(v -> {
            new RecipeFragment(mainActivity, selectedRecipe).show(mainActivity.getSupportFragmentManager(), LOGTAG);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Recipe recipe;
        TextView recipeNameTextView;
        TextView recipeDrinksTextView;
        TextView recipeAmountTextView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipe_list_item_name);
            recipeDrinksTextView = itemView.findViewById(R.id.recipe_list_item_drinks);
            recipeAmountTextView = itemView.findViewById(R.id.recipe_list_item_amount);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
