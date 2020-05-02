package com.bryanpoh.drinkwater;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

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
}
