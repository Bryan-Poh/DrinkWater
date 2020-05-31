package com.bryanpoh.drinkwater;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity{

    TextView tvRecommend;
    EditText etBodyWeight, etBottleSize, etDrinkSize, etUsername;
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
        setContentView(R.layout.activity_editprofile);

        editUsername = findViewById(R.id.editUsername);

        // Back button
        setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        tvRecommend = findViewById(R.id.tvRecommend);
        etUsername = findViewById(R.id.etEditName);
        etBodyWeight = findViewById(R.id.etBodyWeight);
        etDrinkSize = findViewById(R.id.etDefaultDrinkSize);
        etBottleSize = findViewById(R.id.etGoalBottleSize);
        btnSave = findViewById(R.id.btnSaveGoals);
        btnSignout = findViewById(R.id.btnLogout);

        editUsername = findViewById(R.id.editUsername);


        showUserData();

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

        Intent intent = new Intent(EditProfile.this, MainActivity.class);
        intent.putExtra("bottleSize", _BOTTLESIZE);
        intent.putExtra("drinkSize", _DRINKSIZE);

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
                        etUsername.setText(username);
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
    }

    public void updateSettings(){
        String currUsername = etUsername.getText().toString().trim();
        String currWeight = etBodyWeight.getText().toString().trim();
        String currBottleSize = etBottleSize.getText().toString().trim();
        String currDrinkSize = etDrinkSize.getText().toString().trim();

        if(TextUtils.isEmpty(currUsername) || TextUtils.isEmpty(currWeight) || TextUtils.isEmpty(currBottleSize) || TextUtils.isEmpty(currDrinkSize)){
            Toast.makeText(this, "Fields cannot be left empty", Toast.LENGTH_SHORT).show();
        }else{
            updateUserGoals(currUsername, currWeight, currBottleSize, currDrinkSize);
        }
    }

    private boolean updateUserGoals(String username, String weight, String bottle, String drink){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(_USERID);
        User user = new User(_USERID, username, _EMAIL, weight, bottle, drink);
        dbRef.setValue(user);

        Toast.makeText(this, "Updated Settings", Toast.LENGTH_SHORT).show();
        return true;
    }

}
