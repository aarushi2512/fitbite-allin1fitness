package com.example.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Adapter.ItemAdapter;
import com.example.AddUpdateItems;
import com.example.Model.Item;
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
import java.util.List;

public class ManageItemsFrag extends Fragment {
    Toolbar toolbar;
    RelativeLayout layout;
    ProgressBar pb;
    RecyclerView rv;
    FloatingActionButton fab;
    ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manageitem, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Items");
        layout = view.findViewById(R.id.layout_item);
        pb = view.findViewById(R.id.pb_items);
        rv = view.findViewById(R.id.rv_items);
        fab = view.findViewById(R.id.fab_items);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rv.setLayoutManager(gridLayoutManager);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddUpdateItems.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        pb.setVisibility(View.VISIBLE);
        List<Item> recipesList = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference("Items")
                .orderByChild("id");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("res", snapshot.toString());
                rv.setAdapter(null);
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Item yourModel = childSnapshot.getValue(Item.class);
                    // Check if the item belongs to the current user
                    if (yourModel != null && yourModel.getUid().equals(Util.getSP(getContext()))) {
                        recipesList.add(yourModel);
                    }
                }
                if (!recipesList.isEmpty()) {
                    adapter = new ItemAdapter(recipesList, getContext());
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
