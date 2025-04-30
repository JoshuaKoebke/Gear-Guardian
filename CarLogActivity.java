package com.example.gear_guardian;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

public class CarLogActivity extends AppCompatActivity {
    private TextView textCarName, textCountdown;
    private Button buttonResetCountdown, buttonAddRepair, buttonBack;
    private ListView listRepairs, listSuggestiveRepairs;

    private CountDownTimer oilChangeTimer;
    private long countdownDuration = 7 * 24 * 60 * 60 * 1000; // 7 days in ms
    private long remainingTime = countdownDuration;

    private static final String CHANNEL_ID = "oil_change_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);  // car log layout

        // Bind views
        textCarName            = findViewById(R.id.textCarName);
        textCountdown          = findViewById(R.id.textCountdown);
        buttonResetCountdown   = findViewById(R.id.buttonResetCountdown);
        buttonAddRepair        = findViewById(R.id.buttonAddRepair);
        buttonBack             = findViewById(R.id.buttonBack);
        listRepairs            = findViewById(R.id.listRepairs);
        listSuggestiveRepairs  = findViewById(R.id.listSuggestiveRepairs);

        // Get vehicle name from Intent
        String vehicleName = getIntent().getStringExtra("vehicleName");
        textCarName.setText(vehicleName != null ? vehicleName : "Unknown Vehicle");

        // Start countdown
        startOilChangeCountdown(remainingTime);

        // Reset countdown
        buttonResetCountdown.setOnClickListener(v -> {
            if (oilChangeTimer != null) oilChangeTimer.cancel();
            remainingTime = countdownDuration;
            startOilChangeCountdown(remainingTime);
            Toast.makeText(this, "Countdown reset", Toast.LENGTH_SHORT).show();
        });

        // Add repair (launch AddRepairActivity)
        buttonAddRepair.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddRepairActivity.class);
            intent.putExtra("vehicleName", vehicleName);
            startActivity(intent);
        });

        // Back to main menu
        buttonBack.setOnClickListener(v -> finish());

        // TODO: load actual repair lists into listRepairs and listSuggestiveRepairs
    }

    private void startOilChangeCountdown(long duration) {
        oilChangeTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                long days    = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                long hours   = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String time  = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
                textCountdown.setText(time);
            }
            public void onFinish() {
                textCountdown.setText("00:00:00:00");
                sendOilChangeNotification();
            }
        }.start();
    }

    private void sendOilChangeNotification() {
        createNotificationChannel();
        Intent intent = new Intent(this, CarLogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // use built-in icon or your own
                .setContentTitle("Oil Change Reminder")
                .setContentText("Your " + textCarName.getText() + " needs an oil change!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(true);

        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Oil Change Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for overdue oil changes");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }
}
