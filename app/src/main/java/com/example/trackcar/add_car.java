package com.example.trackcar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class add_car extends AppCompatActivity {
    private static final String TAG = "AddCarActivity";
    private Spinner Car_year, Car_make, Car_model;
    //The vin number is used to look up specific details such as trim, engine cyl, mpg, hp and more
    private EditText Car_vin;
    private String carVinNumber = null;
    private ImageView carImage;
    // Will save and store users car info in firebase with basic info coming from spinners and full details coming from vin lookup
    private String chosen_year, chosen_make, chosen_model;
    ArrayList<String> fullCarDetails;
    private boolean vinEnteredSuccess = false;
    // The following is commonly used in AS to help run tasks on different threads to update UI safely
    private ExecutorService executor = Executors.newSingleThreadExecutor(); // Executes complex tasks such as API calls on new background thread
    private Handler handler = new Handler(Looper.getMainLooper()); // Android does not allow UI updates from background threads. Bring back to main thread to update UI
    //Used to save basic car info to firebase database
    private FirebaseAuth mAuth;
    private FirebaseFirestore userDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instantiate Spinner objects via their ID
        Car_year = findViewById(R.id.car_year);
        Car_make = findViewById(R.id.car_make);
        Car_model = findViewById(R.id.car_model);
        Car_vin = findViewById(R.id.vin_input);

        //Instantiate Firebase access
        mAuth = FirebaseAuth.getInstance();
        userDb = FirebaseFirestore.getInstance();

        //Instantiate the array that will contain all the car details
        fullCarDetails = new ArrayList<>();

        // We first populate the years spinner with valid years
        ArrayList<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR); // Get current year

        for (int year = 1960; year <= currentYear; year++) {
            years.add(String.valueOf(year));
        }
        dataToSpinners(years,Car_year);


        // If user selects a year perform this listener and save the year chosen then populate the Car_make spinner
        Car_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item
                chosen_year = parent.getItemAtPosition(position).toString();
                // If a year is selected then we can display the different car makes to the spinner
                getCarMakes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle no selection
            }
        });

        // If user selects a year and make perform this listener and save the make_chosen then populate the Car_model spinner
        Car_make.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosen_make = adapterView.getItemAtPosition(i).toString();
                // Once the make has been stored we can work on displaying the models list to the spinner
                getCarModel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // When user selects the year, make, and model last thing is to save all info and get the VIN number and get remaining details
        // so we can get more details such as HP and MPG later using a our VIN API lookup
        Car_model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosen_model = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    // Used to attach ArrayList data to spinners
    void dataToSpinners(ArrayList<String> dataList, Spinner spinnerToPopulate) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(add_car.this,
                android.R.layout.simple_spinner_item, dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerToPopulate.setAdapter(adapter);
    }

    // Function that handles the actual network call and returns the JSON string
    String returnJSONString(String APIURl) throws IOException {
        String jsonStrCleaned = "";
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(APIURl);
            conn = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder results = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
            jsonStrCleaned = results.toString().trim();
            // Clean JSONP (CarQuery sometimes returns JSON wrapped with ?(...); )
            if (jsonStrCleaned.startsWith("?(") && jsonStrCleaned.endsWith(");")) {
                jsonStrCleaned = jsonStrCleaned.substring(2, jsonStrCleaned.length() - 2);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL: " + APIURl, e);
        } catch (IOException e) {
            Log.e(TAG, "I/O error when calling API: " + APIURl, e);
            throw e;
        } finally {
            if (reader != null) reader.close();
            if (conn != null) conn.disconnect();
        }
        return jsonStrCleaned;
    }

    // Gets all car makes available for the selected year
    void getCarMakes() {
        executor.execute(() -> {
            try {
                // Try and secure a connection with the API to get list of makes from that year
                String APIUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json"; // The url we will make an API call from
                String jsonStr = returnJSONString(APIUrl);
                Log.d("jsonStr", jsonStr);
                JSONObject car_json = new JSONObject(jsonStr);
                JSONArray car_makes = car_json.getJSONArray("Results");
                ArrayList<String> make_ids = new ArrayList<>();
                for (int i = 0; i < car_makes.length(); i++) {
                    JSONObject current_car = car_makes.getJSONObject(i);
                    make_ids.add(current_car.getString("MakeName"));
                }
                // Update Spinner on main thread safely using ArrayAdapter
                handler.post(() -> dataToSpinners(make_ids, Car_make));
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Failed to load car makes", e);
                handler.post(() -> Toast.makeText(this, "Failed to load car makes", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Gets the car models to populate the spinner
    void getCarModel() {
        executor.execute(() -> {
            try {
                String APIUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/getmodelsformakeyear/make/"+chosen_make+"/modelyear/"+chosen_year+"?format=json";
                String jsonStr = returnJSONString(APIUrl);
                JSONObject json = new JSONObject(jsonStr);
                JSONArray modelsList = json.getJSONArray("Results");
                ArrayList<String> models = new ArrayList<>();
                for (int i = 0; i < modelsList.length(); i++) {
                    JSONObject current_model = modelsList.getJSONObject(i);
                    models.add(current_model.getString("Model_Name"));
                }
                handler.post(() -> dataToSpinners(models, Car_model));
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Failed to load car models", e);
                handler.post(() -> Toast.makeText(this, "Failed to load car models", Toast.LENGTH_SHORT).show());
            }
        });
    }
    //When the next button is clicked on add_car activity signalling that we're done
    public void createCarObject(View V){
        ArrayList<String> basicDetails = new ArrayList<>();
        if(!chosen_year.isEmpty() && !chosen_make.isEmpty() && !chosen_model.isEmpty()) {
            basicDetails.add(chosen_year);
            basicDetails.add(chosen_make);
            basicDetails.add(chosen_model);
            //Elements from the basicDetails ArrayList will fill in the basic attributes in the car object
            Car car = new Car(basicDetails);

            //Add additional car details if a VIN number was provided

            if(vinEnteredSuccess == true) {
                car.setTrim(fullCarDetails.get(0).trim());
                car.setEngine_cyl(fullCarDetails.get(1).trim());
                car.setEngine_fuel(fullCarDetails.get(2).trim());
                car.setEngine_drive(fullCarDetails.get(3).trim());
                car.setEngine_hp(fullCarDetails.get(4).trim());

                //Add the newly made car object to the users firebase database
                FirebaseUser user = mAuth.getCurrentUser();
                userDb.collection("Users").document(user.getUid())
                        .collection("Vehicles").add(car)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Info stored", Toast.LENGTH_SHORT).show();
                            String uid = documentReference.getId();
                            documentReference.update("vehicleId", uid);
                            Intent i = new Intent(this, questionare.class);
                            i.putExtra("carID", documentReference.getId());
                            i.putExtra("engineCYl",car.getEngine_cyl());
                            Toast.makeText(this, documentReference.getId(), Toast.LENGTH_LONG).show();
                            startActivity(i);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to save Car: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("failed to save car", e.getMessage());
                        });
            }
            else{
                Toast.makeText(this,"Please enter a VIN number", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this,"All fields must be chosen", Toast.LENGTH_LONG).show();
        }
    }
    //When user wants to add an image of his car he will click this add button
    public void addImage(View v){
        carImage = findViewById(R.id.imageView1);

    }
    public void vinClicked(View V)  {
        //When the vin is submitted get the rest of car details
        executor.execute(()->{
            try{
                carVinNumber = Car_vin.getText().toString().trim();
                String APIURL = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVinValues/"+carVinNumber+"?format=json";
                String jsonStr = returnJSONString(APIURL);
                JSONObject json = new JSONObject(jsonStr);
                JSONArray vinDetails = json.getJSONArray("Results");
                //First index will hold the trim
                //Second index will hold the mpg
                if (vinDetails.length() > 0) {
                    JSONObject car = vinDetails.getJSONObject(0);

                    // Extract values safely
                    String trim = car.optString("Trim", "N/A");
                    String cyl = car.optString("EngineCylinders", "N/A");
                    String fuel = car.optString("FuelTypePrimary", "N/A");
                    String drive = car.optString("DriveType", "N/A");
                    String hp = car.optString("EngineHP", "N/A");

                    // Add to array list in this order we will later save to the Car object
                    handler.post(() -> {
                        fullCarDetails.clear(); // Clear old details if any
                        fullCarDetails.add(trim);
                        fullCarDetails.add(cyl);
                        fullCarDetails.add(fuel);
                        fullCarDetails.add(drive);
                        fullCarDetails.add(hp);
                        vinEnteredSuccess = true;
                        Toast.makeText(add_car.this, "VIN details loaded", Toast.LENGTH_SHORT).show();
                    });
                }
            }catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}