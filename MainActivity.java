package com.example.gear_guardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView vehicleList;
    private Button addVehicleButton;
    private ArrayList<String> vehicles;
    private ArrayAdapter<String> vehicleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // home screen layout

        vehicleList       = findViewById(R.id.rvVehicleList);
        addVehicleButton  = findViewById(R.id.btnAddVehicle);

        // Sample data; replace with dbHelper.getAllVehicles() if you have a Vehicle table
        vehicles = new ArrayList<>();
        vehicles.add("My Car");
        vehicles.add("Family Van");
        vehicles.add("Work Truck");

        vehicleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                vehicles
        );
        vehicleList.setAdapter(vehicleAdapter);
        vehicleList.setOnItemClickListener(this);

        addVehicleButton.setOnClickListener(v -> {
            // Launch your AddVehicleActivity (not shown here)
            Intent intent = new Intent(MainActivity.this, AddVehicleActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedVehicle = vehicles.get(position);
        // Open CarLogActivity, passing the vehicle name
        Intent intent = new Intent(this, CarLogActivity.class);
        intent.putExtra("vehicleName", selectedVehicle);
        startActivity(intent);
    }
}
