package com.example.gearguardian;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ServiceDueWorker extends Worker {
    private static final String CHANNEL_ID = "service_due_channel";

    public ServiceDueWorker(@NonNull Context ctx, @NonNull WorkerParameters params) {
        super(ctx, params);
    }

    @NonNull @Override
    public Result doWork() {
        int vehicleId = getInputData().getInt("vehicleId", -1);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        String name = "Your vehicle";
        try (android.database.Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT name FROM vehicle WHERE id = ?",
                new String[]{ String.valueOf(vehicleId) }
        )) {
            if (c.moveToFirst()) name = c.getString(0);
        }

        NotificationManager nm =
                (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Service Due Alerts",
                            NotificationManager.IMPORTANCE_HIGH
                    )
            );
        }

        NotificationCompat.Builder notif = new NotificationCompat.Builder(
                getApplicationContext(),
                CHANNEL_ID
        )
                .setContentTitle("Service Due")
                .setContentText("Time to service: " + name)
                .setSmallIcon(R.drawable.ic_car)
                .setAutoCancel(true);

        nm.notify((int)vehicleId, notif.build());
        return Result.success();
    }
}
