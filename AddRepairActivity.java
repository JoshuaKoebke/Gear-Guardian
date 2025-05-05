package com.example.gearguardian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddRepairActivity extends AppCompatActivity {
    private RepairTypeAdapter adapter;
    private DatabaseHelper     dbHelper;
    private int                vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repair);

        // Pull vehicleId from Intent extras
        vehicleId = getIntent().getIntExtra("vehicleId", -1);
        if (vehicleId == -1) {
            Toast.makeText(this, "Vehicle ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        // Bind RecyclerView & Buttons
        RecyclerView rv         = findViewById(R.id.rvRepairTypes);
        Button       btnConfirm = findViewById(R.id.btnConfirmRepair);
        Button       btnCancel  = findViewById(R.id.btnCancelRepair);

        // Set up RecyclerView
        adapter = new RepairTypeAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnConfirm.setOnClickListener(v -> {
            String picked = adapter.getSelectedRepair();
            if (picked == null) {
                Toast.makeText(this, "Please select a repair type.", Toast.LENGTH_SHORT).show();
                return;
            }

            long rowId = dbHelper.insertMaintenance(vehicleId, picked);
            if (rowId == -1) {
                Toast.makeText(this, "Error saving repair.", Toast.LENGTH_SHORT).show();
                return;
            }

            String suggestion = RepairData.REPAIR_MAP.getOrDefault(picked, "—");
            Toast.makeText(
                    this,
                    "Saved: " + picked + "\nNext suggested: " + suggestion,
                    Toast.LENGTH_LONG
            ).show();

            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
