package com.example.gear_guardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class AddRepairActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerRepairTypes;
    private Button btnConfirm, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repair);  // Inflate the correct layout :contentReference[oaicite:7]{index=7}

        spinnerRepairTypes = findViewById(R.id.spinnerRepairTypes);
        btnConfirm         = findViewById(R.id.btnConfirmRepair);
        btnCancel          = findViewById(R.id.btnCancelRepair);

        // Data source for spinner
        List<String> repairTypes = Arrays.asList(
                "Oil Change", "Brake Pad Replacement", "Tire Rotation",
                "Tire Replacement", "Spark Plug Replacement", "Battery Replacement",
                "Transmission Fluid Change", "Coolant Flush", "Suspension Repair",
                "Fuel Pump Repair", "Timing Belt Replacement", "Exhaust System Repair",
                "Air Conditioning Repair", "Wheel Bearing Replacement"
        );

        // Adapter setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                repairTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepairTypes.setAdapter(adapter);  // Bind adapter :contentReference[oaicite:8]{index=8}
        spinnerRepairTypes.setOnItemSelectedListener(this);

        // Cancel returns to main screen
        btnCancel.setOnClickListener(v -> finish());

        // Confirm saves the selected repair
        btnConfirm.setOnClickListener(v -> {
            String selected = spinnerRepairTypes.getSelectedItem().toString();
            Toast.makeText(this, "Selected: " + selected, Toast.LENGTH_SHORT).show();
            // TODO: Save to database and map to suggestive repair
            finish();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        // Optional: React immediately to selection
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No action needed
    }
}
