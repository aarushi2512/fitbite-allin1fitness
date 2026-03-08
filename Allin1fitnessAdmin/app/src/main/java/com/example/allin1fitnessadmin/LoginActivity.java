package com.example.allin1fitnessadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.allin1fitnessadmin.Utils.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar pb;
    TextInputEditText username, pass;
    Button btn;
    LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Util.getSP(getApplicationContext()).equals("")) {
            initui();
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }

    private void initui() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        setSupportActionBar(toolbar);
        btn = (Button) findViewById(R.id.loginbtn);
        pb = (ProgressBar) findViewById(R.id.pb_login);
        username = (TextInputEditText) findViewById(R.id.email_edt);
        pass = (TextInputEditText) findViewById(R.id.pass_edt);
        layout = (LinearLayout) findViewById(R.id.layout_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if (username.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Username", Snackbar.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                } else if (pass.getText().toString().isEmpty()) {
                    Snackbar.make(layout, "Enter Password", Snackbar.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                } else {

                    if (username.getText().toString().trim().equals("admin") && pass.getText().toString().trim().equals("admin")) {
                        Util.setSP(getApplicationContext(), "admin");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        pb.setVisibility(View.GONE);
                        finish();

                    } else {
                        pb.setVisibility(View.GONE);
                        Snackbar.make(layout, "InCorrect Username or Password!", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}