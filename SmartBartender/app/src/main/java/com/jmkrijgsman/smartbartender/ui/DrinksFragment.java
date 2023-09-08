package com.jmkrijgsman.smartbartender.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jmkrijgsman.smartbartender.R;
import com.jmkrijgsman.smartbartender.connection.PumpConfiguration;
import com.jmkrijgsman.smartbartender.connection.PumpConfigurationCache;
import com.jmkrijgsman.smartbartender.connection.TcpHandler;
import com.jmkrijgsman.smartbartender.datastorage.AppDatabaseManager;
import com.jmkrijgsman.smartbartender.datastorage.room.DrinkAmount;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.ConnectedDrinkCallback;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.ConnectedDrinksAdapter;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.DrinkAmountAdapter;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.DrinkAmountCallback;

import java.util.ArrayList;
import java.util.List;

public class DrinksFragment extends DialogFragment implements ConnectionCallback, ConnectedDrinkCallback {
    private TcpHandler handler;

    private RecyclerView rv;
    private ConnectedDrinksAdapter adapter;

    public DrinksFragment(TcpHandler handler)
    {
        this.handler = handler;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        if(dialog != null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }

        rv = requireView().findViewById(R.id.recipe_fragment_connected_drinks_recyclerview);
        adapter = new ConnectedDrinksAdapter(PumpConfigurationCache.getInstance().getPumpConfigurations(), this);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onPause() {
        this.handler.removeCallback(this);

        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drinksfragment, container, false);
    }

    @Override
    public void OnConnectionChanged(boolean isConnected) {
    }

    @Override
    public void OnRecipeChanged(boolean isProcessing, int progress, Recipe recipe) {

    }

    @Override
    public void onCleanButtonPressed(PumpConfiguration cfg, boolean runCleaning) {
        this.handler.cleanPump(cfg, runCleaning);
    }
}