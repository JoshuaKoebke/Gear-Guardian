package com.example.gear_guardian;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ListView logsList;
    Button addRecordButton;
    ArrayList<String> logsArray;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        logsList = findViewById(R.id.logsList);
        addRecordButton = findViewById(R.id.addRecordButton);

        logsArray = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logsArray);
        logsList.setAdapter(adapter);

        // Insert a sample record for demonstration
        addSampleRecord();
        loadRecords();

        addRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // For demonstration, insert a sample record when button is clicked
                if (dbHelper.insertRecord("Oil Change", "2025-03-15", 49.99)) {
                    Toast.makeText(MainActivity.this, "Record added", Toast.LENGTH_SHORT).show();
                    loadRecords();
                } else {
                    Toast.makeText(MainActivity.this, "Error adding record", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Load all records from the database into the ListView
    private void loadRecords() {
        logsArray.clear();
        Cursor cursor = dbHelper.getAllRecords();
        if (cursor.getCount() == 0) {
            logsArray.add("No records found.");
        } else {
            while (cursor.moveToNext()) {
                String record = "Service: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SERVICE))
                        + " | Date: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                        + " | Cost: $" + cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_COST));
                logsArray.add(record);
            }
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    // Add a sample record to the database
    private void addSampleRecord() {
        dbHelper.insertRecord("Tire Rotation", "2025-02-10", 29.99);
    }
}
