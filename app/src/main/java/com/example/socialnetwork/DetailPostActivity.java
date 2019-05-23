package com.example.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPostActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private Toolbar mToolbar;

    private CircleImageView detailPostProfilePic;
    private TextView detailPostUsername, detailPostDat, detailPostTime, detailPostDescription;
    private ImageView detailPostImage;

    private Query query;

    private FirebaseAuth mAuth;
    private DatabaseReference  ref;
    private String userID, postDate, postTime, clickedPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        mToolbar = findViewById(R.id.detail_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");

        detailPostProfilePic = findViewById(R.id.detail_post_profile_pic);
        detailPostUsername = findViewById(R.id.detail_post_username);
        detailPostDat = findViewById(R.id.detail_post_date);
        detailPostTime = findViewById(R.id.detail_post_time);
        detailPostDescription = findViewById(R.id.detail_post_description);
        detailPostImage = findViewById(R.id.detail_post_postimage);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userID");
            postDate = extras.getString("date");
            postTime = extras.getString("time");
            clickedPost = userID + "_" + postDate + postTime;
        }

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(clickedPost);
        query = ref.orderByChild("Posts").equalTo(clickedPost);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Posts post = dataSnapshot.getValue(Posts.class);

                Picasso.get().load(post.getPostimage()).into(detailPostImage);
                Picasso.get().load(post.getProfileimage()).placeholder(R.drawable.profile).into(detailPostProfilePic);
                detailPostUsername.setText(post.getUsername());
                detailPostDat.setText(post.getDate());
                detailPostTime.setText(post.getTime());
                detailPostDescription.setText(post.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_post:
                Toast.makeText(this, "Edit post", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.delete_post:
                Toast.makeText(this, "Delete post", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }
    }
}
