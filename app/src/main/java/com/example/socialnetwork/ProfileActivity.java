package com.example.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String userID;

    private RecyclerView postList;
    private Toolbar mToolbar;

    public ArrayList<Posts> posts;

    private CircleImageView profilePic;
    private TextView username;
    private ImageButton addNewPost;
    private ImageView profileHeader;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    public DatabaseReference postsRef;
    public DatabaseReference ref;
    private String currentUserId;

    public MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        postsRef = ref.child("Posts");


        profilePic = findViewById(R.id.user_profile_pic);
        profileHeader = findViewById(R.id.profile_header);
        username = findViewById(R.id.profile_username);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userID");
        }

        userRef = ref.child("Users").child(userID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("username")) {
                        String userName = dataSnapshot.child("username").getValue().toString();
                        username.setText(userName);
                    }
                    if (dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(profilePic);
                    }
                    if (dataSnapshot.hasChild("team")) {
                        String team = dataSnapshot.child("team").getValue().toString();
                        if (team.equals("Sabiduría")) {
                            profileHeader.setBackgroundResource(R.drawable.team_mystic);
                        } else if (team.equals("Instinto")) {
                            profileHeader.setBackgroundResource(R.drawable.team_instinct);
                        } else if (team.equals("Valor")) {
                            profileHeader.setBackgroundResource(R.drawable.team_valor);
                        } else if (TextUtils.isEmpty(team)) {
                            profileHeader.setBackgroundResource(R.drawable.header);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        mToolbar = findViewById(R.id.profile_page_toolbar);

    }
}
