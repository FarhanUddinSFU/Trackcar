package com.example.trackcar;
//Keep each of users cars maintenance info seperate database from the vehicles
public class maintenanceInfo {
    private String userId;
    private String vehicleId;
    //All the intervals that we will calculate and set for reminders
    private double oilInterval;
    private int lastOilChange;
    private int brakePadInterval;
    private int brakeRotorInterval;
    private int engineAirFilterChange;
    private int brakeFluidChange;
    private int tireRotation;
    private int lastTireRotation;
    private int timingBeltInterval;
    private int coolant;
    private int currentKm;
    private double nextOilChange;

    public maintenanceInfo(String vehicleId, String user){
        this.vehicleId =
        this.userId = user;
    };
    public maintenanceInfo(){

    };
    //Simply used to compute the oil change interval given the data of the car
    public void setNextOilChange(){
        //Call this function only after these two infos are filled
        this.nextOilChange = lastOilChange + oilInterval;
    };
    //Setter and getters
    public double getNextOilChange() {
        return nextOilChange;
    }
    public int getCurrentKm() {
        return currentKm;
    }

    public void setCurrentKm(int currentKm) {
        this.currentKm = currentKm;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getOilInterval() {
        return oilInterval;
    }

    public void setOilInterval(double oilInterval) {
        this.oilInterval = oilInterval;
    }

    public int getLastOilChange() {
        return lastOilChange;
    }

    public void setLastOilChange(int lastOilChange) {
        this.lastOilChange = lastOilChange;
    }

    public int getBrakePadInterval() {
        return brakePadInterval;
    }

    public void setBrakePadInterval(int brakePadInterval) {
        this.brakePadInterval = brakePadInterval;
    }
    public void setNextOilChange(double nextOilChange) {
        this.nextOilChange = nextOilChange;
    }

    public int getLastTireRotation() {
        return lastTireRotation;
    }

    public void setLastTireRotation(int lastTireRotation) {
        this.lastTireRotation = lastTireRotation;
    }
    public int getBrakeRotorInterval() {
        return brakeRotorInterval;
    }

    public void setBrakeRotorInterval(int brakeRotorInterval) {
        this.brakeRotorInterval = brakeRotorInterval;
    }

    public int getEngineAirFilterChange() {
        return engineAirFilterChange;
    }

    public void setEngineAirFilterChange(int engineAirFilterChange) {
        this.engineAirFilterChange = engineAirFilterChange;
    }

    public int getBrakeFluidChange() {
        return brakeFluidChange;
    }

    public void setBrakeFluidChange(int brakeFluidChange) {
        this.brakeFluidChange = brakeFluidChange;
    }

    public int getTireRotation() {
        return tireRotation;
    }

    public void setTireRotation(int tireRotation) {
        this.tireRotation = tireRotation;
    }

    public int getTimingBeltInterval() {
        return timingBeltInterval;
    }

    public void setTimingBeltInterval(int timingBeltInterval) {
        this.timingBeltInterval = timingBeltInterval;
    }

    public int getCoolant() {
        return coolant;
    }

    public void setCoolant(int coolant) {
        this.coolant = coolant;
    }

}
