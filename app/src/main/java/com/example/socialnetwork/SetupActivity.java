package com.example.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName, countryName;
    private Button saveInformationButton;
    private CircleImageView profileImage;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    String currentUserId;
    final static int gallery_pic = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        userName = findViewById(R.id.setup_username);
        fullName = findViewById(R.id.setup_fullname);
        countryName = findViewById(R.id.setup_country);
        saveInformationButton = findViewById(R.id.save_setup_info);
        profileImage = findViewById(R.id.setup_user_profile_pic);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);


        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
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

        if (requestCode==gallery_pic && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();

        }
    }

    private void SaveAccountSetupInformation() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String country = countryName.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Por favor, escriba su nombre", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Por favor, escriba su apellido", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(country)){
            Toast.makeText(this, "Por favor, escriba su país de nacimiento", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Guardando información...");
            loadingBar.setMessage("por favor, espera unos segundos");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", country);
            userMap.put("status", "default status value ^^");
            userMap.put("gender", "default gender value ^^");
            userMap.put("dob", "default date of birth value ^^");
            userMap.put("relationshipstatus", "default relationship status value ^^");
            usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Tu cuenta ha sido creada correctamente", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    } else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
