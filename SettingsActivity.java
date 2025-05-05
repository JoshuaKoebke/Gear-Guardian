package com.example.gearguardian;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchNotifications;
    private Button btnClearData, btnBackSettings;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);
        // Initialize views
        switchNotifications = findViewById(R.id.switchNotifications);
        btnClearData = findViewById(R.id.btnClearData);
        btnBackSettings = findViewById(R.id.btnBackSettings);

        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement data clearing logic here
                Toast.makeText(SettingsActivity.this, "Data cleared", Toast.LENGTH_SHORT).show();
                dbHelper.clearAllData();
            }
        });


        btnBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish this activity to return to the previous one
                finish();
            }
        });
    }
}
