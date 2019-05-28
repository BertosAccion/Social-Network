package com.example.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {
    CircleImageView settingsProfileImage;
    EditText settingsUsername, settingsFullname, settingsDob, settingsCountry, settingsGender, settingsTeam, settingsLevel;
    String uid;

    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uid = extras.getString("userID");
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        }

        settingsProfileImage = findViewById(R.id.settings_profile_image);
        settingsUsername = findViewById(R.id.settings_username);
        settingsFullname = findViewById(R.id.setup_fullname);
        settingsDob = findViewById(R.id.settings_dob);
        settingsCountry = findViewById(R.id.settings_country);
        settingsGender = findViewById(R.id.settings_gender);
        settingsTeam = findViewById(R.id.settings_team);
        settingsLevel = findViewById(R.id.settings_level);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
