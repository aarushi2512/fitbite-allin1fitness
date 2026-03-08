package com.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Model.Ingredient;
import com.example.Model.Item;
import com.example.Model.Recipes;
import com.example.Utils.TextSplitter;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipesDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout layout;
    ImageView img;
    TextView name, detail, level, type, procedure, link, available, unavailable;
    Recipes entity;
    Button addbtn;
    FirebaseDatabase database;
    ProgressBar pb;
    String text, quantity, unit;
    CardView youtube;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_details);
        entity = (Recipes) getIntent().getSerializableExtra("recipes");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Suggest Recipes");
        pb = (ProgressBar) findViewById(R.id.pb_detailrecipes);
        layout = (LinearLayout) findViewById(R.id.layout_detailsrecipes);
        addbtn = (Button) findViewById(R.id.btn_addshopping);
        img = (ImageView) findViewById(R.id.recipes_img);
        name = (TextView) findViewById(R.id.recipe_name);
        detail = (TextView) findViewById(R.id.recipe_detail);
        level = (TextView) findViewById(R.id.recipe_level);
        type = (TextView) findViewById(R.id.recipe_type);
        procedure = (TextView) findViewById(R.id.procedure_tv);
        link = (TextView) findViewById(R.id.youtubelink_tv);
        available = (TextView) findViewById(R.id.available_tv);
        unavailable = (TextView) findViewById(R.id.unavailable_tv);
        youtube = (CardView) findViewById(R.id.youtube);
        Glide.with(getApplicationContext()).load(entity.getImg()).into(img);
        name.setText("Recipe Name: " + entity.getName());
        detail.setText("Recipe Detail: " + entity.getDetails());
        level.setText("Level: " + entity.getLevel());
        type.setText("Type: " + entity.getType());
        procedure.setText(entity.getProcedure());
        link.setText(entity.getLink());
        link.setVisibility(View.GONE);

        List<String> unavailableList = new ArrayList<>();
        for (Ingredient ingredient : entity.getUnavailableingredientlist()) {
            unavailableList.add(" " + ingredient.getIngredient() + "(" + ingredient.getQuantity() + ")" + "(" + ingredient.getMeasurement() + ")");
        }
        unavailable.setText(String.join(",", unavailableList));
        if (unavailableList.size() == 0) {
            unavailable.setText("Everything is Available");
            addbtn.setVisibility(View.GONE);
        }

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.getLink()));
                startActivity(intent);
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                for (int i = 0; i < unavailableList.size(); i++) {
                    textspiltter(unavailableList.get(i).toString());
                    if (text != null) {
                        database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Shopping List");
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference quizRef = rootRef.child("id");
                        String key = quizRef.push().getKey();
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("id", key);
                        taskMap.put("Name", text);
                        taskMap.put("Uid", Util.getSP(getApplicationContext()));
                        taskMap.put("Quantity", quantity);
                        taskMap.put("Measurement", unit);
                        myRef.push().setValue(taskMap);
                    }
                }
                pb.setVisibility(View.GONE);
                Snackbar.make(layout, "Added to Shopping List", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        });

        List<String> availableList = new ArrayList<>();
        for (Item item : entity.getAvailableitemlist()) {
            availableList.add(" " + item.getName() + "(" + item.getQuantity() + ")");

        }
        available.setText(String.join(",\n", availableList));


    }

    public void textspiltter(String inputtext) {


        // Define patterns for the three parts
        Pattern textPattern = Pattern.compile("([a-zA-Z ]+)");

        Pattern quantityPattern = Pattern.compile("\\((\\d+(\\.\\d+)?)\\)");
        Pattern unitPattern = Pattern.compile("\\(([a-zA-Z ]+)\\)");

        // Create matchers for each pattern
        Matcher textMatcher;
        textMatcher = textPattern.matcher(inputtext);
        Matcher quantityMatcher = quantityPattern.matcher(inputtext);
        Matcher unitMatcher = unitPattern.matcher(inputtext);
        // Extract and print the three parts
        if (textMatcher.find()) {
            text = textMatcher.group(1);
            System.out.println("1. Text: " + text);
        }

        if (quantityMatcher.find()) {
            quantity = quantityMatcher.group(1);
            System.out.println("2. Quantity: " + quantity);
        }

        if (unitMatcher.find()) {
            unit = unitMatcher.group(1);
            System.out.println("3. Unit: " + unit);
        }
    }
}