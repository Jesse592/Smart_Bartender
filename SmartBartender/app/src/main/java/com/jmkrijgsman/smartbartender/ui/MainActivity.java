package com.jmkrijgsman.smartbartender.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmkrijgsman.smartbartender.R;
import com.jmkrijgsman.smartbartender.connection.TcpHandler;
import com.jmkrijgsman.smartbartender.datastorage.AppDatabaseManager;
import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;
import com.jmkrijgsman.smartbartender.ui.recyclerViews.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BartenderCallback, ConnectionCallback {
    private static final String LOGTAG = "MainActivity";

    //private static final String hostname = "192.168.4.1";
    private static final String hostname = "145.49.12.93";
    private static final int port = 65432;

    private final List<Recipe> recipes = new ArrayList<>();

    public TcpHandler tcpHandler;
    private RecyclerView rv;

    private FloatingActionButton drinksFAB;
    private ProgressBar connectedProgressBar;
    private ProgressBar mixProgressBar;
    private ImageView connectedBar;
    private TextView connectedTextView;
    private TextView mixTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedBar = findViewById(R.id.main_activity_bottom_bar);
        connectedProgressBar = findViewById(R.id.main_activity_bottom_bar_spinner);
        mixProgressBar = findViewById(R.id.main_activity_bottom_bar_progress);
        connectedTextView = findViewById(R.id.main_activity_bottom_bar_text);
        mixTextView = findViewById(R.id.main_activity_bottom_bar_mix_text);
        drinksFAB = findViewById(R.id.main_activity_drinks_FAB);

        this.tcpHandler = new TcpHandler();
        this.tcpHandler.addCallback(this);

        tcpHandler.run(hostname, port);

        rv = findViewById(R.id.main_recyclerview);
        RecipeAdapter adapter = new RecipeAdapter(recipes, this);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();

        OnRecipesChanged();
    }

    public void onAddRecipeClicked(View view) {
        new RecipeFragment(this, tcpHandler).show(getSupportFragmentManager(), LOGTAG);
    }

    @Override
    public void OnConnectionChanged(boolean isConnected) {
        runOnUiThread(() -> {
            mixProgressBar.setVisibility(View.INVISIBLE);
            mixTextView.setVisibility(View.INVISIBLE);
            connectedTextView.setVisibility(View.VISIBLE);


            if (isConnected)
            {
                connectedProgressBar.setVisibility(View.INVISIBLE);
                drinksFAB.setVisibility(View.VISIBLE);
                connectedBar.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tcp_connection_connected, getApplicationContext().getTheme()));
                connectedTextView.setText(R.string.connected_text);
            } else
            {
                connectedProgressBar.setVisibility(View.VISIBLE);
                drinksFAB.setVisibility(View.GONE);
                connectedBar.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tcp_connection_not_connected, getApplicationContext().getTheme()));
                connectedTextView.setText(R.string.not_connected_text);
            }
        });
    }

    @Override
    public void OnRecipeChanged(boolean isProcessing, int progress, Recipe recipe) {
        if (isProcessing)
        {
            runOnUiThread(() -> {

                if (connectedTextView.getVisibility() == View.VISIBLE)
                {
                    connectedTextView.setVisibility(View.INVISIBLE);
                    connectedProgressBar.setVisibility(View.INVISIBLE);
                    mixTextView.setVisibility(View.VISIBLE);
                    mixProgressBar.setVisibility(View.VISIBLE);
                }

                mixTextView.setText(String.format("Mixing %s", recipe.getName()));
                mixProgressBar.setProgress(progress);
            });
        } else
            OnConnectionChanged(true);
    }

    @Override
    public void OnRecipesChanged() {
        new Thread(() -> {
            AppDatabaseManager manager = AppDatabaseManager.getInstance(getApplicationContext());
            this.recipes.clear();

            List<Recipe> tempList = manager.getRecipes();

            this.recipes.addAll(tempList);

            runOnUiThread(() -> Objects.requireNonNull(rv.getAdapter()).notifyItemRangeChanged(0,tempList.size()));
        }).start();
    }

    public void OnRecipeDeleted(Recipe recipe)
    {
        this.recipes.remove(recipe);
        runOnUiThread(() -> Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged());
    }

    public void onDrinksClicked(View view) {
        new DrinksFragment(tcpHandler).show(getSupportFragmentManager(), LOGTAG);
    }
}