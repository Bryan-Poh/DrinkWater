package com.bryanpoh.drinkwater;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.TimeUnit;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.bryanpoh.drinkwater";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;
    NotificationManagerCompat notificationManager;

    boolean isTimerEnd = false;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Starting timer...");

        cdt = new CountDownTimer(360000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bi.removeExtra("remind");

                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                bi.putExtra("countdown", hms);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.d("TAG", "END END END END ");
                bi.putExtra("remind", true);
                sendBroadcast(bi);
                isTimerEnd = true;

                if(isTimerEnd = false){
                    cdt.start();
                }else{
                    cdt.cancel();
                    cdt.start();
                }
            }
        };

        cdt.start();
    }
    public void showNotification(){
        String notiMsg = "Reminder to stay hydrated! Drink water now!";
//
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_REMINDER_ID)
                .setSmallIcon(R.drawable.ic_notificiation_bell)
                .setContentTitle("Alert from DrinkWater!")
                .setContentText(notiMsg)
                .setContentIntent(pendingIntent) // Set where user goes when tap
                .setAutoCancel(true) // Removes noti after user tap
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }



    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}