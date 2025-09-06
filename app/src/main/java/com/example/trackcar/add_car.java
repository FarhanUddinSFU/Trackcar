package com.example.trackcar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class add_car extends AppCompatActivity {
    private Spinner Car_year, Car_make, Car_model, Car_trim;
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
        Car_year = findViewById(R.id.car_year);
        Car_make = findViewById(R.id.car_make);
        Car_model = findViewById(R.id.car_model);
        Car_trim = findViewById(R.id.car_trim);

        //The following is commonly used in AS to help run tasks on different threads to update UI safely
        ExecutorService executor = Executors.newSingleThreadExecutor(); // Executes complex tasks such as API calls on new background thread
        Handler handler = new Handler(Looper.getMainLooper());// Android does not allow UI updates from background threads.Bring back to ain thread to update UI
        /*Quick notes for JSON
        * JSONObject = folder of key-value pairs
        *getJSONObject("key") = open a folder inside the folder
        *getString("key") = grab the value of a specific key
        * JSONArray fruits = obj.getJSONArray("Fruits"); for getting arrays in JSON
        * */
        executor.execute(()->{
            try {
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
    }

}