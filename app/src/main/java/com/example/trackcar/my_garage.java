package com.example.trackcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class my_garage extends AppCompatActivity {

    private ArrayList<Car> carsInGarage;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CarAdapter carAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_garage);

        // Handle system insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.carList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Display cars from Firestore
        displayData();
    }
    // Called when "Add Car" button is pressed
    public void addCar(View v) {
        Intent i = new Intent(this, add_car.class);
        startActivity(i);
    }

    private void displayData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_LONG).show();
            return;
        }
        String uid = currentUser.getUid();

        db.collection("Users").document(uid)
                .collection("Vehicles")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    carsInGarage = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Car currentCar = document.toObject(Car.class);
                        if (currentCar != null) {
                            carsInGarage.add(currentCar);
                        }
                    }

                    // Create adapter & attach to RecyclerView
                    carAdapter = new CarAdapter(carsInGarage);
                    recyclerView.setAdapter(carAdapter);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cars failed to retrieve", Toast.LENGTH_LONG).show();
                    Log.e("Firestore", "Error retrieving cars", e);
                });
    }
}