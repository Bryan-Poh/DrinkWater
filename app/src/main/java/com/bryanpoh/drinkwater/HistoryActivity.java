package com.bryanpoh.drinkwater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.threeten.bp.LocalDate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marcohc.robotocalendar.RobotoCalendarView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HistoryActivity extends AppCompatActivity implements RobotoCalendarView.RobotoCalendarListener {

    private RobotoCalendarView robotoCalendarView;
    TextView tvDate;

    // Firebase
    private static final String USER = "UserDrinkData";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("UserProgressData");
    DatabaseReference reference;
    FirebaseAuth mAuth;

    // Global var for user's drinking data
    String _USERID, _BODYWEIGHT, _DRINKSIZE, _BOTTLESIZE, _PROGRESS;

    Map<String, ArrayList<UserData>> allUserProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Your DrinkWater History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Gets the calendar from the view
        robotoCalendarView = findViewById(R.id.robotoCalendarPicker);
        tvDate = findViewById(R.id.tvDate);

        getUserData();

        /*clearSelectedDayButton.setOnClickListener(v -> robotoCalendarView.clearSelectedDay());*/
        // Set listener, in this case, the same activity
        robotoCalendarView.setRobotoCalendarListener(this);
        robotoCalendarView.setShortWeekDays(false);
        robotoCalendarView.showDateTitle(true);
        // Set current day
        robotoCalendarView.setDate(new Date());

        // Gettng data from Firebase
        reference = FirebaseDatabase.getInstance().getReference("UserDrinkData");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onDayClick(Date date) {
        for(Entry<String, ArrayList<UserData>> en : allUserProgress.entrySet()){
            for(UserData obj : en.getValue()){

                // Convert Unix Timestamp / Epoch from String to long
                long unixTime = Long.parseLong(obj.getDate().trim());

                // Convert long to Date for calendar markings
                // Multiply 1000 because long stored was in seconds and not milliseconds
                Date currDate = new Date(unixTime * 1000);

                DateFormat formatter = new SimpleDateFormat("dd MM yyyy");
                try {
                    Date dateWithoutTime = formatter.parse(formatter.format(currDate));
                    Date selectedDateWithoutTime = formatter.parse(formatter.format(date));
                    /*Log.d("CURRDATE", dateWithoutTime.toString());
                    Log.d("CURRDATE date", selectedDateWithoutTime.toString());*/
                    // Check if date match
                    String msg = "";
                    if(dateWithoutTime.equals(selectedDateWithoutTime)){
                        msg = "Your Goal Bottle Size: " + obj.getBottleSize() + "ml \n"
                                + "Your Daily Drink Size: " + obj.getDrinkSize() + "ml \n"
                                + "Your Weight: " + obj.getWeight() + "kg \n"
                                + "Your Progress For This Date: " + obj.getProgress() + "ml / " + obj.getBottleSize() + "ml";

                        System.out.println("Date: " + obj.getDate());
                        System.out.println("Progress: " + obj.getProgress());
                    }else{
                        msg = "No water log for this date";
                    }

                    tvDate.setText(msg);
                } catch (ParseException e) {
                    e.printStackTrace();
                }




            }
        }
        //Toast.makeText(this, "Today's date is : " + date, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDayLongClick(Date date) {

    }

    @Override
    public void onRightButtonClick() {

    }

    @Override
    public void onLeftButtonClick() {

    }


    private void getUserData(){
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserUID = currentUser.getUid();

        // Read from the database
        DatabaseReference dbReference;

        // Get reference from nested structure with userID as reference
        dbReference = database.getReference("UserProgressData/" + currentUserUID);

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Log.d("tag", "Current user uid : " + currentUserUID);

                // Initialize new HashMap to store User Progress
                allUserProgress = new HashMap<>();
                ArrayList<UserData> data = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                       /* double lat = ds.child("lat").getValue(Double.class);
                        double long = ds.child("long").getValue(Double.class);
                        Log.d("TAG", lat + " / " + long);
                        players.put(lat, long);*/


                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c);

                        String progressDate = ds.getKey(); // Get
                        String bottleSize = ds.child("bottleSize").getValue(String.class); // Get
                        String drinkSize = ds.child("drinkSize").getValue(String.class); // Get
                        String weight = ds.child("weight").getValue(String.class); // Get
                        String progress = ds.child("progress").getValue(String.class);
                        String completedDate = ds.child("date").getValue(String.class);

                        // Store data into hashmap
                        data.add(new UserData(currentUserUID, bottleSize, drinkSize, weight, progress, completedDate));

                        // Convert Unix Timestamp / Epoch from String to long
                        long unixTime = Long.parseLong(completedDate.trim());

                        // Convert long to Date for calendar markings
                        // Multiply 1000 because long stored was in seconds and not milliseconds
                        Date date = new Date(unixTime * 1000);
                        Log.d("DATE", date.toString());

                        allUserProgress.put(progressDate, data);

                        robotoCalendarView.markCircleImage1(date);


                        Log.d("HISTORY", progressDate);
                        Log.d("HISTORY2", bottleSize);
                        Log.d("HISTORY2", drinkSize);
                        Log.d("HISTORY2", weight);
                        Log.d("HISTORY2", progress);


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

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tag", "Failed to read value.", error.toException());
            }
        });


    }

}
