package com.example.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.Model.Item;
import com.example.Model.Recipes;
import com.example.RecipesDetailsActivity;
import com.example.allin1fitnessuser.R;

import java.util.ArrayList;
import java.util.List;

public class SuggestRecipesAdapter extends RecyclerView.Adapter<SuggestRecipesAdapter.myview> {
    List<Recipes> vehicleList = new ArrayList<>();
    Context context;
    View v;
    public SuggestRecipesAdapter(List<Recipes> vehicleList, Context context) {
        this.vehicleList = vehicleList;
        this.context = context;
    }

    @NonNull
    @Override
    public myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_recipes, parent, false);

        // set the view's size, margins, paddings and layout parameters5
        myview vh = new myview(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull myview holder, int position) {
        Recipes vehicle = vehicleList.get(position);
        holder.name.setText("Name: " + vehicle.getName());
        holder.level.setText("Level: " + vehicle.getLevel());
        holder.type.setText("Type: " + vehicle.getType());
        Glide.with(context).load(vehicle.getImg()).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RecipesDetailsActivity.class);
                intent.putExtra("recipes", vehicleList.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public class myview extends RecyclerView.ViewHolder {
        TextView name, level, type;
        ImageView img;

        public myview(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            level = itemView.findViewById(R.id.item_level);
            type = itemView.findViewById(R.id.item_type);
            img = itemView.findViewById(R.id.item_img);
        }
    }
}
