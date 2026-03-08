package com.example.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.Model.User;
import com.example.Utils.Util;
import com.example.allin1fitnessuser.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BMIandBMR extends Fragment {
    Toolbar toolbar;
    ProgressBar pb;
    LinearLayout layout;
    TextView bmi, bmr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bmiandbmr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout = view.findViewById(R.id.layout_bmiandbmr);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("BMI & BMR");
        pb = view.findViewById(R.id.pb_bmi);
        bmi = view.findViewById(R.id.bmi_tv);
        bmr = view.findViewById(R.id.bmr_tv);

        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("uid")
                .equalTo(Util.getSP(getContext()));

        Log.d("bmibmr",query.toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pb.setVisibility(View.VISIBLE);
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User yourModel = childSnapshot.getValue(User.class);
                    String bmiValue = yourModel.getBmi();
                    String bmrValue = yourModel.getBmr();

                    Log.d("bmibmr", bmiValue);
                    Log.d("bmibmr", bmrValue);

                    // Check if the string is not empty before parsing
                    if (!bmiValue.isEmpty()) {
                        bmi.setText(String.format("%.2f", Float.parseFloat(bmiValue)));
                    }
                    if (!bmrValue.isEmpty()) {
                        bmr.setText(String.format("%.2f", Float.parseFloat(bmrValue)));
                    }
                    pb.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("res", "Error " + error.toString());
            }
        });
    }

    private String convertToString(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Integer) {
            return String.valueOf((Integer) value);
        } else {
            // Handle other types or return empty string if not supported
            return "";
        }
    }


}
