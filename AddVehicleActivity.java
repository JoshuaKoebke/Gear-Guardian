package com.example.gear_guardian;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddVehicleActivity extends AppCompatActivity {

    // UI elements
    private EditText etVehicleName;
    private EditText etVehicleMake;
    private EditText etVehicleModel;
    private EditText etVehicleYear;
    private RadioGroup rgServiceInterval;
    private Button btnSaveVehicle;
    private Button btnCancelVehicle;

    // Database helper
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);  // must match your XML filename

        // Bind UI elements to their XML counterparts
        etVehicleName      = findViewById(R.id.etVehicleName);
        etVehicleMake      = findViewById(R.id.etVehicleMake);
        etVehicleModel     = findViewById(R.id.etVehicleModel);
        etVehicleYear      = findViewById(R.id.etVehicleYear);
        rgServiceInterval  = findViewById(R.id.rgServiceInterval);
        btnSaveVehicle     = findViewById(R.id.btnSaveVehicle);
        btnCancelVehicle   = findViewById(R.id.btnCancelVehicle);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Save button handler
        btnSaveVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name  = etVehicleName.getText().toString().trim();
                String make  = etVehicleMake.getText().toString().trim();
                String model = etVehicleModel.getText().toString().trim();
                String yearStr = etVehicleYear.getText().toString().trim();

                // Validate inputs
                if (name.isEmpty() || make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
                    Toast.makeText(AddVehicleActivity.this,
                            "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int year;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddVehicleActivity.this,
                            "Invalid year format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get selected service interval
                int selectedId = rgServiceInterval.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(AddVehicleActivity.this,
                            "Please select", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton rbInterval = findViewById(selectedId);
                String serviceInterval = rbInterval.getText().toString();

                // Insert into database
                boolean inserted = dbHelper.insertVehicle(
                        name, make, model, year, serviceInterval
                );

                if (inserted) {
                    Toast.makeText(AddVehicleActivity.this,
                            "Vehicle added successfully", Toast.LENGTH_SHORT).show();
                    finish();  // return to previous screen
                } else {
                    Toast.makeText(AddVehicleActivity.this,
                            "Error adding vehicle", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel button handler
        btnCancelVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();  // simply close this activity
            }
        });
    }
}
