package com.example.trackcar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private ArrayList<Car> carGarage;
    //Get an array of cars from the
    public CarAdapter(ArrayList<Car> cars) {
        this.carGarage = cars;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for one car item (make sure you have car_item.xml created)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_view, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carGarage.get(position);

        holder.carName.setText(car.getMake() +" " +car.getTrim());     // make sure Car has getName()
        holder.carEngine.setText(car.getEngine_cyl() + " engine producing " + car.getEngine_hp()); // make sure Car has getEngine()
        holder.carKm.setText(String.valueOf(car.getCurrentKM())); // make sure Car has getKm()
    }

    @Override
    public int getItemCount() {
        return carGarage.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView carName;
        TextView carEngine;
        TextView carKm;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carName = itemView.findViewById(R.id.carDetails);
            carEngine = itemView.findViewById(R.id.carEngine);
            carKm = itemView.findViewById(R.id.carKm);
        }
    }
}