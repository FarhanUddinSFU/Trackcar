package com.example.trackcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

public class oilChangeDetails extends AppCompatActivity {
    private FirebaseAuth user;
    private FirebaseFirestore db;

    private Spinner typesOfOil;
    private Spinner drivingHabit;
    private EditText lastOilChange;
    private EditText manualInterval;
    private String chosenType;
    private String chosenStyle;

    private int km;
    private String engineCyl;

    String carID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_oil_change_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        typesOfOil = findViewById(R.id.oilTypes);
        drivingHabit = findViewById(R.id.drivingHabit);
        lastOilChange = findViewById(R.id.lastOilChange);
        manualInterval = findViewById(R.id.manualOilIntervalEntry);

        user = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        carID = getIntent().getStringExtra("carID");
        km = getIntent().getIntExtra("currentKm", 0);;
        engineCyl = getIntent().getStringExtra("engineCyl");
        if (engineCyl == null) engineCyl = "4";//Default value to avoid a crash


        //Populate the spinners with the details
        String[] oilTypes = {"synthetic blend", "conventional oil", "full synthetic"};
        String[] drivingTypes = {"severe driving","normal"};
        dataToSpinners(oilTypes, typesOfOil);
        dataToSpinners(drivingTypes,drivingHabit);
        //If the user selects have listeners ready to save it into a variable to store later
        typesOfOil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item
                chosenType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle no selection
            }
        });
        drivingHabit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item
                chosenStyle = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle no selection
            }
        });
    }
    void dataToSpinners(String[] dataList, Spinner spinnerToPopulate) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(oilChangeDetails.this,
                android.R.layout.simple_spinner_item, dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerToPopulate.setAdapter(adapter);
    }
    //When submit is clicked
    //Validate the data
    //If data is valid create a record for maintenceInfo for the car and fill it with some of the details
    public void submitOilData(View V){
        if(chosenType == null || chosenStyle == null || chosenType.isEmpty() || chosenStyle.isEmpty()){
            Toast.makeText(this,"Please select an oil type, and driving style", Toast.LENGTH_LONG).show();
            return;
        }

        if(lastOilChange.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Please enter last oil change KM", Toast.LENGTH_LONG).show();
            return;
        }

        int loc = Integer.parseInt(lastOilChange.getText().toString().trim());

        if(loc > km){
            Toast.makeText(this, "Last oil change cannot be greater than current KM", Toast.LENGTH_LONG).show();
            return;
        }

        int manualIntervalEntry = 0;
        if(!manualInterval.getText().toString().trim().isEmpty()){
            manualIntervalEntry = Integer.parseInt(manualInterval.getText().toString().trim());
        }
        //If the user enetered a manual oil change interval save that
        //If not we will manually compute the reccomended oil change interval using an algorithim from the details they gave
        if(manualIntervalEntry > 1){
            saveUserData(loc, manualIntervalEntry);
        } else {
            double calculatedInterval = 8000; // default base interval (km)
            double engineScore = 1; // add cylinder spinner later
            double oilTypeScore = 1;
            double drivingFactorScore = 1;
            //Engine CYL score
            if(!engineCyl.isEmpty()){
                switch(engineCyl){
                    case "3": engineScore = 1.0; break;
                    case "4" : engineScore = 1.0; break;
                    case "6" : engineScore = 0.9; break;
                    case "8" : engineScore = 0.85; break;
                    case "10" : engineScore = 0.85; break;
                    case "12" : engineScore = 0.85; break;
                    case "16" : engineScore = 0.85; break;
                }
            }
            // Oil type factor
            switch (chosenType) {
                case "synthetic blend": oilTypeScore = 0.8; break;
                case "conventional oil": oilTypeScore = 0.6; break;
                case "full synthetic": oilTypeScore = 1.0; break;
            }

            // Driving factor
            switch (chosenStyle) {
                case "severe driving": drivingFactorScore = 0.7; break;
                case "normal": drivingFactorScore = 1.0; break;
            }

            calculatedInterval = calculatedInterval * engineScore * oilTypeScore * drivingFactorScore;
            saveUserData(loc, calculatedInterval);
        }
    }
    void saveUserData(int lastOilChange, double oilChangeInterval){
        FirebaseUser currentUser = user.getCurrentUser();
        maintenanceInfo newMI = new maintenanceInfo(carID, currentUser.getUid());
        newMI.setLastOilChange(lastOilChange);
        newMI.setOilInterval(oilChangeInterval);
        //Call this function to calculate the next interval the oil change should occur at
        newMI.setNextOilChange();
        //I forgot to add the current KM in the class so we add that here
        newMI.setCurrentKm(km);
        //Since it's our first time creating this car we will create a new database called maintenanceInfo and store the data there
        db.collection("maintenanceInfo")
                .document(currentUser.getUid())
                .collection("cars")
                .document(carID)
                .set(newMI, SetOptions.merge()).addOnSuccessListener(a ->{
            Toast.makeText(this, "Info was successfully added", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(a->{
            Toast.makeText(this, "failed to add info", Toast.LENGTH_LONG).show();
            Log.d("Failed to add maintenance info", a.getMessage().toString());

        });
        Intent i = new Intent(this, my_garage.class);
        startActivity(i);
    }
}