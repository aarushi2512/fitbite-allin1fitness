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
import com.example.AddUpdateItems;
import com.example.Model.Item;
import com.example.allin1fitnessuser.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private Context context;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView itemNameTextView;
        private TextView itemStatusTextView;
        private ImageView itemImageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_tv);
            itemStatusTextView = itemView.findViewById(R.id.status_tv);
            itemImageView = itemView.findViewById(R.id.item_image); // Initialize ImageView
        }

        public void bind(Item item) {
            itemNameTextView.setText(item.getName());
            itemStatusTextView.setText("Qty: " + item.getQuantity() + " " + item.getStatus());

            // Load image using Glide
            Glide.with(context)
                    .load(item.getImageUrl()) // Assuming item.getImageUrl() returns the URL of the image
                    .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                    .error(R.drawable.error_image) // Error image if the load fails
                    .into(itemImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddUpdateItems.class);
                    intent.putExtra("items", itemList.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
