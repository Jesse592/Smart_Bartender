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

import com.jmkrijgsman.smartbartender.R;
import com.jmkrijgsman.smartbartender.connection.PumpConfiguration;
import com.jmkrijgsman.smartbartender.connection.PumpConfigurationCache;
import com.jmkrijgsman.smartbartender.datastorage.room.DrinkAmount;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.DrinkAmountAdapter;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeFragment extends DialogFragment {
    private final Recipe recipe;

    public RecipeFragment() {
        this.recipe = new Recipe();
    }

    public RecipeFragment(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private List<DrinkAmount> getDrinkAmounts() {
        List<DrinkAmount> list = new ArrayList<>(recipe.getDrinkAmounts());

        PumpConfigurationCache.getInstance().getPumpConfigurations().forEach((p) -> {
            if (!p.getDrink().equals("null"))
                list.add(new DrinkAmount(this.recipe, p));
        });

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

        List<DrinkAmount> drinkAmounts = getDrinkAmounts();

        RecyclerView rv = requireView().findViewById(R.id.recipe_fragment_drink_amount_recyclerview);
        DrinkAmountAdapter adapter = new DrinkAmountAdapter(drinkAmounts);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipefragment, container, false);
    }
}