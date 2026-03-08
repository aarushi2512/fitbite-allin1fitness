package com.example.allin1fitnessadmin;

import static kotlinx.coroutines.selects.SelectKt.select;

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
import android.telephony.CarrierConfigManager;
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

import com.bumptech.glide.Glide;
import com.example.allin1fitnessadmin.Adapter.RecipesAdapter;
import com.example.allin1fitnessadmin.Model.Ingredient;
import com.example.allin1fitnessadmin.Model.Recipes;
import com.example.allin1fitnessadmin.Utils.PermissionUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DetailRecipes extends AppCompatActivity {
    Recipes entity;
    Toolbar toolbar;
    String[] typelist = {"Snack", "Dessert", "Main Course", "Appetizers"};
    String[] level = {"Underweight", "Healthy Weight", "Overweight", "Obesity"};
    Spinner spinnertype, spinnerlevel;
    String strlevel, strtype;
    TextInputEditText details, procedure, link, name;
    Button submit;
    ImageView img, addimg;
    ScrollView layout;
    private static final int MY_RESULT_CODE_FILECHOOSER = 11111;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String path, picurl;
    FirebaseDatabase database;
    ProgressBar pb;
    ImageView deleteicon;
    RecyclerView rv;
    IngredientAdapter adapter;
    String measurementtext;
    List<Ingredient> listingredient = new ArrayList<>();
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipes);
        initUi();
    }

    private void initUi() {
        entity = (Recipes) getIntent().getSerializableExtra("recipes");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Recipes");
        deleteicon = (ImageView) findViewById(R.id.deleteicon);
        deleteicon.setVisibility(View.VISIBLE);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        setSupportActionBar(toolbar);
        pb = (ProgressBar) findViewById(R.id.dpb_addrecipes);
        layout = (ScrollView) findViewById(R.id.daddrecipes_layout);
        spinnerlevel = (Spinner) findViewById(R.id.dspinner_level);
        spinnertype = (Spinner) findViewById(R.id.dspinner_types);
        name = (TextInputEditText) findViewById(R.id.drecipename_edt);
        details = (TextInputEditText) findViewById(R.id.drecipesdetail_edt);
        rv = (RecyclerView) findViewById(R.id.drv_ingredients);
        addimg = (ImageView) findViewById(R.id.daddimg);
        procedure = (TextInputEditText) findViewById(R.id.drecipesprocedure_edt);
        link = (TextInputEditText) findViewById(R.id.drecipeslink_edt);
        submit = (Button) findViewById(R.id.dbtn_addrecipe);
        img = (ImageView) findViewById(R.id.drecipeimg);
        setInput();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, typelist);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnertype.setAdapter(aa);
        if (entity.getType().equals("Snack")) {
            spinnertype.setSelection(0);
        } else if (entity.getType().equals("Dessert")) {
            spinnertype.setSelection(1);
        } else if (entity.getType().equals("Main Course")) {
            spinnertype.setSelection(2);
        } else if (entity.getType().equals("Appetizers")) {
            spinnertype.setSelection(3);
        } else {

        }
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

        if (entity.getLevel().equals("Underweight")) {
            spinnerlevel.setSelection(0);
        } else if (entity.getLevel().equals("Healthy Weight")) {
            spinnerlevel.setSelection(1);
        } else if (entity.getLevel().equals("Overweight")) {
            spinnerlevel.setSelection(2);
        } else if (entity.getLevel().equals("Obesity")) {
            spinnerlevel.setSelection(3);
        } else {

        }
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
                } else if (procedure.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Procedure", Snackbar.LENGTH_SHORT).show();
                } else if (link.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Youtube Link", Snackbar.LENGTH_SHORT).show();
                } else if (path == null) {
                    update(entity.getImg());
                } else if (path != null) {
                    uploadimg();
                }
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.requestPermission(DetailRecipes.this, 101)) {
                    select();
                }
            }
        });

        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meausrementlist[] = {"Number", "Litre", "Kilogram", "ml", "gram"};
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailRecipes.this);
                builder.setTitle("Add Ingrients");

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.add_ingredients, null);
                EditText name = customLayout.findViewById(R.id.recipename_edt);
                EditText quantity = customLayout.findViewById(R.id.quantity_edt);
                Spinner spinner = customLayout.findViewById(R.id.spinner_ingredient);
                Button btn = customLayout.findViewById(R.id.addingredient);

                //Creating the ArrayAdapter instance having the country list
                ArrayAdapter aa = new ArrayAdapter(DetailRecipes.this, android.R.layout.simple_spinner_item, meausrementlist);
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
                                    new LinearLayoutManager(DetailRecipes.this));
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

        deleteicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("Recipes").orderByChild("rid").equalTo(entity.getRid());
                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            Snackbar.make(layout, "Deleted!", Snackbar.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar.make(layout, "Failed!", Snackbar.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void allowPermissionForFile() {
        ActivityCompat.requestPermissions(DetailRecipes.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                },
                1
        );
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
                    update(picurl);
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

    private void setInput() {
        Log.d("details", entity.getDetails());
        name.setText(entity.getName());
        details.setText(entity.getDetails());
        procedure.setText(entity.getProcedure());
        listingredient=entity.getIngredientlist();
        Glide.with(getApplicationContext()).load(entity.getImg()).into(img);
        link.setText(entity.getLink());
        if (!listingredient.isEmpty()) {
            adapter = new IngredientAdapter(listingredient, getApplicationContext());
            rv.setAdapter(adapter);

            adapter.notifyDataSetChanged();
            pb.setVisibility(View.GONE);
        } else {
            Log.d("res", "nodata");
            pb.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar.make(layout, "No Ingredients Found", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
//        getIngredients(entity.getRid());

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

    void update(String img) {
        Query query = FirebaseDatabase.getInstance().getReference("Recipes")
                .orderByChild("rid")
                .equalTo(entity.getRid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Recipes yourModel = childSnapshot.getValue(Recipes.class);
                    childSnapshot.getRef().child("details").setValue(details.getText().toString());
                    childSnapshot.getRef().child("img").setValue(img);
                    childSnapshot.getRef().child("level").setValue(strlevel);
                    childSnapshot.getRef().child("link").setValue(link.getText().toString());
                    childSnapshot.getRef().child("name").setValue(name.getText().toString());
                    childSnapshot.getRef().child("procedure").setValue(procedure.getText().toString());
                    childSnapshot.getRef().child("rid").setValue(entity.getRid());
                    childSnapshot.getRef().child("type").setValue(strtype);
                    childSnapshot.getRef().child("ingredientlist").setValue(listingredient);

                    Snackbar snackbar = Snackbar.make(layout, "Updated!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    pb.setVisibility(View.GONE);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());
            }
        });
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
        public IngredientAdapter.myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_ingredient, parent, false);
            // set the view's size, margins, paddings and layout parameters5
            IngredientAdapter.myview vh = new IngredientAdapter.myview(v); // pass the view to View Holder
            return vh;
        }
        @Override
        public void onBindViewHolder(@NonNull IngredientAdapter.myview holder, int position) {
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String meausrementlist[] = {"Number", "Litre", "Kilogram", "ml", "gram"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailRecipes.this);
                    builder.setTitle("Add Ingrients");

                    // set the custom layout
                    final View customLayout = getLayoutInflater().inflate(R.layout.add_ingredients, null);
                    EditText name = customLayout.findViewById(R.id.recipename_edt);
                    EditText quantity = customLayout.findViewById(R.id.quantity_edt);
                    Spinner spinner = customLayout.findViewById(R.id.spinner_ingredient);
                    Button btn = customLayout.findViewById(R.id.addingredient);

                    //Creating the ArrayAdapter instance having the country list
                    ArrayAdapter aa = new ArrayAdapter(DetailRecipes.this, android.R.layout.simple_spinner_item, meausrementlist);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(aa);
                    name.setText(vehicle.getIngredient());
                    quantity.setText(vehicle.getQuantity());
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            measurementtext = meausrementlist[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    if (vehicle.getMeasurement().equals("Number")) {
                        spinner.setSelection(0);
                    } else if (vehicle.getMeasurement().equals("Litre")) {
                        spinner.setSelection(1);
                    } else if (vehicle.getMeasurement().equals("Kilogram")) {
                        spinner.setSelection(2);
                    } else if (vehicle.getMeasurement().equals("ml")) {
                        spinner.setSelection(3);
                    } else if (vehicle.getMeasurement().equals("gram")) {
                        spinner.setSelection(4);
                    } else {

                    }

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (name.getText().toString().isEmpty()) {
                                Snackbar.make(layout, "Enter Name", Snackbar.LENGTH_SHORT).show();
                            } else if (quantity.getText().toString().isEmpty()) {
                                Snackbar.make(layout, "Enter Quantity", Snackbar.LENGTH_SHORT).show();
                            } else {
                                if(vehicleList.get(position)==null) {
                                    listingredient.add(new Ingredient(UUID.randomUUID().toString(), name.getText().toString().trim(),
                                            quantity.getText().toString().trim(),
                                            measurementtext));


                                }else {
                                    Ingredient ingredient=new Ingredient(vehicle.getId(), name.getText().toString().trim(),
                                            quantity.getText().toString().trim(),
                                            measurementtext);

                                    listingredient.set(position, ingredient);

                                }
                                adapter = new IngredientAdapter(
                                        listingredient, getApplicationContext());
                                rv.setAdapter(adapter);
                                rv.setLayoutManager(
                                        new LinearLayoutManager(DetailRecipes.this));
                                adapter.notifyDataSetChanged();
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