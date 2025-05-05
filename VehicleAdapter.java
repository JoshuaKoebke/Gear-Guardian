package com.example.gearguardian;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class VehicleAdapter
        extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {

    /** Callback interface for vehicle clicks */
    public interface OnVehicleClick {
        void onClick(String name);
    }

    private final List<String> data;
    private final OnVehicleClick callback;

    public VehicleAdapter(List<String> data, OnVehicleClick callback) {
        this.data = data;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {
        String name = data.get(position);
        holder.tvVehicleName.setText(name);

        holder.cardVehicle.setOnClickListener(v ->
                callback.onClick(name)
        );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView cardVehicle;
        final TextView tvVehicleName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardVehicle = itemView.findViewById(R.id.cardVehicle);
            tvVehicleName  = itemView.findViewById(R.id.tvVehicleName);
        }
    }
}
