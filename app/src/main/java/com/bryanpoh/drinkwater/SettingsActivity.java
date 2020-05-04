package com.bryanpoh.drinkwater;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {

    TextView tvUsername, tvRecommend;
    EditText etBodyWeight, etBottleSize, etDrinkSize;
    Button btnSave, btnSignout;
    RelativeLayout editUsername;

    private static final String USER = "Users";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    DatabaseReference reference;
    FirebaseAuth mAuth;

    // Global var for user data in this activity
    String _USERID, _USERNAME, _EMAIL, _BODYWEIGHT, _DRINKSIZE, _BOTTLESIZE;
    String recommendedSize;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Back button
        setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        tvUsername = findViewById(R.id.tvUsername);
        tvRecommend = findViewById(R.id.tvRecommend);
        etBodyWeight = findViewById(R.id.etBodyWeight);
        etDrinkSize = findViewById(R.id.etDefaultDrinkSize);
        etBottleSize = findViewById(R.id.etGoalBottleSize);
        btnSave = findViewById(R.id.btnSaveGoals);
        btnSignout = findViewById(R.id.btnLogout);

        editUsername = findViewById(R.id.editUsername);


        showUserData();

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditProfile.class);
                startActivity(intent);
            }
        });

        etBodyWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String userWeight = etBodyWeight.getText().toString();
                Log.d("tag", "USER WEIGHT : " + userWeight);

                try{
                    int intRecommendedSize = (Integer.parseInt(userWeight) / 30) * 1000;
                    Log.d("tag", "USER INT : " + intRecommendedSize);
                    recommendedSize = Integer.toString(intRecommendedSize);
                    Log.d("tag", "USER STRING : " + intRecommendedSize);
                    String msg = "According to your weight, we recommend that you drink at least " + recommendedSize + " ml daily";
                    tvRecommend.setText(msg);
                } catch (NumberFormatException e){
                    String msg = "Invalid weight";
                    tvRecommend.setText(msg);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(SettingsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra("bottleSize", _BOTTLESIZE);
        intent.putExtra("drinkSize", _DRINKSIZE);

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
                        String email = ds.child("email").getValue(String.class); // Get email
                        String weight = ds.child("weight").getValue(String.class); // Get weight
                        String bottleSize = ds.child("bottleSize").getValue(String.class); // Get
                        String drinkSize = ds.child("drinkSize").getValue(String.class); // Get
                        /*Log.d("tag", "Userid Keyvalue is: " + key);
                        Log.d("tag", "USERNAME FINALLY is: " + username);*/

                        _USERID = key;
                        _USERNAME = username;
                        _EMAIL = email;
                        _BODYWEIGHT = weight;
                        _DRINKSIZE = drinkSize;
                        _BOTTLESIZE = bottleSize;
                        tvUsername.setText(username);
                        etBottleSize.setText(bottleSize);
                        etBodyWeight.setText(weight);
                        etDrinkSize.setText(drinkSize);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tag", "Failed to read value.", error.toException());
            }


        });



//        final String email = intent.getStringExtra("email");
//        Log.d("tag", "email");
//        Log.d("tag", email);
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USER);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void  onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    if(ds.child("email").getValue().equals(email)){
//                        tvUsername.setText(ds.child(auth.getCurrentUser().getUid()).child("username").getValue(String.class));
//                        Log.d("tag", tvUsername.getText().toString());
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    public void updateSettings(){
        String currWeight = etBodyWeight.getText().toString().trim();
        String currBottleSize = etBottleSize.getText().toString().trim();
        String currDrinkSize = etDrinkSize.getText().toString().trim();

        if(TextUtils.isEmpty(currWeight) || TextUtils.isEmpty(currBottleSize) || TextUtils.isEmpty(currDrinkSize)){
            Toast.makeText(this, "Fields cannot be left empty", Toast.LENGTH_SHORT).show();
        }else{
            updateUserGoals(currWeight, currBottleSize, currDrinkSize);
        }
       /* if(isBottleSizeChanged() || isDrinkSizeChanged()){
            Toast.makeText(this, "Goals has been updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nothing to update", Toast.LENGTH_SHORT).show();
        }*/
    }

    private boolean updateUserGoals(String weight, String bottle, String drink){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(_USERID);
        User user = new User(_USERID, _USERNAME, _EMAIL, weight, bottle, drink);
        dbRef.setValue(user);

        Toast.makeText(this, "Updated Goals", Toast.LENGTH_SHORT).show();
        return true;
    }

    /*private boolean isBottleSizeChanged(){
        if(!_BOTTLESIZE.equals(etBottleSize.getText().toString()))
        {
            reference.child(_USERID).child("goal_bottle_size").setValue(etBottleSize.getText().toString());
            _BOTTLESIZE = etBottleSize.getText().toString();
            return true;
        }else{
            return false;
        }
    }

    private boolean isDrinkSizeChanged(){
        if(!_DRINKSIZE.equals(etDrinkSize.getText().toString()))
        {
            reference.child(_USERID).child("default_drink_size").setValue(etDrinkSize.getText().toString());
            _DRINKSIZE = etDrinkSize.getText().toString();
            return true;
        }else{
            return false;
        }
    }*/


}
