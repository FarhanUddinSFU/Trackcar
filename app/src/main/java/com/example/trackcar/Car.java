package com.example.trackcar;


import java.io.Serializable;
import java.util.ArrayList;

public class Car implements Serializable {
    //vehicleID is generated at random when car is added to firebase
    private String vehicleId;
    private String year;
    private String make;
    private String model;

    //The additional info will be displayed and sometimes used to calculate maintenance intervals
    private String engine_cyl;
    private String engine_fuel;
    private String engine_hp;
    private String engine_drive;
    private String trim;


    private int currentKM;
    private int averageKMYearly;
    private int averageKMMonthly;

    public Car(ArrayList<String> basic_details){
        //If no vin is provided we can still add basic details
        this.year = basic_details.get(0);
        this.make = basic_details.get(1);
        this.model = basic_details.get(2);
        this.engine_cyl = null;
        this.engine_fuel = null;
        this.engine_hp = null;
        this.engine_drive = null;
        this.trim = null;
    }
    //Used to update the currentKm daily even when the app isn't open.
    //The changing KM will be used to set off reminder functions
    public void updateCurrentKM(){

    }
    public Car(){}

    //Setters and Getters

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
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

    public String getEngine_cyl() {
        return engine_cyl;
    }

    public void setEngine_cyl(String engine_cyl) {
        this.engine_cyl = engine_cyl;
    }

    public String getEngine_fuel() {
        return engine_fuel;
    }

    public void setEngine_fuel(String engine_fuel) {
        this.engine_fuel = engine_fuel;
    }

    public String getEngine_hp() {
        return engine_hp;
    }

    public void setEngine_hp(String engine_hp) {
        this.engine_hp = engine_hp;
    }

    public String getEngine_drive() {
        return engine_drive;
    }

    public void setEngine_drive(String engine_drive) {
        this.engine_drive = engine_drive;
    }
    public int getCurrentKM() {
        return currentKM;
    }

    public void setCurrentKM(int currentKM) {
        this.currentKM = currentKM;
    }

    public int getAverageKMYearly() {
        return averageKMYearly;
    }

    public void setAverageKMYearly(int averageKMYearly) {
        this.averageKMYearly = averageKMYearly;
    }

    public int getAverageKMMonthly() {
        return averageKMMonthly;
    }

    public void setAverageKMMonthly(int averageKMMonthly) {
        this.averageKMMonthly = averageKMMonthly;
    }
}
