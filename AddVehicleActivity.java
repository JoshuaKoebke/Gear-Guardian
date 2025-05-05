package com.example.gearguardian;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class AddVehicleActivity extends AppCompatActivity {
    private EditText  etVehicleName, etVehicleMake, etVehicleModel, etVehicleYear;
    private RadioGroup rgServiceInterval;
    private Button    btnSaveVehicle, btnCancelVehicle;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        etVehicleName     = findViewById(R.id.etVehicleName);
        etVehicleMake     = findViewById(R.id.etVehicleMake);
        etVehicleModel    = findViewById(R.id.etVehicleModel);
        etVehicleYear     = findViewById(R.id.etVehicleYear);
        rgServiceInterval = findViewById(R.id.rgServiceInterval);
        btnSaveVehicle    = findViewById(R.id.btnSaveVehicle);
        btnCancelVehicle  = findViewById(R.id.btnCancelVehicle);

        dbHelper = new DatabaseHelper(this);

        btnSaveVehicle.setOnClickListener(v -> {
            String name  = etVehicleName.getText().toString().trim();
            String make  = etVehicleMake.getText().toString().trim();
            String model = etVehicleModel.getText().toString().trim();
            String yearS = etVehicleYear.getText().toString().trim();

            if (name.isEmpty() || make.isEmpty() || model.isEmpty() || yearS.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int year;
            try {
                year = Integer.parseInt(yearS);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid year format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determine interval days
            long days;
            int sel = rgServiceInterval.getCheckedRadioButtonId();
            if      (sel == R.id.rbWeekly)    days = 7;
            else if (sel == R.id.rbMonthly)   days = 30;
            else /*rbQuarterly*/              days = 90;

            String intervalText = days + " days";

            // Insert vehicle and retrieve its new ID
            long newId = dbHelper.insertVehicleReturnId(
                    name, make, model, year, intervalText
            );
            if (newId == -1) {
                Toast.makeText(this, "Error saving vehicle", Toast.LENGTH_SHORT).show();
                return;
            }

            OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ServiceDueWorker.class)
                    .setInitialDelay(days, TimeUnit.DAYS)
                    .setInputData(new Data.Builder()
                            .putInt("vehicleId", (int)newId)
                            .build()
                    ).build();
            WorkManager.getInstance(this).enqueue(work);

            Toast.makeText(this, "Vehicle added!", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnCancelVehicle.setOnClickListener(v -> finish());
    }
}
