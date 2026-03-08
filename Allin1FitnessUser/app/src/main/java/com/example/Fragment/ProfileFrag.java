package com.example.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.Model.User;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileFrag extends Fragment {
    Toolbar toolbar;
    LinearLayout layout;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextInputEditText name, email, height, weight, age;
    String gender;
    Button register_btn;
    String bmi, bmr;
    FirebaseDatabase database;
    ProgressBar pb;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        radioGroup = view.findViewById(R.id.pradioGender);
        register_btn = view.findViewById(R.id.pregisteration_btn);
        name = view.findViewById(R.id.pname_edt);
        email = view.findViewById(R.id.pemail_edt);
        height = view.findViewById(R.id.pheight_edt);
        weight = view.findViewById(R.id.pweight_edt);
        age = view.findViewById(R.id.page_edt);

        layout = view.findViewById(R.id.playout_reg);
        pb = view.findViewById(R.id.ppb_reg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);
                gender = radioButton.getText().toString();

            }
        });
        getProfile();
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if (name.getText().toString().isEmpty()) {
                    pb.setVisibility(View.GONE);
                    Snackbar.make(layout, "Enter Name", Snackbar.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    pb.setVisibility(View.GONE);
                    Snackbar.make(layout, "Enter Email", Snackbar.LENGTH_SHORT).show();
                } else if (height.getText().toString().isEmpty()) {
                    pb.setVisibility(View.GONE);
                    Snackbar.make(layout, "Enter Height", Snackbar.LENGTH_SHORT).show();
                } else if (weight.getText().toString().isEmpty()) {
                    pb.setVisibility(View.GONE);
                    Snackbar.make(layout, "Enter Weight", Snackbar.LENGTH_SHORT).show();
                } else if (age.getText().toString().isEmpty()) {
                    pb.setVisibility(View.GONE);
                    Snackbar.make(layout, "Enter Age", Snackbar.LENGTH_SHORT).show();
                } else {

                    calculatebmi(Float.parseFloat(height.getText().toString()), Float.parseFloat(weight.getText().toString()));
                    calculatebmr(Float.parseFloat(height.getText().toString()), Float.parseFloat(weight.getText().toString()), Float.parseFloat(age.getText().toString()), gender);
                    update();
                }

            }
        });
    }

    private void getProfile() {
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("uid")
                .equalTo(Util.getSP(getContext()));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pb.setVisibility(View.VISIBLE);
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User yourModel = childSnapshot.getValue(User.class);

                    String p = yourModel.getPass();
                    String uid = yourModel.getUid();
                    String name1 = yourModel.getName();
                    String email1 = yourModel.getEmail();
                    String age1 = yourModel.getAge();
                    String heigt1 = yourModel.getHeight();
                    String weight1 = yourModel.getWeight();
                    String gender1 = yourModel.getGender();
                    if (gender1.equals("Male")) {
                        radioGroup.check(R.id.pradioMale);
                    } else {
                        radioGroup.check(R.id.pradioFemale);
                    }

                    Log.d("res", p);
                    name.setText(name1);
                    email.setText(email1);
                    age.setText(age1);
                    height.setText(heigt1);
                    weight.setText(weight1);
                    pb.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());

            }
        });

    }

    private void update() {
        String n = name.getText().toString();
        String e = email.getText().toString();
        String h = height.getText().toString();
        String w = weight.getText().toString();
        String a = age.getText().toString();

        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("uid")
                .equalTo(Util.getSP(getContext()));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User yourModel = childSnapshot.getValue(User.class);
                    childSnapshot.getRef().child("Name").setValue(n);
                    childSnapshot.getRef().child("Email").setValue(e);
                    childSnapshot.getRef().child("Height").setValue(h);
                    childSnapshot.getRef().child("Height").setValue(h);
                    childSnapshot.getRef().child("Weight").setValue(w);
                    childSnapshot.getRef().child("Age").setValue(a);
                    childSnapshot.getRef().child("Gender").setValue(gender);
                    Snackbar snackbar = Snackbar.make(layout, "Updated!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    pb.setVisibility(View.GONE);
                    getProfile();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());
            }
        });

    }

    private void calculatebmr(float height, float weight, float age, String gender) {
        if (gender.equals("Male")) {
            bmr = String.valueOf((88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)));
            Log.d("gender", gender);
            Log.d("bmr", bmr);
        } else if (gender.equals("Female")) {
            bmr = String.valueOf((447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)));
            Log.d("gender", gender);
            Log.d("bmr", bmr);
        } else {

        }

    }

    private void calculatebmi(float height, float weight) {
        bmi = String.valueOf(weight / (height * height));
        Log.d("bmi", bmi);
    }

}
