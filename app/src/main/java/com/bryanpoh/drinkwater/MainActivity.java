package com.bryanpoh.drinkwater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    TextView tvDate, tvIntakeGoal, tvIntakePercent, tvReminder;
    Button drinkBtn;
    ImageButton settingsBtn, historyBtn;
    ProgressBar intakeProgressBar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    FirebaseAuth mAuth;

    // Global var for user data in this activity
    String _USERID, _USERNAME, _EMAIL, _DRINKSIZE, _BOTTLESIZE, _WEIGHT;
    int _PROGRESS;

    int currentProgress;

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
        historyBtn = findViewById(R.id.btnHistory);
        intakeProgressBar = findViewById(R.id.progress_circular);

        showUserData();

        String date = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(new Date());

        startService(new Intent(this, BroadcastService.class));

        tvDate.setText(date);

        // Set progress
        /*intakeProgressBar.setProgress(currentProgress);
        String str_currentProgress = currentProgress + "ml";
        tvIntakePercent.setText(str_currentProgress);*/

        drinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Add drink to current that user already drank
                currentProgress += Integer.parseInt(_DRINKSIZE);
                Log.d("PROGRESS DRINK", Integer.toString(currentProgress));
                // Set progress bar with current progress
                intakeProgressBar.setProgress(currentProgress);

                String str_currentProgress = currentProgress + "ml";
                tvIntakePercent.setText(str_currentProgress);

                _PROGRESS = currentProgress;

                // If goal already met, show congratulation alert and warning so that user does not over drink
                if(currentProgress >= Integer.parseInt(_BOTTLESIZE)){
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("You did it!")
                            .setContentText("Congrats, you met your daily water goal! \n Any extra drinks will be a bonus, but remember to not over drink!")
                            .setCustomImage(R.drawable.partycone)
                            .setConfirmText("Ok, I got it!")
                            .show();
                }

                saveUserProgress();
                Toast.makeText(MainActivity.this, "Gulp gulp", Toast.LENGTH_SHORT).show();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
//                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String time = intent.getStringExtra("countdown");
                tvReminder.setText(time);
            }

            // Expensive because refreshes alot
            boolean isRemind = intent.getBooleanExtra("remind", false);
            Log.d("tag", "remind value " + isRemind);
            if(isRemind) {
                showNotification();
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    public void showNotification(){
        String notiMsg = "Reminder to stay hydrated! Drink water now!";

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
                        String weight = ds.child("weight").getValue(String.class); // Get

                        _USERID = key;
                        _USERNAME = username;
                        _EMAIL = email;
                        _DRINKSIZE = drinkSize;
                        _BOTTLESIZE = bottleSize;
                        _WEIGHT = weight;
                        tvIntakeGoal.setText(_BOTTLESIZE + "ml");
                    }
                }

                // Set max value of progress bar: bottleSize
                intakeProgressBar.setMax(Integer.parseInt(_BOTTLESIZE));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tag", "Failed to read value.", error.toException());
            }
        });

        // Read from the database
        DatabaseReference dbReference;
//        dbReference = FirebaseDatabase.getInstance().getReference().child("UserProgressData").child(_USERID);
        dbReference = database.getReference("UserProgressData");
        dbReference.addValueEventListener(new ValueEventListener() {
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
                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c);

                        String progress = ds.child(formattedDate).child("progress").getValue(String.class); // Get

                        // Check if database has child that is equals to current date
                        if(ds.hasChild(formattedDate)){
                            if(progress == null) {
                                _PROGRESS = 0;
                            }else{
                                _PROGRESS = Integer.parseInt(progress);
                                Log.d("PROGRESS2", Integer.toString(_PROGRESS));
                            }

                            intakeProgressBar.setProgress(_PROGRESS);
                            String str_currentProgress = _PROGRESS + "ml";
                            tvIntakePercent.setText(str_currentProgress);

                            currentProgress = _PROGRESS;
                            Log.d("CURRENT PROGRESS2", Integer.toString(currentProgress));
                        }

                        /*Date c = Calendar.getInstance().getTime();

                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c);

                        String progress = ds.child(formattedDate).child("progress").getValue(String.class); // Get
                        if(progress == null) {
                            _PROGRESS = 0;
                        }else{
                            Log.d("PROGRESS", progress);
                            _PROGRESS = Integer.parseInt(progress);
                            Log.d("PROGRESS2", Integer.toString(_PROGRESS));
                        }

                        intakeProgressBar.setProgress(_PROGRESS);
                        String str_currentProgress = _PROGRESS + "ml";
                        tvIntakePercent.setText(str_currentProgress);

                        currentProgress = _PROGRESS;
                        Log.d("CURRENT PROGRESS2", Integer.toString(currentProgress));*/
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tag", "Failed to read value.", error.toException());
            }
        });
    }

    private void saveUserProgress(){
        DatabaseReference dbReference;
        dbReference = FirebaseDatabase.getInstance().getReference().child("UserProgressData").child(_USERID);

        // Set the user data
        UserData userData = new UserData();
        userData.setId(_USERID);
        userData.setWeight(_WEIGHT);
        userData.setBottleSize(_BOTTLESIZE);
        userData.setDrinkSize(_DRINKSIZE);
        userData.setProgress(Integer.toString(_PROGRESS));
        // Get date in epoch unix timestamp
        long unixTime = System.currentTimeMillis() / 1000L;
        userData.setDate(Long.toString(unixTime));

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        // Push the data to database
        dbReference.child(formattedDate).setValue(userData);
    }
}
