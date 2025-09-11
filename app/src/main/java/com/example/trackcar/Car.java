package com.example.trackcar;

import android.widget.Toast;

import java.util.ArrayList;

public class Car {
    private String year;
    private String make;
    private String model;
    private String trim;
    private String model_id;    //Used to get more details on car from the API
    public Car(ArrayList<String> basic_deatils){
        this.year = basic_deatils.get(0).toString();
        this.make = basic_deatils.get(1).toString();
        this.model = basic_deatils.get(2).toString();
        String trim = basic_deatils.get(3).toString();

        int trimEnds = trim.indexOf("[");
        int modelIdBegins = trim.indexOf(":");
        this.trim = trim.substring(0, trimEnds);
        this.model_id = trim.substring(modelIdBegins+1,trim.length()-1).trim();
    }

    //Setters and Getters

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTrim() {
        return trim;
    }

    public void setTrim(String trim) {
        this.trim = trim;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }
}
