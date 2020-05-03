package com.bryanpoh.drinkwater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView tvDate, tvIntakeGoal, tvIntakePercent, tvReminder;
    Button drinkBtn;
    ImageButton settingsBtn;
    ProgressBar intakeProgressBar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    FirebaseAuth mAuth;

    // Global var for user data in this activity
    String _USERID, _USERNAME, _EMAIL, _DRINKSIZE, _BOTTLESIZE;

    // How much user drank
    int currentProgress = 0;

    // String value for progress %
    String currProgressPercent;

    // Notification
    NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(MainActivity.this);

        tvDate = findViewById(R.id.tvDate);
        tvReminder = findViewById(R.id.tvTime);
        tvIntakePercent = findViewById(R.id.tvIntakePercent);
        tvIntakeGoal = findViewById(R.id.tvIntakeGoal);
        drinkBtn = findViewById(R.id.btnDrink);
        settingsBtn = findViewById(R.id.btnSettings);
        intakeProgressBar = findViewById(R.id.progress_circular);

        showUserData();

        /*// Notification
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        String notiMsg = "Reminder to stay hydrated! Drink water now!";
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                .setSmallIcon(R.drawable.ic_notificiation_bell)
                .setContentTitle("New Reminder!")
                .setContentText(notiMsg)
                .setContentIntent(pendingIntent) // Set where user goes when tap
                .setAutoCancel(true) // Removes noti after user tap
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

         notificationManager = NotificationCompat.from(MainActivity.this);*/

        //

        String date = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(new Date());

        // Countdown timer for 1 hour
        new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                tvReminder.setText(hms);//set text
            }

            public void onFinish() {
                showNotification();
            }
        }.start();
        //



        tvDate.setText(date);

        // Set progress
        intakeProgressBar.setProgress(currentProgress);
        String str_currentProgress = currentProgress + "ml";
        tvIntakePercent.setText(str_currentProgress);

        drinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Add drink to current that user already drank
                currentProgress += Integer.parseInt(_DRINKSIZE);

                // Set progress bar with current progress
                intakeProgressBar.setProgress(currentProgress);

                String str_currentProgress = currentProgress + "ml";
                tvIntakePercent.setText(str_currentProgress);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showNotification(){
        String notiMsg = "Reminder to stay hydrated! Drink water now!";
//
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

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

    private void showUserData(){
        Log.d("tag", "Inside");

        Intent settingsIntent = getIntent();
        String user_username = settingsIntent.getStringExtra("username");

        Log.d("tag", "Settings UN: " + user_username);

        //tvUsername.setText(user_username);



        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mAuth = FirebaseAuth.getInstance();

                FirebaseUser currentUser = mAuth.getCurrentUser();

                String currentUserUID = currentUser.getUid();
                Log.d("tag", "Current user uid : " + currentUserUID);


                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (currentUserUID.equals(ds.getKey())) {
                        String key = ds.getKey(); // Get USERID
                        String username = ds.child("username").getValue(String.class); // Get username
                        String email = ds.child("email").getValue(String.class); // Get username
                        String bottleSize = ds.child("bottleSize").getValue(String.class); // Get
                        String drinkSize = ds.child("drinkSize").getValue(String.class); // Get
                        /*Log.d("tag", "Userid Keyvalue is: " + key);
                        Log.d("tag", "USERNAME FINALLY is: " + username);*/

                        _USERID = key;
                        _USERNAME = username;
                        _EMAIL = email;
                        _DRINKSIZE = drinkSize;
                        _BOTTLESIZE = bottleSize;
                        tvIntakeGoal.setText(_BOTTLESIZE + "ml");
                    }
                }

                // Set max value of progress bar: bottleSize
                intakeProgressBar.setMax(Integer.parseInt(_BOTTLESIZE));
                Log.d("tag", "After bottle" + Integer.parseInt(_BOTTLESIZE));

                // Calculate each drink percent out of bottesize
                //currentProgress = Integer.parseInt(_DRINKSIZE) / Integer.parseInt(_BOTTLESIZE);
                Log.d("tag", "CURRENT PROGRESS INSIDE DS: " + currentProgress);


                //Log.d("tag", "bottle size: " + Integer.parseInt(_BOTTLESIZE));

                //currProgressPercent = (currentProgress * 100) + "%";
                //tvIntakePercent.setText(currProgressPercent);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tag", "Failed to read value.", error.toException());
            }
        });
    }
}
