package com.example.trackcar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class add_car extends AppCompatActivity {
    private Spinner Car_year, Car_make, Car_model, Car_trim;
    private String chosen_year, chosen_make,chosen_model,chose_trim;
    private int car_id;
    private ExecutorService executor = Executors.newSingleThreadExecutor(); // Executes complex tasks such as API calls on new background thread
    private Handler handler = new Handler(Looper.getMainLooper());// Android does not allow UI updates from background threads.Bring back to ain thread to update UI
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
        //Spinner objects
        Car_year = findViewById(R.id.car_year);
        Car_make = findViewById(R.id.car_make);
        Car_model = findViewById(R.id.car_model);
        Car_trim = findViewById(R.id.car_trim);
        //find car years
        //The following is commonly used in AS to help run tasks on different threads to update UI safely
        /*Quick notes for JSON
        * JSONObject = folder of key-value pairs
        *getJSONObject("key") = open a folder inside the folder
        *getString("key") = grab the value of a specific key
        * JSONArray fruits = obj.getJSONArray("Fruits"); for getting arrays in JSON
        * */
        executor.execute(()->{
            try {
                //Give them the option to select a year first
                //Secure a connection with URL get the data, transfer it to readable strings using input reader
                //and then build a string clean it transfer it to a JSON object and get the needed values
                URL url = new URL("https://www.carqueryapi.com/api/0.3/?cmd=getYears"); //The url we will make an APi call from
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = input.readLine())!= null){
                    result.append(line);
                }
                input.close();
                conn.disconnect();
                String jsonStr = result.toString().trim();
                if (jsonStr.startsWith("?(") && jsonStr.endsWith(");")) {
                    jsonStr = jsonStr.substring(2, jsonStr.length() - 2);
                }
                JSONObject obj = new JSONObject(jsonStr);
                JSONObject yearsObj;
                try {
                    yearsObj = obj.getJSONObject("Years");
                } catch (JSONException e) {
                    // Sometimes API returns "Years" as a string
                    yearsObj = new JSONObject(obj.getString("Years"));
                }

                int minYear = yearsObj.getInt("min_year");
                int maxYear = yearsObj.getInt("max_year");
                // Generate list of years using the extracted JSON strings
                List<String> yearList = new ArrayList<>();
                for (int y = maxYear; y >= minYear; y--) { // descending order
                    Log.d("Year: ", "y");
                    yearList.add(String.valueOf(y));
                }

                // Update Spinner on main thread safely using ArrayAdapter very common for android studio
                handler.post(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(add_car.this,
                            android.R.layout.simple_spinner_item, yearList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Car_year.setAdapter(adapter);
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.d("CarYearError", "failed to load years", e);
                handler.post(() -> Toast.makeText(this, "Failed to load years", Toast.LENGTH_SHORT).show());
            }


        });
        //If user selects a year do this listener and save the year chosen
        Car_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item
                chosen_year = parent.getItemAtPosition(position).toString();
                //If a year is selected then we can display the different car makes
                getCarMakes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle no selection
            }
        });
        //When user selects a make store the make in a variable
        Car_make.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosen_make = adapterView.getItemAtPosition(i).toString();
                //Once the make has been stored we can work on displaying the models list
                getCarModel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //When user selects the year, make, and model last thing is to get the trim and also save the trim ID so we can get more details after
    }

    void getCarMakes(){
        executor.execute(() -> {
            try{
                //Try and secure a connection with the API to get list of makes fromt hat year
                URL url = new URL("https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getMakes&year="+chosen_year);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = input.readLine()) != null){
                    result.append(line);
                }
                input.close();
                conn.disconnect();
                String jsonStr = result.toString().trim();
                if (jsonStr.startsWith("?(") && jsonStr.endsWith(");")) {
                    jsonStr = jsonStr.substring(2, jsonStr.length() - 2);
                }
                JSONObject json = new JSONObject(jsonStr);
                JSONArray car_makes = json.getJSONArray("Makes");
                ArrayList<String> make_ids = new ArrayList<>();
                for(int i = 0; i < car_makes.length();i++){
                    JSONObject current_car = car_makes.getJSONObject(i);
                    make_ids.add(current_car.getString("make_display"));
                }
                // Update Spinner on main thread safely using ArrayAdapter very common for android studio
                handler.post(()->{
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(add_car.this,
                            android.R.layout.simple_spinner_item, make_ids);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Car_make.setAdapter(adapter);
                });


            }catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        });
    }
    void getCarModel(){
        //Retrives the car models to populate the spinner
        executor.execute(()->{
            try{
                URL url = new URL("https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getModels&make="+chosen_make+"&year="+chosen_year);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine())!= null){
                    result.append(line);
                }
                reader.close();
                String jsonStr = result.toString().trim();
                if (jsonStr.startsWith("?(") && jsonStr.endsWith(");")) {
                    jsonStr = jsonStr.substring(2, jsonStr.length() - 2);
                }
                JSONObject json = new JSONObject(jsonStr);
                JSONArray modelsList = json.getJSONArray("Models");
                ArrayList<String>models = new ArrayList<>();
                for(int i = 0; i < modelsList.length();i++){
                    JSONObject current_model = modelsList.getJSONObject(i);
                    models.add(current_model.getString("model_name"));
                }
                handler.post(()->{
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(add_car.this,
                            android.R.layout.simple_spinner_item, models);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Car_model.setAdapter(adapter);
                });

            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }
    void getCarTrim(){

    }


}