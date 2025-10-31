package com.example.trackcar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class specificCar extends AppCompatActivity {
    private Car currentCar;
    private FrameLayout fL;
    private Button viewCar;
    private Button viewMaint;
    private FirebaseFirestore fb;
    private FirebaseAuth user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_specific_car);

        // Handle system insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the car object passed from the previous activity
        currentCar = (Car)getIntent().getSerializableExtra("carObject");

        // Initialize views and Firebase
        fL = findViewById(R.id.framelayout);
        viewCar = findViewById(R.id.viewCar);
        viewMaint = findViewById(R.id.viewMaint);
        fb = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance();

        // By default when this view is opened it shows car details first
        updateFrameLayout(currentCar);

        // View Car button listener
        viewCar.setOnClickListener(v -> {
            updateFrameLayout(currentCar);
        });

        // View Maintenance button listener
        viewMaint.setOnClickListener(v -> {
            // First go through Firebase and retrieve record of maintenance, then update the layout
            fb.collection("maintenanceInfo")
                    .document(user.getUid())
                    .collection("cars")
                    .document(currentCar.getVehicleId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            maintenanceInfo maintInfo = documentSnapshot.toObject(maintenanceInfo.class);

                            if (maintInfo != null) {
                                // Update layout with maintenance info
                                updateFrameLayoutWithMaintInfo(maintInfo);
                            } else {
                                Log.d("viewMaint", "Maintenance info is null");
                            }

                        } else {
                            Log.d("viewMaint", "Failed to load maintenance info");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d("viewMaint2", "Failed to load maintenance info: " + e.getMessage());
                    });
        });
    }

    // Display car details in FrameLayout
    public void updateFrameLayout(Car car){
        // Clear old content first
        fL.removeAllViews();

        // Create a LinearLayout to hold TextViews vertically
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        // Helper method to create TextViews
        layout.addView(createTextView("Year: " + car.getYear()));
        layout.addView(createTextView("Make: " + car.getMake()));
        layout.addView(createTextView("Model: " + car.getModel()));
        layout.addView(createTextView("Trim: " + car.getTrim()));
        layout.addView(createTextView("Engine Cylinders: " + car.getEngine_cyl()));
        layout.addView(createTextView("Fuel Type: " + car.getEngine_fuel()));
        layout.addView(createTextView("Horsepower: " + car.getEngine_hp()));
        layout.addView(createTextView("Drive: " + car.getEngine_drive()));
        layout.addView(createTextView("Current KM: " + car.getCurrentKM()));
        layout.addView(createTextView("Avg KM Yearly: " + car.getAverageKMYearly()));
        layout.addView(createTextView("Avg KM Monthly: " + car.getAverageKMMonthly()));

        // Add the LinearLayout to the FrameLayout
        fL.addView(layout);
    }

    // Overridden method: Call this when View Maintenance button is clicked
    // Reusable method to update the FrameLayout with maintenance info
    // Pass in a maintenanceInfo object
    public void updateFrameLayoutWithMaintInfo(maintenanceInfo maintInfo) {
        // Clear old content first
        fL.removeAllViews();

        // Create a LinearLayout to hold TextViews vertically
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        // Add all maintenance fields dynamically
        layout.addView(createTextView("Oil Interval: " + maintInfo.getOilInterval() + " km"));
        layout.addView(createTextView("Last Oil Change: " + maintInfo.getLastOilChange() + " km"));
        layout.addView(createTextView("Next Oil Change: " + maintInfo.getNextOilChange() + " km"));
        layout.addView(createTextView("Brake Pad Interval: " + maintInfo.getBrakePadInterval() + " km"));
        layout.addView(createTextView("Brake Rotor Interval: " + maintInfo.getBrakeRotorInterval() + " km"));
        layout.addView(createTextView("Engine Air Filter Change: " + maintInfo.getEngineAirFilterChange() + " km"));
        layout.addView(createTextView("Brake Fluid Change: " + maintInfo.getBrakeFluidChange() + " km"));
        layout.addView(createTextView("Tire Rotation Interval: " + maintInfo.getTireRotation() + " km"));
        layout.addView(createTextView("Last Tire Rotation: " + maintInfo.getLastTireRotation() + " km"));
        layout.addView(createTextView("Timing Belt Interval: " + maintInfo.getTimingBeltInterval() + " km"));
        layout.addView(createTextView("Coolant Change Interval: " + maintInfo.getCoolant() + " km"));
        layout.addView(createTextView("Current KM (Maintenance): " + maintInfo.getCurrentKm() + " km"));

        // Add the LinearLayout to the FrameLayout
        fL.addView(layout);
    }

    // Helper method to create styled TextViews
    private TextView createTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.DKGRAY);
        tv.setPadding(0, 8, 0, 8); // spacing between lines
        return tv;
    }
}
