package com.jmkrijgsman.smartbartender.ui;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jmkrijgsman.smartbartender.R;
import com.jmkrijgsman.smartbartender.connection.PumpConfiguration;
import com.jmkrijgsman.smartbartender.connection.PumpConfigurationCache;
import com.jmkrijgsman.smartbartender.connection.TcpHandler;
import com.jmkrijgsman.smartbartender.datastorage.AppDatabaseManager;
import com.jmkrijgsman.smartbartender.datastorage.room.AppDatabase;
import com.jmkrijgsman.smartbartender.datastorage.room.DrinkAmount;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.DrinkAmountAdapter;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.DrinkAmountCallback;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeFragment extends DialogFragment implements DrinkAmountCallback, ConnectionCallback {
    private TcpHandler handler;
    private final Recipe recipe;

    private BartenderCallback callback;
    private List<DrinkAmount> drinkAmounts;

    private RecyclerView rv;
    private DrinkAmountAdapter adapter;

    private EditText recipeNameText;
    private Button produceButton;
    private ProgressBar progressBar;

    public RecipeFragment() {
        this.recipe = new Recipe();
    }

    public RecipeFragment(BartenderCallback callback, TcpHandler handler)
    {
        this(callback, handler, new Recipe());
    }

    public RecipeFragment(BartenderCallback callback, TcpHandler handler, Recipe recipe) {
        this.callback = callback;
        this.handler = handler;
        this.recipe = recipe;
    }

    @Override
    public void onResume() {
        super.onResume();

        handler.addCallback(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private List<DrinkAmount> setDrinkAmounts() {
        List<DrinkAmount> list = new ArrayList<>(recipe.getDrinkAmounts());

        PumpConfigurationCache.getInstance().getPumpConfigurations().forEach((p) -> {
            if (!p.getDrink().equals("null") && list.stream().noneMatch(d -> d.getDrinkName().equals(p.getDrink())))
                list.add(new DrinkAmount(this.recipe, p));
        });

        this.drinkAmounts = list;
        return list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        if(dialog != null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }

        List<DrinkAmount> drinkAmounts = setDrinkAmounts();

        recipeNameText = requireView().findViewById(R.id.recipe_fragment_name_edit);
        recipeNameText.setText(recipe.getName());

        Button deleteButton = requireView().findViewById(R.id.recipe_fragment_delete_button);
        deleteButton.setOnClickListener(v -> {
            AppDatabaseManager.getInstance(getContext()).deleteRecipe(recipe);
            this.callback.OnRecipeDeleted(recipe);
            dismiss();
        });
        if (recipe.getId() == 0) deleteButton.setVisibility(View.GONE);

        this.progressBar = requireView().findViewById(R.id.recipe_fragment_produce_progress);
        Button saveButton = requireView().findViewById(R.id.recipe_fragment_save_button);
        saveButton.setOnClickListener(v -> {
            recipe.setName(recipeNameText.getText().toString());
            AppDatabaseManager.getInstance(getContext()).insertRecipe(recipe);

            dismiss();
            this.callback.OnRecipesChanged();
        });

        this.produceButton = requireView().findViewById(R.id.recipe_fragment_produce_button);
        this.produceButton.setOnClickListener(this::onProduceClicked);

        rv = requireView().findViewById(R.id.recipe_fragment_drink_amount_recyclerview);
        adapter = new DrinkAmountAdapter(drinkAmounts, this);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onPause() {
        this.handler.removeCallback(this);

        super.onPause();
    }

    private void onProduceClicked(View view) {
        handler.startRecipe(recipe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipefragment, container, false);
    }

    @Override
    public void onDrinkAmountSliderChanged(DrinkAmount drinkAmount) {
        recipe.setName(recipeNameText.getText().toString());

        List<DrinkAmount> nonEmptyDrinkAmounts = new ArrayList<>();
        drinkAmounts.forEach(d -> {
            if (d.getAmountInMilliliters() != 0)
                nonEmptyDrinkAmounts.add(d);
        });

        recipe.setDrinkAmounts(nonEmptyDrinkAmounts);

        for(int i = 0; i < adapter.getItemCount(); i++)
        {
            adapter.updateHolderPercentage(rv.findViewHolderForAdapterPosition(i), i);
        }
    }

    @Override
    public void OnConnectionChanged(boolean isConnected) {
        requireActivity().runOnUiThread(() -> produceButton.setEnabled(isConnected));
    }

    @Override
    public void OnRecipeChanged(boolean isProcessing, int progress, Recipe recipe) {
        requireActivity().runOnUiThread(() -> {
            produceButton.setEnabled(!isProcessing);
            progressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
            progressBar.setProgress(progress);
        });
    }
}