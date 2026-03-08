package com.example.allin1fitnessadmin;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allin1fitnessadmin.Model.Ingredient;
import com.example.allin1fitnessadmin.Utils.PermissionUtils;
import com.example.allin1fitnessadmin.Utils.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddRecipes extends AppCompatActivity {
    Toolbar toolbar;
    String[] typelist = {"Snack", "Dessert", "Main Course", "Appetizers"};
    String[] level = {"Underweight", "Healthy Weight", "Overweight", "Obesity"};
    Spinner spinnertype, spinnerlevel;
    String strlevel, strtype;
    TextInputEditText details, ingredients, procedure, link, name;
    Button submit;
    ImageView img, addimg;
    ScrollView layout;
    private static final int MY_RESULT_CODE_FILECHOOSER = 11111;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String path, picurl;
    FirebaseDatabase database;
    ProgressBar pb;
    RecyclerView rv;
    IngredientAdapter adapter;
    String measurementtext;
    List<Ingredient> listingredient = new ArrayList<>();
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipes);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Recipes");
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        setSupportActionBar(toolbar);
        pb = (ProgressBar) findViewById(R.id.pb_addrecipes);
        layout = (ScrollView) findViewById(R.id.addrecipes_layout);
        spinnerlevel = (Spinner) findViewById(R.id.spinner_level);
        spinnertype = (Spinner) findViewById(R.id.spinner_types);
        name = (TextInputEditText) findViewById(R.id.recipename_edt);
        details = (TextInputEditText) findViewById(R.id.recipesdetail_edt);
        rv = (RecyclerView) findViewById(R.id.rv_ingredients);
//        ingredients = (TextInputEditText) findViewById(R.id.recipesingredients_edt);
        addimg = (ImageView) findViewById(R.id.addimg);
        procedure = (TextInputEditText) findViewById(R.id.recipesprocedure_edt);
        link = (TextInputEditText) findViewById(R.id.recipeslink_edt);
        submit = (Button) findViewById(R.id.btn_addrecipe);
        img = (ImageView) findViewById(R.id.recipeimg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, typelist);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnertype.setAdapter(aa);
        spinnertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strtype = typelist[position];
                Log.d("type", strtype);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, level);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerlevel.setAdapter(aa1);
        spinnerlevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strlevel = level[position];
                Log.d("level", strlevel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Name", Snackbar.LENGTH_SHORT).show();
                } else if (details.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Details", Snackbar.LENGTH_SHORT).show();
                }  else if (procedure.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Procedure", Snackbar.LENGTH_SHORT).show();
                } else if (link.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Youtube Link", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (listingredient.size()==0){
                        Snackbar.make(layout, "Add Ingredients", Snackbar.LENGTH_SHORT).show();
                    }else {
                        uploadimg();
                    }

                }
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtils.requestPermission(AddRecipes.this, 101)) {
                    select();
                }
            }
        });

        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meausrementlist[] = {"Number", "Litre", "Kilogram", "ml", "gram"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipes.this);
                builder.setTitle("Add Ingrients");

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.add_ingredients, null);
                EditText name = customLayout.findViewById(R.id.recipename_edt);
                EditText quantity = customLayout.findViewById(R.id.quantity_edt);
                Spinner spinner = customLayout.findViewById(R.id.spinner_ingredient);
                Button btn = customLayout.findViewById(R.id.addingredient);


                //Creating the ArrayAdapter instance having the country list
                ArrayAdapter aa = new ArrayAdapter(AddRecipes.this, android.R.layout.simple_spinner_item, meausrementlist);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(aa);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        measurementtext = meausrementlist[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (name.getText().toString().isEmpty()) {
                            Snackbar.make(layout, "Enter Name", Snackbar.LENGTH_SHORT).show();
                        } else if (quantity.getText().toString().isEmpty()) {
                            Snackbar.make(layout, "Enter Quantity", Snackbar.LENGTH_SHORT).show();
                        } else {
                            listingredient.add(new Ingredient(UUID.randomUUID().toString(),name.getText().toString().trim(),
                                    quantity.getText().toString().trim(),
                                    measurementtext));
                            adapter = new IngredientAdapter(
                                    listingredient, getApplicationContext());
                            rv.setAdapter(adapter);
                            rv.setLayoutManager(
                                    new LinearLayoutManager(AddRecipes.this));
                            dialog.dismiss();

                        }
                    }
                });

                builder.setView(customLayout);
                if (dialog != null) {
                    dialog.cancel();
                }
                dialog = builder.create();
                dialog.show();

            }
        });
    }

    private void uploadimg() {
        pb.setVisibility(View.VISIBLE);
        Uri file = Uri.fromFile(new File(path));
        StorageReference fileRef = storageRef.child("images/" + file.getLastPathSegment());

        UploadTask uploadTask = fileRef.putFile(file);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    picurl = uri.toString();
                    // Use the download URL as needed
                    Log.d("url", picurl);
                    database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Recipes");
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference quizRef = rootRef.child("rid");
                    String key = quizRef.push().getKey();
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("rid", key);
                    taskMap.put("name", name.getText().toString());
                    taskMap.put("type", strtype);
                    taskMap.put("level", strlevel);
                    taskMap.put("details", details.getText().toString());
                    taskMap.put("procedure", procedure.getText().toString());
                    taskMap.put("img", picurl);
                    taskMap.put("link", link.getText().toString());
                    taskMap.put("ingredientlist",listingredient);
                    myRef.push().setValue(taskMap);

//                    for (int i = 0; i < listingredient.size(); i++) {
//                        database = FirebaseDatabase.getInstance();
//                        DatabaseReference myRef1 = database.getReference("Ingredients");
//                        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
//                        DatabaseReference quizRef1 = rootRef1.child("id");
//                        String key1 = quizRef1.push().getKey();
//                        Map<String, Object> taskMap1 = new HashMap<>();
//                        taskMap1.put("rid", key);
//                        taskMap1.put("id", key1);
//                        taskMap1.put("ingredient", listingredient.get(i).getIngredient());
//                        taskMap1.put("quantity", listingredient.get(i).getQuantity());
//                        taskMap1.put("measurement", listingredient.get(i).getMeasurement());
//                        myRef1.push().setValue(taskMap1);
//
//
//                    }
                    Snackbar snackbar = Snackbar.make(layout, "Recipes Added", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    pb.setVisibility(View.GONE);
                    finish();
                });
            } else {
                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                // Handle unsuccessful upload
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void allowPermissionForFile() {
        ActivityCompat.requestPermissions(AddRecipes.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                },
                1
        );
    }

    private void select() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == MY_RESULT_CODE_FILECHOOSER) {
                try {
                    Uri uri = data.getData();
                    Log.d("uri", uri.toString());
                    File file = new File(getPath(uri));
                    path = file.getAbsolutePath();
                    Log.d("path", path);
                    final InputStream inputStream = getContentResolver().openInputStream(uri);
                    final Bitmap imageMap = BitmapFactory.decodeStream(inputStream);
                    Log.d("bitmap", imageMap.toString());
                    img.setImageBitmap(imageMap);
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                }

            } else {

            }
        }

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.myview> {
        List<com.example.allin1fitnessadmin.Model.Ingredient> vehicleList = new ArrayList<>();
        Context context;
        View v;

        public IngredientAdapter(List<com.example.allin1fitnessadmin.Model.Ingredient> vehicleList, Context context) {
            this.vehicleList = vehicleList;
            this.context = context;
        }

        @NonNull
        @Override
        public myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_ingredient, parent, false);

            // set the view's size, margins, paddings and layout parameters5
            myview vh = new myview(v); // pass the view to View Holder
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull myview holder, int position) {
            com.example.allin1fitnessadmin.Model.Ingredient vehicle = vehicleList.get(position);
            holder.name.setText(vehicle.getIngredient());
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vehicleList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, vehicleList.size());
                }
            });
        }

        @Override
        public int getItemCount() {
            return vehicleList.size();
        }

        public class myview extends RecyclerView.ViewHolder {
            TextView name;
            ImageView img;

            public myview(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.item_ingrdient);
                img = itemView.findViewById(R.id.remove_ingredient);
            }
        }
    }


}