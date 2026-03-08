package com.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.Model.Item;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddUpdateItems extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar pb;
    LinearLayout layout;
    Spinner spinner;
    String status;
    TextInputEditText name_edt, qauntity_edt;
    Button submit;
    String[] statuslist = {"Number", "Litre", "Kilogram", "ml", "gram"};
    Item entity;
    ImageView deleteimg;
    Button selectImageBtn;
    ImageView itemImageView;
    Uri selectedImageUri;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_items);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Items");
        pb = findViewById(R.id.pb_additem);
        layout = findViewById(R.id.layout_additem);
        spinner = findViewById(R.id.spinner_itemstatus);
        deleteimg = findViewById(R.id.deleteicon);
        name_edt = findViewById(R.id.itemname_edt);
        qauntity_edt = findViewById(R.id.itemquantity_edt);
        submit = findViewById(R.id.submit_item);
        entity = (Item) getIntent().getSerializableExtra("items");
        selectImageBtn = findViewById(R.id.select_image_btn);
        itemImageView = findViewById(R.id.item_image_view);

        storageReference = FirebaseStorage.getInstance().getReference();

        // Spinner setup
        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, statuslist);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa1);

        // If entity is not null, populate fields with its data
        if (entity != null) {
            name_edt.setText(entity.getName());
            qauntity_edt.setText(entity.getQuantity());
            spinner.setSelection(getSpinnerIndex(entity.getStatus()));
            deleteimg.setVisibility(View.VISIBLE);

            // Load the image using Glide or similar library
            Glide.with(this)
                    .load(entity.getImageUrl()) // Assuming getImageUrl() method exists in Item class
                    .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                    .error(R.drawable.error_image) // Error image if loading fails
                    .into(itemImageView);
        }

        // Spinner item selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = statuslist[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Submit button click listener
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name_edt.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Name", Snackbar.LENGTH_SHORT).show();
                } else if (qauntity_edt.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Quantity", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (entity == null) {
                        addItem();
                    } else {
                        updateItem();
                    }
                }
            }
        });

        // Delete button click listener
        deleteimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

        // Select image button click listener
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    // Method to select an image from device storage
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    // Method to handle result of selecting an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Compress the image to 50% quality
                byte[] imageData = baos.toByteArray();
                selectedImageUri = getImageUri(getApplicationContext(), bitmap); // Update the selectedImageUri with the compressed image
                itemImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get URI from Bitmap
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes); // Compress the image to 50% quality
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    // Method to add an item
    private void addItem() {
        pb.setVisibility(View.VISIBLE);

        String itemName = name_edt.getText().toString();
        String itemQuantity = qauntity_edt.getText().toString();

        if (selectedImageUri != null) {
            StorageReference imageRef = storageReference.child("item_images/" + itemName + "_" + System.currentTimeMillis() + ".jpg");

            imageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveItemToDatabase(itemName, itemQuantity, status, imageUrl);
                });
            }).addOnFailureListener(e -> {
                Snackbar.make(layout, "Failed to upload image", Snackbar.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            });
        } else {
            saveItemToDatabase(itemName, itemQuantity, status, "");
        }
    }

    // Method to save item to Firebase Database
    private void saveItemToDatabase(String itemName, String itemQuantity, String status, String imageUrl) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Items");
        String key = myRef.push().getKey();

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("id", key);
        itemMap.put("Name", itemName);
        itemMap.put("Quantity", itemQuantity);
        itemMap.put("status", status);
        itemMap.put("Uid", Util.getSP(getApplicationContext()));
        itemMap.put("imageUrl", imageUrl);

        myRef.child(key).setValue(itemMap)
                .addOnSuccessListener(aVoid -> {
                    Snackbar.make(layout, "Item Added", Snackbar.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(layout, "Failed to add item", Snackbar.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                });
    }

    /// Method to update an item
    private void updateItem() {
        pb.setVisibility(View.VISIBLE);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Items");
        String itemId = entity.getId();

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("Name", name_edt.getText().toString());
        updateMap.put("Quantity", qauntity_edt.getText().toString());
        updateMap.put("status", status);

        myRef.child(itemId).updateChildren(updateMap)
                .addOnSuccessListener(aVoid -> {
                    Snackbar.make(layout, "Item Updated", Snackbar.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(layout, "Failed to update item", Snackbar.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                });
    }

    // Method to delete an item
    private void deleteItem() {
        pb.setVisibility(View.VISIBLE);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Items");
        String itemId = entity.getId();

        myRef.child(itemId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(layout, "Item Deleted", Snackbar.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                        finish();
                    } else {
                        Snackbar.make(layout, "Failed to delete item", Snackbar.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }
                });
    }


    // Method to get spinner index based on status
    private int getSpinnerIndex(String status) {
        for (int i = 0; i < statuslist.length; i++) {
            if (statuslist[i].equals(status)) {
                return i;
            }
        }
        return 0; // Default index
    }
}
