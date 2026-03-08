package com.example.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.LoginActivity;
import com.example.Model.User;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChangePassFrag extends Fragment {
    TextInputEditText oldpass, newpass;
    Button cpbtn;
    LinearLayout layout;
    String uid;
    ProgressBar pb;
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.changepass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Change Password");
        oldpass = view.findViewById(R.id.cp_oldpassedt);
        newpass = view.findViewById(R.id.cp_newpassedt);
        cpbtn = view.findViewById(R.id.cp_btn);
        layout = view.findViewById(R.id.layout_cp);
        pb = view.findViewById(R.id.pb_cp);
        cpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (oldpass.getText().toString().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(layout, "Enter Old Password", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else if (newpass.getText().toString().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(layout, "Enter New Password", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    cp();
                }
            }

        });

    }

    private void cp() {

        pb.setVisibility(View.VISIBLE);


        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("uid")
                .equalTo(Util.getSP(getContext()));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User yourModel = childSnapshot.getValue(User.class);

                    String pass = yourModel.getPass();
                    Log.d("res", pass);
                    if (pass.equals(oldpass.getText().toString().trim())) {

                        childSnapshot.getRef().child("Pass").setValue(newpass.getText().toString());
                        pb.setVisibility(View.GONE);
                        Util.setSP(getContext(), "");
                        Snackbar snackbar = Snackbar.make(layout, "Password Changed ", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        pb.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(layout, "Old Password is Wrong", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());


            }
        });


    }
}
