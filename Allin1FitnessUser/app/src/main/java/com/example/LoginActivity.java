package com.example;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.Model.User;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar pb;
    TextInputEditText username, pass;
    Button btn, register;
    LinearLayout layout;
    private static final int RC_SIGN_IN = 101 ;

    ViewPager viewPager;
    FloatingActionButton google;
    GoogleSignInClient mGoogleSignInClient;

    FirebaseAuth fAuth;
    float v=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        if (Util.getSP(getApplicationContext()).equals("")) {
            initui();
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        viewPager = findViewById(R.id.view_pager);
        google = findViewById(R.id.fab_google);
        google.setTranslationY(300);
        google.setAlpha(v);

        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("37505530447-hmfdimqavu67pbad66vptkq8ip8r4fus.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set OnClickListener for the Google sign-in button
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void initui() {
        register = findViewById(R.id.registerbtn);
        setSupportActionBar(toolbar);
        btn = (Button) findViewById(R.id.loginbtn);
        pb = (ProgressBar) findViewById(R.id.pb_login);
        username = (TextInputEditText) findViewById(R.id.email_edt);
        pass = (TextInputEditText) findViewById(R.id.pass_edt);
        layout = (LinearLayout) findViewById(R.id.linearLayout);

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
                    login();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void login() {
        pb.setVisibility(View.VISIBLE);

        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("Email")
                .equalTo(username.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("res", snapshot.toString());

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User yourModel = childSnapshot.getValue(User.class);

                    String p = yourModel.getPass();
                    String uid = yourModel.getUid();


                    if (p.equals(pass.getText().toString())) {
                        Snackbar snackbar = Snackbar.make(layout, "Login Successful!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
    
                        pb.setVisibility(View.GONE);
                        Util.setSP(getApplicationContext(),yourModel.getUid());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        pb.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(layout, "Wrong Password", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                }

                if (snapshot.getValue(User.class) == null) {
                    pb.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(layout, "Wrong Email or Password", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    Log.d("res", snapshot.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());
                pb.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to initiate Google sign-in process
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Email:"+user.getEmail()+"\n\n"+"Name:"+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    int doubleBackToExitPressed = 1;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressed == 2) {
            finishAffinity();
            System.exit(0);
        } else {
            doubleBackToExitPressed++;
            Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(() -> doubleBackToExitPressed = 1, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();

        if(user != null){
            Intent intent = new Intent(com.example.LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(com.example.LoginActivity.this).toBundle());
            finish();
        }
    }
}
