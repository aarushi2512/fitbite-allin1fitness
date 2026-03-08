package com.example.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.Adapter.ItemAdapter;
import com.example.Adapter.SuggestRecipesAdapter;
import com.example.AddRecipes;
import com.example.Model.Ingredient;
import com.example.Model.Item;
import com.example.Model.Recipes;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SuggestRecipes extends Fragment {
    Toolbar toolbar;
    RecyclerView rv;
    ProgressBar pb;
    LinearLayout layout;
    List<Item> itemListm = new ArrayList<>();
    List<Ingredient> ingredientList = new ArrayList<>();
    List<Recipes> SuggestRecipesList = new ArrayList<>();
    SuggestRecipesAdapter adapter;
    FloatingActionButton fab;
    SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.suggestrecipes, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Suggest Recipes");
        pb = view.findViewById(R.id.pb_recipes);
        fab = view.findViewById(R.id.add_recipe_button);
        rv = view.findViewById(R.id.rv_recipes);
        layout = view.findViewById(R.id.layout_recipes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);

        // Set OnClickListener to the FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AddRecipe activity
                startActivity(new Intent(getContext(), AddRecipes.class));
            }
        });

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call method to refresh the list of items
                refreshItems();
            }
        });

        getItems();
        getRecipes();
    }

    private void getItems() {

        Query query = FirebaseDatabase.getInstance().getReference("Items")
                .orderByChild("Uid").equalTo(Util.getSP(getContext()));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("User inventory items", snapshot.toString());
                rv.setAdapter(null);
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Item yourModel = childSnapshot.getValue(Item.class);
                    itemListm.add(yourModel);
                }
                if (!itemListm.isEmpty()) {

                    pb.setVisibility(View.GONE);
                } else {
                    Log.d("res", "nodata");
                    pb.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(layout, "No Item Found", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());

            }
        });

    }

    private void refreshItems() {
        // Implement the logic to refresh the list of items here
        // For example, you can call your getItems() method again to fetch updated data
        SuggestRecipesList.clear();
        getRecipes();
        // After refreshing, remember to stop the SwipeRefreshLayout animation
        swipeRefreshLayout.setRefreshing(false);
    }

    private void getRecipes() {
        pb.setVisibility(View.VISIBLE);
        List<Recipes> recipesList = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference("Recipes")
                .orderByChild("rid");

        query.addListenerForSingleValueEvent(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("res", snapshot.toString());

                rv.setAdapter(null);
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Recipes yourModel = childSnapshot.getValue(Recipes.class);
                    recipesList.add(yourModel);
                }
                Log.d("receipe list ", recipesList.toString());
                if (!recipesList.isEmpty()) {
                    for (int i = 0; i < recipesList.size(); i++) {
                        List<Item> availableitemlist = new ArrayList<>();
                        int totalmatchinginrecipes = 0;
                        ingredientList = new ArrayList<>();
                        ingredientList.addAll(recipesList.get(i).getIngredientlist());
                        List<Ingredient> unavailableitemlist = new ArrayList<>(ingredientList);

                        int totaitemcount = ingredientList.size();
                        List<String> addingredientnamelist = new ArrayList<>();
                        for (int j = 0; j < itemListm.size(); j++) {
                            String item = itemListm.get(j).getName();
                            for (int k = 0; k < ingredientList.size(); k++) {
                                String ingredient = ingredientList.get(k).getIngredient();
                                if (item.contains(ingredient) && !addingredientnamelist.contains(ingredient)) {
                                    addingredientnamelist.add(ingredient);
                                    unavailableitemlist.removeIf(ingredient1 -> {
                                        return Objects.equals(ingredient1.getIngredient(), ingredient);
                                    });
                                    totalmatchinginrecipes = totalmatchinginrecipes + 1;
                                    Log.d("available", ingredient);
                                    availableitemlist.add(itemListm.get(j));
                                }
                            }
                        }
                        Log.d("Ingredient list ", addingredientnamelist.toString());
                        Log.d("available item list ", availableitemlist.toString());

                        Map<String,Ingredient> uniqueNameObjects = new HashMap<>();
                        for (Ingredient ingredient : unavailableitemlist){
                          if (ingredient.getIngredient()!=null){
                              if (!uniqueNameObjects.containsKey(ingredient.getIngredient())){
                                  uniqueNameObjects.put(ingredient.getIngredient(),ingredient);
                              }
                          }
                        }
                        unavailableitemlist = new ArrayList<>(uniqueNameObjects.values());

                        float i1 = (float) totalmatchinginrecipes / totaitemcount;
                        float percent = i1 * 100;
                        Log.d("percent", String.valueOf(percent));
                        if (percent > 60) {
                            Recipes recipes = recipesList.get(i);
                            recipes.setSuggestedpercent((double) percent);
                            recipes.setAvailableitemlist(availableitemlist);
                            recipes.setUnavailableingredientlist(unavailableitemlist);
                            SuggestRecipesList.add(recipes);
                        }
                    }

                    Collections.sort(SuggestRecipesList, (o1, o2) -> o2.getSuggestedpercent().compareTo(o1.getSuggestedpercent()));
                    Log.d("finalsuggest", SuggestRecipesList.toString());
                    adapter = new SuggestRecipesAdapter(SuggestRecipesList, getContext());
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                } else {
                    Log.d("res", "nodata");
                    pb.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(layout, "No Item Found", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());
            }
        });
    }


}
