package com.example.gearguardian;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.content.SharedPreferences;

public class CountdownService extends Service {
    private CountDownTimer timer;
    private long remainingTime = 0;
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        CountdownService getService() {
            return CountdownService.this;
        }
    }

    public void startCountdown(long totalMillis) {
        if (timer != null) {
            timer.cancel();
        }

        remainingTime = totalMillis;
        saveRemainingTime();

        timer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                saveRemainingTime();
            }

            @Override
            public void onFinish() {
                remainingTime = 0; // Reset the remaining time after completion
                saveRemainingTime();
            }
        };
        timer.start();
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    private void saveRemainingTime() {
        SharedPreferences prefs = getSharedPreferences("countdown_prefs", MODE_PRIVATE);
        prefs.edit().putLong("remaining_time", remainingTime).apply();
    }

    public void loadRemainingTime() {
        SharedPreferences prefs = getSharedPreferences("countdown_prefs", MODE_PRIVATE);
        remainingTime = prefs.getLong("remaining_time", 0);
    }

    public void cancelCountdown() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
