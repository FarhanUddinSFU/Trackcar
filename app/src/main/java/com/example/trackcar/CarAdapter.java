package com.example.trackcar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private ArrayList<Car> carGarage;
    //When Im in my adapter class and want to change activitys the adapter dosn't know what activity I'm in
    //So we pass on the my_garage activity which we are currently in
    private Context context;
    //Get an array of cars from the
    public CarAdapter(Context context, ArrayList<Car> cars) {
        this.carGarage = cars;
        this.context = context;
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
        //When a specific element in viewholder is clicked we want to open another page where user can see more data and update specific on specific car
        holder.itemView.setOnClickListener(v->{
            Intent i = new Intent(context, specificCar.class);
            i.putExtra("carObject",car);
            context.startActivity(i);
        });
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