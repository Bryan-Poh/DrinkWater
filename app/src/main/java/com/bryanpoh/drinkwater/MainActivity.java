package com.bryanpoh.drinkwater;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    TextView tvDate, tvIntakeGoal, tvIntakePercent;
    Button drinkBtn;
    ImageButton settingsBtn;
    ProgressBar intakeProgressBar;

    private int currentProgress = 0;
    private String currProgressPercent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    FirebaseAuth mAuth;

    // Global var for user data in this activity
    String _USERID, _USERNAME, _EMAIL, _DRINKSIZE, _BOTTLESIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        tvDate = findViewById(R.id.tvDate);
        tvIntakePercent = findViewById(R.id.tvIntakePercent);
        tvIntakeGoal = findViewById(R.id.tvIntakeGoal);
        drinkBtn = findViewById(R.id.btnDrink);
        settingsBtn = findViewById(R.id.btnSettings);
        intakeProgressBar = findViewById(R.id.progress_circular);

        showUserData();

        String date = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(new Date());


        tvDate.setText(date);
        intakeProgressBar.setMax(100);
        intakeProgressBar.setProgress(currentProgress);
        currProgressPercent = currentProgress + "%";
        tvIntakePercent.setText(currProgressPercent);



        drinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentProgress += 25;
                intakeProgressBar.setProgress(currentProgress);
                currProgressPercent = currentProgress + "%";
                if(currentProgress < 100)
                {
                    tvIntakePercent.setText(currProgressPercent);
                }
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
                        tvIntakeGoal.setText(_BOTTLESIZE);
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
}
