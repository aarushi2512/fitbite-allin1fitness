package com.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.allin1fitnessuser.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
public class RegisterActivity extends AppCompatActivity {
    LinearLayout layout;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextInputEditText name, email, height, weight, age, pass;
    String gender;
    Button register_btn;
    String bmi, bmr;
    FirebaseDatabase database;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUi(); // Initialize UI elements
    }

    private void initUi() {
        layout = findViewById(R.id.registration_linear_layout);
        radioGroup = findViewById(R.id.radioGender);
        register_btn = findViewById(R.id.registeration_btn);
        name = findViewById(R.id.name_edt);
        email = findViewById(R.id.email_edt);
        height = findViewById(R.id.height_edt);
        weight = findViewById(R.id.weight_edt);
        age = findViewById(R.id.age_edt);
        pass = findViewById(R.id.pass_edt);
        gender = "Male";
        pb = findViewById(R.id.pb_reg);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                gender = radioButton.getText().toString();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if (isFieldEmpty(name) || isFieldEmpty(email) || isFieldEmpty(height) || isFieldEmpty(weight) || isFieldEmpty(age) || isFieldEmpty(pass)) {
                    pb.setVisibility(View.GONE);
                    Snackbar.make(layout, "All fields are required", Snackbar.LENGTH_SHORT).show();
                } else {
                    String n = name.getText().toString();
                    String e = email.getText().toString();
                    String h = height.getText().toString();
                    String w = weight.getText().toString();
                    String a = age.getText().toString();
                    String p = pass.getText().toString();
                    float bmiValue = Float.parseFloat(calculateBMI(Float.parseFloat(h), Float.parseFloat(w)));
                    float bmrValue = Float.parseFloat(calculateBMR(Float.parseFloat(h), Float.parseFloat(w), Float.parseFloat(a), gender));

                    saveUserData(n, e, h, w, gender, a, bmiValue, bmrValue, p);
                }
            }
        });
    }

    private boolean isFieldEmpty(TextInputEditText field) {
        return field.getText().toString().trim().isEmpty();
    }

    private void saveUserData(String name, String email, String height, String weight, String gender, String age, float bmi, float bmr, String password) {
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference quizRef = rootRef.child("uid");
        String key = quizRef.push().getKey();
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("uid", key);
        taskMap.put("Name", name);
        taskMap.put("Email", email);
        taskMap.put("Height", String.valueOf(height));
        taskMap.put("Weight", String.valueOf(weight));
        taskMap.put("Gender", gender);
        taskMap.put("Age", age);
        taskMap.put("Bmi", String.valueOf(bmi));
        taskMap.put("Bmr", String.valueOf(bmr));
        taskMap.put("Pass", password);
        myRef.push().setValue(taskMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pb.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Failed to register: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String calculateBMI(float height, float weight) {
        float bmi = weight / (height * height);
        return String.valueOf(bmi);
    }

    private String calculateBMR(float height, float weight, float age, String gender) {
        String bmr = "";
        if (gender.equals("Male")) {
            height = (height < 5) ? height * 100 : height;
            bmr = String.valueOf((10 * weight) + (6.25 * height) - (5 * age) + 5);
            Log.d("gender", gender);
            Log.d("bmr", bmr);
        } else if (gender.equals("Female")) {
            height = (height < 5) ? height * 100 : height;
            bmr = String.valueOf((10 * weight) + (6.25 * height) - (5 * age) - 161);
            Log.d("gender", gender);
            Log.d("bmr", bmr);
        }
        return bmr;
    }

}
