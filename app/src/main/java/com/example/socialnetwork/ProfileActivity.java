package com.example.socialnetwork;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String userID;

    private RecyclerView postList;
    private Toolbar mToolbar;

    private ArrayList<Posts> posts;

    private CircleImageView profilePic;
    private TextView username;
    private ImageButton addNewPost;
    private ImageView profileHeader;

    private DatabaseReference userRef, ref, postsRef;
    private Query query;
    private String currentUserId;

    private MyPostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ref = FirebaseDatabase.getInstance().getReference();
        postsRef = ref.child("Posts");

        profilePic = findViewById(R.id.user_profile_pic);
        profileHeader = findViewById(R.id.profile_header);
        username = findViewById(R.id.profile_username);

        postList = findViewById(R.id.profile_postsList);
        posts = new ArrayList<>();
        postList.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        postList.setLayoutManager(linearLayoutManager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userID");
            query = postsRef.orderByChild("uid").equalTo(userID);
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

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Posts post = dataSnapshot1.getValue(Posts.class);
                    posts.add(post);
                }

                ArrayList<Posts> arrangedPosts = arrangePosts(posts);
                adapter = new MyPostsAdapter(ProfileActivity.this, arrangedPosts);
                postList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Oops... algo ha ido mal", Toast.LENGTH_SHORT).show();
            }
        });

        mToolbar = findViewById(R.id.profile_page_toolbar);

    }

    private ArrayList<Posts> arrangePosts(@NotNull ArrayList<Posts> posts) {
        ArrayList<String> arrangeDateTime = new ArrayList<>();
        for (Posts post : posts) {
            String combo = post.getDate() + " " + post.getTime();
            arrangeDateTime.add(combo);
        }

        Collections.sort(arrangeDateTime);

        ArrayList<Posts> arrangedPosts = new ArrayList<>();
        for (String dateTime : arrangeDateTime) {
            String[] aux = dateTime.split(" ");
            for (Posts post : posts) {
                if (aux[0].equals(post.getDate()) && aux[1].equals(post.getTime())) {
                    arrangedPosts.add(post);
                }
            }
        }

        return arrangedPosts;
    }
}
