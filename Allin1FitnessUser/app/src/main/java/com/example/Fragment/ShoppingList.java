package com.example.Fragment;

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

import com.example.Adapter.ItemAdapter;
import com.example.Adapter.ShoppingAdapter;
import com.example.Model.Item;
import com.example.Model.Shopping;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingList extends Fragment implements ShoppingAdapter.ItemCLickListener {
    ProgressBar pb;
    RecyclerView rv;
    LinearLayout layout;
    ShoppingAdapter adapter;
    Toolbar toolbar;

    Fragment fragment;
    ShoppingAdapter.ItemCLickListener itemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_shoppinglistfrag, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment = this;
        itemClickListener=this;
        layout = view.findViewById(R.id.layout_shopping);
        rv = view.findViewById(R.id.rv_shoppinglist);
        pb = view.findViewById(R.id.pb_shoppinglist);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Shopping List");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);

    }

    @Override
    public void onResume() {
        super.onResume();
        pb.setVisibility(View.VISIBLE);
        List<Shopping> recipesList = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference("Shopping List")
                .orderByChild("Uid").equalTo(Util.getSP(getContext()));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("res", snapshot.toString());
                rv.setAdapter(null);
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Shopping yourModel = childSnapshot.getValue(Shopping.class);
                    recipesList.add(yourModel);
                }
                if (!recipesList.isEmpty()) {
                    adapter = new ShoppingAdapter(recipesList, getContext(), itemClickListener);
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

    @Override
    public void onclickcheck(String s) {
        if (s.equals("true")){
            onResume();
        }
    }
}
