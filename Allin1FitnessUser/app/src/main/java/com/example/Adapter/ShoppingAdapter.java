package com.example.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Model.Shopping;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.myview> {
    List<Shopping> vehicleList = new ArrayList<>();
    Context context;
    View v;
    Fragment fragment;
    ItemCLickListener itemClickListener;

    public ShoppingAdapter(List<Shopping> vehicleList, Context context, ItemCLickListener itemClickListener) {
        this.vehicleList = vehicleList;
        this.context = context;
        this.itemClickListener = itemClickListener;

    }

    @NonNull
    @Override
    public myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);

        // set the view's size, margins, paddings and layout parameters5
        myview vh = new myview(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull myview holder, int position) {
        Shopping vehicle = vehicleList.get(position);
        holder.name.setText("Name: " + vehicle.getName());
        holder.measure.setText("Measurement: " + vehicle.getMeasurement());
        holder.qauntity.setText("Quantity: " + vehicle.getQuantity());

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              FirebaseDatabase  database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Items");
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference quizRef = rootRef.child("id");
                String key = quizRef.push().getKey();
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("id", key);
                taskMap.put("Name", vehicle.getName());
                taskMap.put("Quantity", vehicle.getQuantity());
                taskMap.put("status", vehicle.getMeasurement());
                taskMap.put("Uid", Util.getSP(context.getApplicationContext()));
                myRef.push().setValue(taskMap);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("Shopping List").orderByChild("id").equalTo(vehicle.getId());
                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();

                        }
                        itemClickListener.onclickcheck("true");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(v.getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("Shopping List").orderByChild("id").equalTo(vehicle.getId());
                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();

                        }
                        itemClickListener.onclickcheck("true");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(v.getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public class myview extends RecyclerView.ViewHolder {
        TextView name, measure, qauntity;
        Button add, delete;

        public myview(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.shop_name);
            measure = itemView.findViewById(R.id.shop_measure);
            qauntity = itemView.findViewById(R.id.shop_quantity);
            add = itemView.findViewById(R.id.btn_additemshop);
            delete = itemView.findViewById(R.id.btn_deleteshop);

        }
    }

    public interface ItemCLickListener {

        void onclickcheck(String s);

    }

}
