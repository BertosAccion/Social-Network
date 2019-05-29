package com.example.socialnetwork;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {
    private CircleImageView settingsProfileImage;
    private EditText settingsUsername, settingsFullname, settingsDob, settingsCountry, settingsGender, settingsTeam, settingsLevel;
    private Button updateButton, resetButton;

    private Toolbar mToolbar;

    private String uid, username, profileimage, fullname, dob, country, gender, team, level, downloadUrl;
    private Boolean bBoolean = false, aBoolean = false;
    private final static int gallery_pic = 1;

    private Uri resultUri;
    private DatabaseReference userRef;
    private StorageReference userProfilePicRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uid = extras.getString("userID");
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            userProfilePicRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        }

        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Configuración");

        updateButton = findViewById(R.id.settings_update_button);
        resetButton = findViewById(R.id.settings_reset_button);

        settingsProfileImage = findViewById(R.id.settings_profile_image);
        settingsUsername = findViewById(R.id.settings_username);
        settingsFullname = findViewById(R.id.settings_fullname);
        settingsDob = findViewById(R.id.settings_dob);
        settingsCountry = findViewById(R.id.settings_country);
        settingsGender = findViewById(R.id.settings_gender);
        settingsTeam = findViewById(R.id.settings_team);
        settingsLevel = findViewById(R.id.settings_level);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aBoolean) {
                    updateUserInfo(aBoolean);
                } else {
                    StorageReference filePath = userProfilePicRef.child(uid + ".jpg");
                    filePath.delete();
                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                userProfilePicRef.child(uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadUrl = uri.toString();
                                        aBoolean = true;
                                        updateUserInfo(aBoolean );
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFieldData();
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.child("username").getValue().toString();
                    profileimage = dataSnapshot.child("profileimage").getValue().toString();
                    fullname = dataSnapshot.child("fullname").getValue().toString();
                    dob = dataSnapshot.child("dob").getValue().toString();
                    country = dataSnapshot.child("country").getValue().toString();
                    team = dataSnapshot.child("team").getValue().toString();
                    level = dataSnapshot.child("level").getValue().toString();

                    resetFieldData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        settingsProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallery_pic);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_pic && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.get().load(resultUri).into(settingsProfileImage);
                aBoolean = true;
            }
        }
    }

    public void updateUserInfo(Boolean changeProfileImage) {
        HashMap updateMap = new HashMap();
        updateMap.put("country", settingsCountry.getText().toString());
        updateMap.put("dob", settingsDob.getText().toString());
        updateMap.put("fullname", settingsFullname.getText().toString());
        updateMap.put("gender", settingsGender.getText().toString());
        updateMap.put("level", settingsLevel.getText().toString());
        updateMap.put("team", settingsTeam.getText().toString());
        updateMap.put("username", settingsUsername.getText().toString());
        if (changeProfileImage) {
            updateMap.put("profileimage", downloadUrl);
        }

        userRef.updateChildren(updateMap);
    }

    public void resetFieldData() {
        settingsUsername.setText(username);
        settingsFullname.setText(fullname);
        Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(settingsProfileImage);
        settingsDob.setText(dob);
        settingsLevel.setText(level);
        settingsTeam.setText(team);
        settingsCountry.setText(country);
        settingsGender.setText(gender);
    }
}
