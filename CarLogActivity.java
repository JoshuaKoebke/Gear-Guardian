package com.example.gearguardian;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CarLogActivity extends AppCompatActivity {
    private TextView textCarName, textCountdown;
    private ListView listRepairs, listSuggestiveRepairs;
    private Button buttonBack, buttonResetCountdown, buttonAddRepair;

    private DatabaseHelper dbHelper;
    private int vehicleId;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        textCarName = findViewById(R.id.textCarName);
        textCountdown = findViewById(R.id.textCountdown);
        listRepairs = findViewById(R.id.listRepairs);
        listSuggestiveRepairs = findViewById(R.id.listSuggestiveRepairs);
        buttonBack = findViewById(R.id.buttonBack);
        buttonResetCountdown = findViewById(R.id.buttonResetCountdown);
        buttonAddRepair = findViewById(R.id.buttonAddRepair);

        dbHelper = new DatabaseHelper(this);

        // Retrieve vehicle name and ID from the intent
        Intent it = getIntent();
        String name = it.getStringExtra("vehicleName");
        textCarName.setText(name != null ? name : "Unknown Vehicle");

        // Lookup vehicleId using the name
        vehicleId = -1;
        if (name != null) {
            try (Cursor c = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT " + DatabaseHelper.COLUMN_VEHICLE_ID +
                            " FROM " + DatabaseHelper.TABLE_VEHICLE +
                            " WHERE " + DatabaseHelper.COLUMN_VEHICLE_NAME + " = ?",
                    new String[]{name}
            )) {
                if (c.moveToFirst()) {
                    vehicleId = c.getInt(0);
                }
            }
        }

        if (vehicleId == -1) {
            textCountdown.setText("Error: Vehicle not found");
            return;
        }

        buttonBack.setOnClickListener(v -> finish());

        buttonResetCountdown.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("countdown_prefs", MODE_PRIVATE);
            prefs.edit().putLong("last_reset_" + vehicleId, System.currentTimeMillis()).apply();
            startCountdown(); // Reset the countdown

            // Get the countdown interval in milliseconds
            String interval = "7 days"; // Default value if the database doesn't provide an interval
            try (Cursor vCursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT " + DatabaseHelper.COLUMN_SERVICE_INTERVAL +
                            " FROM " + DatabaseHelper.TABLE_VEHICLE +
                            " WHERE " + DatabaseHelper.COLUMN_VEHICLE_ID + " = ?",
                    new String[]{String.valueOf(vehicleId)}
            )) {
                if (vCursor != null && vCursor.moveToFirst()) {
                    interval = vCursor.getString(vCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_INTERVAL));
                }
            } catch (Exception e) {
                e.printStackTrace(); // Handle any errors in database query
            }

            // Get the countdown interval in milliseconds
            long totalMillis = parseDays(interval) * 24L * 60 * 60 * 1000;

            // Set up AlarmManager to trigger when countdown ends
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, CountdownReceiver.class);
            alarmIntent.putExtra("vehicleName", textCarName.getText().toString());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, vehicleId, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Schedule the alarm
            long triggerAtMillis = System.currentTimeMillis() + totalMillis;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        });

        buttonAddRepair.setOnClickListener(v -> {
            Intent r = new Intent(this, AddRepairActivity.class);
            r.putExtra("vehicleId", vehicleId);
            startActivity(r);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vehicleId != -1) {
            loadHistory();
            loadSuggestions();
            startCountdown();
        }
    }

    private void loadHistory() {
        ArrayList<String> hist = new ArrayList<>();
        try (Cursor c = dbHelper.getMaintenanceForVehicle(vehicleId)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndexOrThrow("service");
                do {
                    hist.add(c.getString(idx));
                } while (c.moveToNext());
            }
        }
        if (hist.isEmpty()) hist.add("No maintenance records found.");
        listRepairs.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hist));
    }

    private void loadSuggestions() {
        ArrayList<String> sug = new ArrayList<>();
        try (Cursor c = dbHelper.getMaintenanceForVehicle(vehicleId)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndexOrThrow("service");
                do {
                    String done = c.getString(idx);
                    String next = (RepairData.REPAIR_MAP != null)
                            ? RepairData.REPAIR_MAP.getOrDefault(done, "—")
                            : "—";
                    sug.add(next);
                } while (c.moveToNext());
            }
        }
        if (sug.isEmpty()) sug.add("No suggestions available.");
        listSuggestiveRepairs.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sug));
    }

    private void startCountdown() {
        if (timer != null) timer.cancel();

        String interval = "7 days"; // Default
        try (Cursor v = dbHelper.getReadableDatabase().rawQuery(
                "SELECT " + DatabaseHelper.COLUMN_SERVICE_INTERVAL +
                        " FROM " + DatabaseHelper.TABLE_VEHICLE +
                        " WHERE " + DatabaseHelper.COLUMN_VEHICLE_ID + " = ?",
                new String[]{String.valueOf(vehicleId)}
        )) {
            if (v != null && v.moveToFirst()) interval = v.getString(v.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_INTERVAL));
        } catch (Exception e) {
            e.printStackTrace(); // Handle database query errors
        }

        long days = parseDays(interval);
        if (days <= 0) {
            textCountdown.setText("No interval set.");
            return;
        }

        long totalMillis = days * 24L * 60 * 60 * 1000;
        SharedPreferences prefs = getSharedPreferences("countdown_prefs", MODE_PRIVATE);
        long lastReset = prefs.getLong("last_reset_" + vehicleId, System.currentTimeMillis());
        long elapsed = System.currentTimeMillis() - lastReset;
        long remaining = totalMillis - elapsed;

        if (remaining <= 0) {
            textCountdown.setText("Service due!");
            return;
        }

        timer = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long ms) {
                long s = ms / 1000,
                        d = s / 86400,
                        h = (s % 86400) / 3600,
                        m = (s % 3600) / 60,
                        sec = s % 60;
                textCountdown.setText(String.format("%d days %02d:%02d:%02d", d, h, m, sec));
            }

            @Override
            public void onFinish() {
                textCountdown.setText("Service due!");
            }
        }.start();
    }

    private long parseDays(String txt) {
        txt = txt.toLowerCase();
        String numPart = txt.replaceAll("\\D+", " ").trim();
        int n = 1;
        if (!numPart.isEmpty()) {
            try {
                n = Integer.parseInt(numPart.split("\\s+")[0]);
            } catch (NumberFormatException ignored) {}
        }
        if (txt.contains("week")) return n * 7L;
        if (txt.contains("month")) return n * 30L;
        return n; // default to days
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }
}
