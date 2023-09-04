package com.jmkrijgsman.smartbartender.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jmkrijgsman.smartbartender.R;
import com.jmkrijgsman.smartbartender.connection.TcpHandler;
import com.jmkrijgsman.smartbartender.datastorage.AppDatabaseManager;
import com.jmkrijgsman.smartbartender.datastorage.room.DrinkAmount;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.ui.recyclerView.RecipeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String LOGTAG = "MainActivity";

    //private static final String hostname = "192.168.178.213";
    private static final String hostname = "145.49.41.13";
    private static final int port = 65432;

    private final List<Recipe> recipes = new ArrayList<>();

    private RecyclerView rv;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new TcpHandler(null).run(hostname, port);

        rv = findViewById(R.id.main_recyclerview);
        adapter = new RecipeAdapter(recipes, this);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(() -> {
            AppDatabaseManager manager = AppDatabaseManager.getInstance(getApplicationContext());
            this.recipes.clear();

            List<Recipe> tempList = manager.getRecipes();

            this.recipes.addAll(tempList);

            runOnUiThread(() -> Objects.requireNonNull(rv.getAdapter()).notifyItemRangeChanged(0,tempList.size()));
        }).start();
    }

    public void onAddAccountClicked(View view) {
    }
}