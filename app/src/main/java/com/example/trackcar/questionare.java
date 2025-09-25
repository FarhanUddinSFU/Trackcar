package com.example.trackcar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class questionare extends AppCompatActivity {
    private String carID;
    private EditText currentKm;
    private EditText averageYear;
    private EditText averageMonth;
    private FirebaseAuth mAuth;
    private FirebaseFirestore userDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionare);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //We will add the additional data to the exact vehicle user is adding
        carID = getIntent().getStringExtra("carID");
        currentKm = findViewById(R.id.currentKmInput);
        averageYear = findViewById(R.id.yearlyKmInput);
        averageMonth = findViewById(R.id.monthlyAverageInput);
        mAuth = FirebaseAuth.getInstance();
        userDb = FirebaseFirestore.getInstance();

    }

    public void saveData(View v){
        //Basically validating the data the user has entered
        String km = currentKm.getText().toString().trim();
        String averageYearly = averageYear.getText().toString().trim();
        String averageMonthly = averageMonth.getText().toString().trim();
        // Check if current KM is entered
        if (km.isEmpty()) {
            Toast.makeText(this, "Please enter current KM", Toast.LENGTH_LONG).show();
            return;
        }
        // Check that **at least one** of yearly or monthly is entered
        if (averageYearly.isEmpty() && averageMonthly.isEmpty()) {
            Toast.makeText(this, "Please enter either yearly or monthly average KM", Toast.LENGTH_LONG).show();
            return;
        }
        int kmNum;
        int averageYearlyNum;
        int averageMonthlyNum;
        try {
            kmNum = Integer.parseInt(km);  // Will throw NumberFormatException if not a valid number
            averageYearlyNum = Integer.parseInt(averageYearly);
            averageMonthlyNum = Integer.parseInt(averageMonthly);
            if(kmNum < 0 || averageMonthlyNum < 0 || averageYearlyNum < 0){
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
            // valid number
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return;  // stop execution if input is invalid
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("currentKM", kmNum);
        updates.put("averageKMYearly", averageYearlyNum);
        updates.put("averageKMMonthly", averageMonthlyNum);

        userDb.collection("Users").document(mAuth.getUid())
                .collection("Vehicles").document(carID)
                .update(updates)  // updates with all in the updates map
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Details updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update details: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
