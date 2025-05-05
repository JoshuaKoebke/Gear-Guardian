package com.example.gearguardian;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays one Button per repair type (from RepairData.REPAIR_MAP keys),
 * highlights the selected one, and exposes getSelectedRepair().
 */
public class RepairTypeAdapter
        extends RecyclerView.Adapter<RepairTypeAdapter.ViewHolder> {

    // Pull all repair names from mapping table
    private final List<String> repairTypes = new ArrayList<>(RepairData.REPAIR_MAP.keySet());
    private int selectedPos = RecyclerView.NO_POSITION;

    /** Returns the currently selected repair, or null if none. */
    public String getSelectedRepair() {
        if (selectedPos >= 0 && selectedPos < repairTypes.size()) {
            return repairTypes.get(selectedPos);
        }
        return null;
    }

    @NonNull
    @Override
    public RepairTypeAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repair_type, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RepairTypeAdapter.ViewHolder holder, int position) {
        String type = repairTypes.get(position);
        holder.btn.setText(type);

        // Highlight the selected button
        if (position == selectedPos) {
            holder.btn.setBackgroundColor(Color.parseColor("#00796B"));  // teal700
            holder.btn.setTextColor(Color.WHITE);
        } else {
            holder.btn.setBackgroundColor(Color.parseColor("#80CBC4"));  // teal200
            holder.btn.setTextColor(Color.BLACK);
        }

        // Handle taps: update selection and refresh old/new items
        holder.btn.setOnClickListener(v -> {
            int old = selectedPos;
            selectedPos = holder.getAdapterPosition();
            notifyItemChanged(old);
            notifyItemChanged(selectedPos);
        });
    }

    @Override
    public int getItemCount() {
        return repairTypes.size();
    }

    /** ViewHolder holds a single Button. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final Button btn;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.btnRepairType);
        }
    }
}
