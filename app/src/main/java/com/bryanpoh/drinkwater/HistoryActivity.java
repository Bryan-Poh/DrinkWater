package com.bryanpoh.drinkwater;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.threeten.bp.LocalDate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.marcohc.robotocalendar.RobotoCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HistoryActivity extends AppCompatActivity implements RobotoCalendarView.RobotoCalendarListener {

    private RobotoCalendarView robotoCalendarView;
    TextView tvDate;

    // Firebase
    private static final String USER = "UserDrinkData";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("UserDrinkData");
    DatabaseReference reference;
    FirebaseAuth mAuth;

    // Global var for user's drinking data
    String _USERID, _BODYWEIGHT, _DRINKSIZE, _BOTTLESIZE, _PROGRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Your DrinkWater History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Gets the calendar from the view
        robotoCalendarView = findViewById(R.id.robotoCalendarPicker);
        Button markDayButton = findViewById(R.id.markDayButton);
        tvDate = findViewById(R.id.tvDate);

        markDayButton.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            Random random = new Random(System.currentTimeMillis());
            int style = random.nextInt(2);
            int daySelected = random.nextInt(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, daySelected);

            switch (style) {
                case 0:
                    // Value of calender.getTime() is Wed Jun 17 01:01:15 GMT+08:00 2020
                    robotoCalendarView.markCircleImage1(calendar.getTime());
                    Log.d("CALENDAR", calendar.getTime().toString());
                    break;
                case 1:
                    robotoCalendarView.markCircleImage2(calendar.getTime());
                    break;
                default:
                    break;
            }
        });

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
        tvDate.setText("Date: " + date);
        //Toast.makeText(this, "Today's date is : " + date, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDayLongClick(Date date) {
        Toast.makeText(this, "onDayLongClick: " + date, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightButtonClick() {
        Toast.makeText(this, "onRightButtonClick!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLeftButtonClick() {
        Toast.makeText(this, "onLeftButtonClick!", Toast.LENGTH_SHORT).show();
    }

}
