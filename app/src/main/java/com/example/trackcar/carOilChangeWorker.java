package com.example.trackcar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.trackcar.maintenanceInfo;
import com.example.trackcar.maintenanceRecord;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Date;
import java.util.List;

public class carOilChangeWorker extends Worker {

    public carOilChangeWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = getInputData().getString("userId");

        if (userId == null) return Result.failure();

        try {
            // Fetch all cars for this user synchronously
            QuerySnapshot carSnapshot = Tasks.await(
                    db.collection("Users").document(userId)
                            .collection("Vehicles").get()
            );

            for (DocumentSnapshot carDoc : carSnapshot.getDocuments()) {
                maintenanceInfo info = carDoc.toObject(maintenanceInfo.class);
                if (info == null) continue;

                int curKm = info.getCurrentKm();
                double nextOilChange = info.getNextOilChange();

                if (curKm >= nextOilChange) {
                    // Add maintenance record
                    maintenanceRecord newMR = new maintenanceRecord("oil-change", new Date(), curKm);
                    Tasks.await(db.collection("maintenanceRecord")
                            .document(userId)
                            .collection("cars")
                            .document(info.getVehicleId())
                            .collection("records")
                            .add(newMR));

                    // Send notification
                    sendNotification(info.getVehicleId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }

        return Result.success();
    }

    private void sendNotification(String vehicleId) {
    //Work on this part
    }
}

