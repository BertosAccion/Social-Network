package com.example.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, postsRef;
    private StorageReference postImageReference;
    private ProgressDialog loadingBar;

    private Toolbar mToolbar;
    private ImageView chosenImage;
    private ImageButton selectPostImage;
    private Button updatePostButton;
    private EditText textPost;
    private String postDescription;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;

    private Uri imageUri;

    private static final int gallery_pic = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        postImageReference = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        chosenImage = findViewById(R.id.chosen_image);
        chosenImage.setVisibility(View.GONE);

        textPost = findViewById(R.id.text_post);
        updatePostButton = findViewById(R.id.send_post_button);
        selectPostImage = findViewById(R.id.add_photo_button);

        mToolbar = findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Nuevo post");

        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallery_pic);
            }
        });

        updatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar date = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                saveCurrentDate = currentDate.format(date.getTime());

                Date time = Calendar.getInstance().getTime();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                saveCurrentTime = currentTime.format(time.getTime());

                postRandomName = saveCurrentDate + saveCurrentTime;

                validatePost();
            }
        });

    }

    private void validatePost() {
        postDescription = textPost.getText().toString();
        if (imageUri != null) {
            saveImgToStorage();
        } else {
            savingToDatabase();
        }
        if (TextUtils.isEmpty(postDescription) && imageUri == null) {
            Toast.makeText(this, "Introduce texto o imagen para el post", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Posteando...");
            loadingBar.setMessage("por favor, espera unos segundos");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
        }


    }

    private void saveImgToStorage() {
        final StorageReference filePath = postImageReference.child("Post Images").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    //downloadUrl = filePath.getDownloadUrl().toString()
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            savingToDatabase();
                        }
                    });
                }
            }
        });
    }


    private void savingToDatabase() {
        usersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String timeZone = Calendar.getInstance().getTimeZone().getDisplayName();
                    HashMap postMap = new HashMap();
                    postMap.put("uid", current_user_id);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("timezone", timeZone);
                    if (!TextUtils.isEmpty(postDescription)) {
                        postMap.put("description", postDescription);
                    }
                    if (downloadUrl != null) {
                        postMap.put("postimage", downloadUrl);
                    }
                    postMap.put("username", username);
                    postMap.put("profileimage", profileImage);

                    postsRef.child(current_user_id + "_" + postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PostActivity.this, "Publicado correctamente", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(PostActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_pic && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            chosenImage.setVisibility(View.INVISIBLE);
            chosenImage.setImageURI(imageUri);
            chosenImage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
