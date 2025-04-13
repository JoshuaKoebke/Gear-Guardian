package com.example.gear_guardian;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AddVehicleActivity extends AppCompatActivity {

    // UI elements
    private EditText etVehicleName, etVehicleMake, etVehicleModel, etVehicleYear;
    private RadioGroup rgServiceInterval;
    private Button btnConfirmVehicle, btnCancelVehicle;

    // Database helper
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        // Initialize UI elements
        etVehicleName = findViewById(R.id.etVehicleName);
        etVehicleMake = findViewById(R.id.etVehicleMake);
        etVehicleModel = findViewById(R.id.etVehicleModel);
        etVehicleYear = findViewById(R.id.etVehicleYear);
        rgServiceInterval = findViewById(R.id.rgServiceInterval);
        btnConfirmVehicle = findViewById(R.id.btnSaveVehicle);
        btnCancelVehicle = findViewById(R.id.btnCancelVehicle);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set up the Confirm button click listener
        btnConfirmVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gather input from EditText fields
                String name = etVehicleName.getText().toString().trim();
                String make = etVehicleMake.getText().toString().trim();
                String model = etVehicleModel.getText().toString().trim();
                String yearString = etVehicleYear.getText().toString().trim();

                // Validate input
                if(name.isEmpty() || make.isEmpty() || model.isEmpty() || yearString.isEmpty()){
                    Toast.makeText(AddVehicleActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int year;
                try {
                    year = Integer.parseInt(yearString);
                } catch(NumberFormatException e) {
                    Toast.makeText(AddVehicleActivity.this, "Invalid year format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get selected service interval from the RadioGroup
                int selectedId = rgServiceInterval.getCheckedRadioButtonId();
                if(selectedId == -1){
                    Toast.makeText(AddVehicleActivity.this, "Please select a service interval", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton rbInterval = findViewById(selectedId);
                String serviceInterval = rbInterval.getText().toString();

                // Insert the vehicle data into the database
                boolean isInserted = dbHelper.insertVehicle(name, make, model, year, serviceInterval);
                if(isInserted) {
                    Toast.makeText(AddVehicleActivity.this, "Vehicle added successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, finish this activity to return to the main menu
                    finish();
                } else {
                    Toast.makeText(AddVehicleActivity.this, "Error adding vehicle", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the Cancel button click listener
        btnCancelVehicle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
