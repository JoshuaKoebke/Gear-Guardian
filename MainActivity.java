package com.example.gearguardian;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView      vehicleList;
    private Button            addVehicleButton, btnSettings;
    private ArrayList<String> vehicles;
    private VehicleAdapter    vehicleAdapter;
    private DatabaseHelper    dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Find & configure the RecyclerView
        vehicleList = findViewById(R.id.rvVehicleList);
        vehicleList.setLayoutManager(new LinearLayoutManager(this));

        // Prepare the adapter with an empty list and attach it
        vehicles = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(
                vehicles,
                name -> {
                    // On item click, open CarLogActivity
                    Intent i = new Intent(MainActivity.this, CarLogActivity.class);
                    i.putExtra("vehicleName", name);
                    startActivity(i);
                }
        );
        vehicleList.setAdapter(vehicleAdapter);

        addVehicleButton = findViewById(R.id.btnAddVehicle);
        addVehicleButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddVehicleActivity.class))
        );

        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload from DB whenever this screen comes back into view
        loadVehiclesFromDatabase();
        vehicleAdapter.notifyDataSetChanged();
    }

    /**
     * Query all vehicles from SQLite and refresh the RecyclerView.
     */
    private void loadVehiclesFromDatabase() {
        Cursor cursor = dbHelper.getAllVehicles();
        vehicles.clear();

        if (cursor.moveToFirst()) {
            int nameCol = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_VEHICLE_NAME
            );
            do {
                vehicles.add(cursor.getString(nameCol));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Tell the adapter to rebind views with the new data
        vehicleAdapter.notifyDataSetChanged();
    }
}
