package com.example.trackcar;

import java.util.Date;

//Keep all records in a different database will also keep track of finances
public class maintenanceRecord {

    private String maintenanceType;
    private int costForMaintenance;
    private boolean completedOrIgnored;
    private int km;          // Current km at the time of maintenance
    private Date date;       // Date of maintenance

    public maintenanceRecord(String mT, Date d, int currKm){
        this.maintenanceType = maintenanceType;
        this.date = d;
        this.km = currKm;
    }
    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public int getCostForMaintenance() {
        return costForMaintenance;
    }

    public void setCostForMaintenance(int costForMaintenance) {
        this.costForMaintenance = costForMaintenance;
    }

    public boolean isCompletedOrIgnored() {
        return completedOrIgnored;
    }

    public void setCompletedOrIgnored(boolean completedOrIgnored) {
        this.completedOrIgnored = completedOrIgnored;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }


}
