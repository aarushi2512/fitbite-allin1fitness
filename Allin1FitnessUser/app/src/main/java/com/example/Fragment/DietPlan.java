package com.example.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.Model.Diet;
import com.example.Model.User;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietPlan extends Fragment {
    LinearLayout layout;
    Toolbar toolbar;
    List<String> foodItems = new ArrayList<>(Arrays.asList(
            "Nuts&Almonds",
            "Melon(100g)",
            "Tomato soup(medium dish)",
            "Vegetables Soup (medium dish)",
            "Cream of mushroom soup",
            "Vegetable and rice",
            "Cheese Pizza with Vegetables(one piece)",
            "Boiled Potatoes",
            "Grilled Chicken with Vegetables",
            "Falafel",
            "Soup, Chicken with Rice(medium dish)",
            "Cheese, Cream (100g)",
            "Yogurt, low fat(200g)",
            "Pastrami",
            "Broccoli Soup(cup)",
            "Hummus(2 tablespoons)",
            "Cantaloupe melon (cup)",
            "Yogurt whole milk",
            "Fries",
            "Kiwi",
            "Blueberry",
            "Dates",
            "Mixed Vegetable Salad",
            "Apple",
            "Yellow Cheese",
            "Chocolate",
            "Chocolate Cake",
            "Cake",
            "Mango",
            "Meat",
            "Bread",
            "Rice",
            "Bean",
            "Banana",
            "Dark Chocolate",
            "Chicken(grilled)",
            "Moong",
            "Chicken(roasted)",
            "Milk",
            "Egg",
            "Fish(roasted)",
            "Fish(grilled)",
            "Fish(fried)",
            "Grapes",
            "Egg White",
            "Orange",
            "Strawberry",
            "Lemon juice",
            "Pear",
            "Guava",
            "Ice Cream",
            "Lady Finger(Bamya)",
            "Brinjal",
            "Spinach",
            "White Cheese (one piece)",
            "Cherries",
            "Popcorn",
            "Kiwi fruit",
            "Fig",
            "Spaghetti",
            "Labneh",
            "Molokhia -1 cup",
            "French fries",
            "Stuffed Zucchini",
            "Cup of Tabbouleh",
            "Turkey(100g)",
            "Avocado",
            "Fruit Salad",
            "Fruit Cocktail",
            "Grapefruit juice (cup)",
            "Grapes juice",
            "Blueberries",
            "Hummus(small dish)",
            "Cereals"
    ));
    List<Double> carbslist = new ArrayList<>(Arrays.asList(
            21.55, 8.16, 16.87, 9.78, 6.8, 12.1, 30.0, 34.42, 17.13, 5.41,
            12.0, 7.0, 14.0, 22.7, 8.0, 6.04, 14.44, 12.29, 28.0, 21.7,
            3.69, 3.69, 9.1, 17.3, 1.0, 20.0, 23.0, 37.0, 12.0, 3.0,
            30.0, 45.0, 29.0, 28.0, 8.0, 2.0, 55.0, 2.0, 31.0, 24.0,
            0.0, 0.0, 1.0, 12.0, 5.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 12.0, 6.22, 21.0, 3.69, 34.26, 8.6, 5.0, 28.0, 26.8,
            15.9, 0.0, 0.0, 8.0, 15.0, 8.0, 0.52, 3.69, 49.5, 27.3
    ));

    List<Double> fatlist = new ArrayList<>(Arrays.asList(
            49.93, 0.19, 3.42, 1.58, 5.4, 1.5, 8.9, 0.2, 3.06, 3.03,
            2.0, 43.0, 3.0, 1.5, 4.0, 2.58, 0.34, 3.0, 5.0, 0.77,
            5.6, 0.85, 0.5, 0.2, 6.0, 46.0, 40.0, 38.0, 32.0, 26.0,
            26.9, 23.0, 23.0, 23.0, 22.0, 21.0, 20.0, 18.0, 17.8, 13.0,
            11.0, 11.0, 11.0, 6.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 3.0, 0.0, 0.36, 0.69, 0.3, 4.11, 3.6, 9.0,
            5.0, 12.8, 9.0, 100.0, 99.0, 0.0, 0.0, 0.0, 0.06, 2.03, 21.13, 0.93
    ));

    List<Double> proteinslist = new ArrayList<>(Arrays.asList(
            21.15, 0.84, 3.55, 1.72, 1.35, 1.9, 9.3, 5.72, 7.35, 2.26,
            2.0, 6.0, 10.0, 0.2, 2.0, 1.46, 1.49, 7.33, 3.0, 1.69,
            12.3, 4.23, 1.8, 0.3, 3.0, 18.0, 12.0, 23.0, 22.0, 36.0,
            15.0, 10.0, 32.0, 41.0, 20.0, 45.0, 15.0, 46.0, 53.0, 12.0,
            46.0, 42.0, 39.0, 26.0, 42.0, 25.0, 23.5, 25.0, 18.0, 19.0,
            28.0, 17.0, 21.0, 32.0, 4.0, 2.0, 1.04, 3.6, 4.96, 7.71, 6.4,
            12.0, 3.0, 21.6, 2.62, 0.0, 0.0, 0.0, 0.0, 1.0, 0.17, 4.23, 11.96, 2.28
    ));

    String[] consumptionlist = {
            "Healthy,Over Weight",
            "Healthy,Over Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy",
            "Over Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy",
            "Over Weight",
            "Under Weight",
            "Over Weight",
            "Healthy,Over Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy",
            "Healthy,Over Weight",
            "Healthy,Over Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy",
            "Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight,Healthy,Over Weight",
            "Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Healthy,Over Weight",
            "Over Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy",
            "Healthy,Over Weight",
            "Healthy,Over Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight,Healthy,Over Weight",
            "Healthy,Over Weight",
            "Under Weight",
            "Under Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Healthy,Over Weight",
            "Under Weight",
            "Healthy,Over Weight",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Under Weight,Healthy",
            "Under Weight,Healthy"
    };
    String[] timelist = {
            "Snacks",
            "Snacks",
            "Snacks",
            "Dinner",
            "Dinner",
            "Lunch",
            "Dinner",
            "Dinner",
            "Lunch",
            "Breakfast",
            "Lunch",
            "Breakfast",
            "Dinner",
            "Breakfast",
            "Dinner",
            "Breakfast,Lunch,Snacks,Dinner",
            "Breakfast,Snacks,Dinner",
            "Breakfast",
            "Lunch",
            "Breakfast,Snacks",
            "Breakfast,Snacks",
            "Breakfast,Snacks",
            "Dinner",
            "Breakfast",
            "Breakfast",
            "Snacks",
            "Snacks",
            "Snacks",
            "Breakfast",
            "Lunch",
            "Breakfast,Lunch,Snacks,Dinner",
            "Lunch",
            "Lunch",
            "Breakfast",
            "Snacks",
            "Lunch",
            "Lunch",
            "Lunch",
            "Breakfast,Dinner",
            "Breakfast,Dinner",
            "Lunch",
            "Lunch",
            "Lunch",
            "Snacks",
            "Breakfast,Dinner",
            "Breakfast,Snacks",
            "Snacks",
            "Snacks",
            "Breakfast,Snacks",
            "Breakfast,Snacks",
            "Snacks",
            "Lunch",
            "Lunch",
            "Lunch",
            "Breakfast,Dinner",
            "Snacks",
            "Snacks",
            "Breakfast,Snacks",
            "Breakfast,Snacks,Dinner",
            "Lunch",
            "Breakfast,Dinner",
            "Lunch",
            "Lunch",
            "Lunch",
            "Lunch,Dinner",
            "Lunch",
            "Breakfast",
            "Snacks",
            "Snacks",
            "Snacks",
            "Breakfast,Snacks",
            "Breakfast,Snacks,Dinner",
            "Breakfast",
            "Breakfast"
    };
    String[] catlist = {
            "Others",
            "Fruits",
            "Veg",
            "Veg",
            "Veg",
            "Veg",
            "Veg",
            "Veg",
            "Nonveg",
            "Nonveg",
            "Nonveg",
            "Veg",
            "Veg",
            "Nonveg",
            "Veg",
            "Veg",
            "Fruits",
            "Veg",
            "Veg",
            "Fruits",
            "Fruits",
            "Others",
            "Salad",
            "Fruits",
            "Veg",
            "Veg",
            "Veg",
            "Veg",
            "Fruits",
            "Nonveg",
            "Veg",
            "Veg",
            "Veg",
            "Fruits",
            "Veg",
            "Nonveg",
            "Veg",
            "Nonveg",
            "Veg",
            "Egg",
            "Nonveg",
            "Nonveg",
            "Nonveg",
            "Fruits",
            "Egg",
            "Fruits",
            "Fruits",
            "Juices",
            "Fruits",
            "Fruits",
            "Veg",
            "Veg",
            "Veg",
            "Veg",
            "Veg",
            "Fruits",
            "Veg",
            "Fruits",
            "Fruits",
            "Veg",
            "Veg",
            "Veg",
            "Veg",
            "Veg",
            "Salad",
            "Nonveg",
            "Fruits",
            "Fruits",
            "Fruits",
            "Juices",
            "Juices",
            "Fruits",
            "Veg",
            "Veg"
    };
    TextView result_tv;
    String result;
    LinearLayout cardview;
    LinearLayout cardviewlunch;
    LinearLayout snackcard;
    LinearLayout dinnercard;
    Context context;
    ProgressDialog pd;
    double b, l, s, d = 0;
    TextView breaktv, launctv, snacktv, dinnertv,titletv;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dietplan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = view.findViewById(R.id.layout_dietplan);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Diet Plan");
        cardview = view.findViewById(R.id.brecard);
        cardviewlunch = view.findViewById(R.id.lincard);
        snackcard = view.findViewById(R.id.linsnack);
        cardviewlunch = view.findViewById(R.id.lincard);
        dinnercard = view.findViewById(R.id.lindinner);
        result_tv = view.findViewById(R.id.result_tv);
        breaktv = view.findViewById(R.id.breakfasttv);
        snacktv = view.findViewById(R.id.snacktv);
        launctv = view.findViewById(R.id.launchtv);
        dinnertv = view.findViewById(R.id.dinnertv);
        titletv= view.findViewById(R.id.title_tv);
        pd = new ProgressDialog(context);
        pd.setMessage("Loading");
        pd.show();
        getProfile();

//        for (int i=0;i<foodItems.size();i++){
//          FirebaseDatabase  database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference("DietPlan");
//            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//            DatabaseReference quizRef = rootRef.child("id");
//            String key = quizRef.push().getKey();
//            Map<String, Object> taskMap = new HashMap<>();
//            taskMap.put("id", key);
//            taskMap.put("Name", foodItems.get(i));
//            taskMap.put("carbs", carbslist.get(i));
//            taskMap.put("fats", fatlist.get(i));
//            taskMap.put("protein",proteinslist.get(i));
//            taskMap.put("consumption",consumptionlist[i]);
//            taskMap.put("time",timelist[i]);
//            taskMap.put("cat",catlist[i]);
//            myRef.push().setValue(taskMap);
//            Snackbar snackbar = Snackbar.make(layout, "Item Added", Snackbar.LENGTH_SHORT);
//            snackbar.show();
//        }


    }

    private void getProfile() {
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("uid")
                .equalTo(Util.getSP(getContext()));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User yourModel = childSnapshot.getValue(User.class);

                    String p = yourModel.getBmi();
                    String bmr = yourModel.getBmr();
                    b = Double.parseDouble(bmr) * 0.2;
                    l = Double.parseDouble(bmr) * 0.4;
                    s = Double.parseDouble(bmr) * 0.1;
                    d = Double.parseDouble(bmr) * 0.3;
                    titletv.setText("Your Bmi is "+String.format("%.2f",Float.parseFloat(String.valueOf(yourModel.getBmi())))+" And Bmr is "+String.format("%.2f",Float.parseFloat(String.valueOf(bmr))));
                    breaktv.setText("BreakFast  (" + String.format("%.2f",Float.parseFloat(String.valueOf(b))) + ") cal");
                    launctv.setText("Launch  (" + String.format("%.2f",Float.parseFloat(String.valueOf(l))) + ") cal");
                    snacktv.setText("Snack  (" + String.format("%.2f",Float.parseFloat(String.valueOf(s))) + ") cal");
                    dinnertv.setText("Dinner  (" + String.format("%.2f",Float.parseFloat(String.valueOf(d))) + ") cal");
                    Log.d("b", String.valueOf(b));
                    Log.d("l", String.valueOf(l));
                    Log.d("s", String.valueOf(s));
                    Log.d("d", String.valueOf(d));
                    Log.d("bmi", p);
                    if (Double.parseDouble(p) < 18.5) {
                        result_tv.setText("You are Under Weight");
                        result = "Under Weight";
                    } else if (Double.parseDouble(p) < 24.9) {
                        result_tv.setText("You are Healthy");
                        result = "Healthy";
                    } else if (Double.parseDouble(p) < 99.9) {
                        result_tv.setText("You are Over Weight");
                        result = "Over Weight";
                    } else {

                    }
                    getdietplan(result);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());

            }
        });
    }

    private void getdietplan(String result) {

        Query query = FirebaseDatabase.getInstance().getReference("DietPlan")
                .orderByChild("id");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double breakfast = 0;
                double launch = 0;
                double dinner = 0;
                double snack = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Diet yourModel = childSnapshot.getValue(Diet.class);
                    String consumption = yourModel.getConsumption();
                    Log.d("consumption", consumption);
                    String p = yourModel.getTime();
                    if (consumption.contains(result)) {
                        if (p.contains("Breakfast")) {

                            if (b > breakfast) {
                                LayoutInflater li = LayoutInflater.from(context);
                                View cv = li.inflate(R.layout.breakfastlayout, null);
                                TextView item = cv.findViewById(R.id.item);
                                item.setText("Food Item: " + yourModel.getName());
                                TextView item2 = cv.findViewById(R.id.itemprotein);
                                item2.setText("Protein: " + yourModel.getProtein());
                                TextView item3 = cv.findViewById(R.id.itemcarbs);
                                item3.setText("Carbs: " + yourModel.getCarbs());
                                TextView item4 = cv.findViewById(R.id.itemfats);
                                item4.setText("Fats: " + yourModel.getFats());
                                TextView item5 = cv.findViewById(R.id.itemcategory);
                                item5.setText("Category: " + yourModel.getCat());
                                cardview.addView(cv);
                                breakfast = breakfast + yourModel.getCarbs() + yourModel.getFats() + yourModel.getProtein();
                                Log.d("break", String.valueOf(breakfast));
                            }
                        } else if (p.contains("Lunch")) {
                            if (l > launch) {
                                LayoutInflater li = LayoutInflater.from(context);
                                View cv = li.inflate(R.layout.dinner_layout, null);
                                TextView item = cv.findViewById(R.id.dinneritem);
                                item.setText("Food Item: " + yourModel.getName());
                                TextView item2 = cv.findViewById(R.id.dinneritemprotein);
                                item2.setText("Protein: " + yourModel.getProtein());
                                TextView item3 = cv.findViewById(R.id.dinneritemcarbs);
                                item3.setText("Carbs: " + yourModel.getCarbs());
                                TextView item4 = cv.findViewById(R.id.dinneritemfats);
                                item4.setText("Fats: " + yourModel.getFats());
                                TextView item5 = cv.findViewById(R.id.dinneritemcat);
                                item5.setText("Category: " + yourModel.getCat());
                                cardviewlunch.addView(cv);
                                launch = launch + yourModel.getCarbs() + yourModel.getFats() + yourModel.getProtein();
                                Log.d("launch", String.valueOf(launch));
                            }
                        } else if (p.contains("Dinner")) {
                            if (d > dinner) {
                                LayoutInflater li = LayoutInflater.from(context);
                                View cv = li.inflate(R.layout.dinner_layout, null);
                                TextView item = cv.findViewById(R.id.dinneritem);
                                item.setText("Food Item: " + yourModel.getName());
                                TextView item2 = cv.findViewById(R.id.dinneritemprotein);
                                item2.setText("Protein: " + yourModel.getProtein());
                                TextView item3 = cv.findViewById(R.id.dinneritemcarbs);
                                item3.setText("Carbs: " + yourModel.getCarbs());
                                TextView item4 = cv.findViewById(R.id.dinneritemfats);
                                item4.setText("Fats: " + yourModel.getFats());
                                TextView item5 = cv.findViewById(R.id.dinneritemcat);
                                item5.setText("Category: " + yourModel.getCat());
                                dinnercard.addView(cv);
                                dinner = dinner + yourModel.getCarbs() + yourModel.getFats() + yourModel.getProtein();
                                Log.d("dinner", String.valueOf(dinner));
                            }
                        } else if (p.contains("Snacks")) {
                            if (s > snack) {
                                LayoutInflater li = LayoutInflater.from(context);
                                View cv = li.inflate(R.layout.dinner_layout, null);
                                TextView item = cv.findViewById(R.id.dinneritem);
                                item.setText("Food Item: " + yourModel.getName());
                                TextView item2 = cv.findViewById(R.id.dinneritemprotein);
                                item2.setText("Protein: " + yourModel.getProtein());
                                TextView item3 = cv.findViewById(R.id.dinneritemcarbs);
                                item3.setText("Carbs: " + yourModel.getCarbs());
                                TextView item4 = cv.findViewById(R.id.dinneritemfats);
                                item4.setText("Fats: " + yourModel.getFats());
                                TextView item5 = cv.findViewById(R.id.dinneritemcat);
                                item5.setText("Category: " + yourModel.getCat());
                                snackcard.addView(cv);
                                snack = snack + yourModel.getCarbs() + yourModel.getFats() + yourModel.getProtein();
                                Log.d("dinner", String.valueOf(snack));
                            }
                        } else {

                        }
                    }
                    pd.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
