package com.example.allin1fitnessadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.allin1fitnessadmin.Adapter.RecipesAdapter;
import com.example.allin1fitnessadmin.Model.Recipes;
import com.example.allin1fitnessadmin.Utils.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton fab;
    RecyclerView rv;
    ProgressBar pb;
    RelativeLayout layout;

    RecipesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();

    }

    private void initUi() {
        fab = (FloatingActionButton) findViewById(R.id.fab_recipes);
        layout = (RelativeLayout) findViewById(R.id.layout_recipes);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rv = (RecyclerView) findViewById(R.id.rv_recipes);
        pb = (ProgressBar) findViewById(R.id.pb_recipes);
        toolbar.setTitle("Recipes");
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        setSupportActionBar(toolbar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddRecipes.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        pb.setVisibility(View.VISIBLE);
        List<Recipes> recipesList = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference("Recipes")
                .orderByChild("rid");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("res", snapshot.toString());
                rv.setAdapter(null);
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Recipes yourModel = childSnapshot.getValue(Recipes.class);
                    childSnapshot.getValue();
                    recipesList.add(yourModel);
                }
                if (!recipesList.isEmpty()) {
                    adapter = new RecipesAdapter(recipesList, getApplicationContext());
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                } else {
                    Log.d("res", "nodata");
                    pb.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(layout, "No Recipes Found", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            Util.setSP(getApplicationContext(), "");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}