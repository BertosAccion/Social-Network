package com.example.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private ArrayList<Posts> posts;
    private ArrayList<String> friends;

    private CircleImageView navProfilePic, postProfilePic;
    private TextView navUsername;
    private ImageButton addNewPost;
    private FloatingActionButton fab;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, postsRef, friendsRef;
    private String currentUserId;
    public MyPostsAdapter adapter;

    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        currentUserId = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);

        fab = findViewById(R.id.fab);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Inicio");

        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfilePic = navView.findViewById(R.id.nav_profile_pic);
        navUsername = navView.findViewById(R.id.nav_user_full_name);
        rl = navView.findViewById(R.id.layout_header);

        friends = new ArrayList<>();
        posts = new ArrayList<>();
        postList = findViewById(R.id.all_users_post_list);
        postProfilePic = postList.findViewById(R.id.post_profile_image);
        postList.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        postList.setLayoutManager(linearLayoutManager);

        addNewPost = findViewById(R.id.add_new_post_button);

        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    friends.add(dataSnapshot1.getKey());
                }
                System.out.println(friends.get(0));
                for (String friend : friends){
                    Query query = postsRef.orderByChild("uid").equalTo(friend);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            posts.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Posts post = dataSnapshot1.getValue(Posts.class);
                                posts.add(post);
                            }
                            ArrayList<Posts> arrangedPosts = arrangePosts(posts);
                            adapter = new MyPostsAdapter(MainActivity.this, arrangedPosts);
                            postList.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Posts post = dataSnapshot1.getValue(Posts.class);
                    posts.add(post);
                }
                ArrayList<Posts> arrangedPosts = arrangePosts(posts);
                adapter = new MyPostsAdapter(MainActivity.this, arrangedPosts);
                postList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Oops... algo ha ido mal", Toast.LENGTH_SHORT).show();
            }
        });*/

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("username")) {
                        String userName = dataSnapshot.child("username").getValue().toString();
                        navUsername.setText(userName);
                    }
                    if (dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(navProfilePic);
                    }
                    if (dataSnapshot.hasChild("team")) {
                        String team = dataSnapshot.child("team").getValue().toString();
                        if (team.equals("Sabiduría")) {
                            rl.setBackgroundResource(R.drawable.team_mystic);
                        } else if (team.equals("Instinto")) {
                            rl.setBackgroundResource(R.drawable.team_instinct);
                        } else if (team.equals("Valor")) {
                            rl.setBackgroundResource(R.drawable.team_valor);
                        } else if (TextUtils.isEmpty(team)) {
                            rl.setBackgroundResource(R.drawable.header);
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });
    }


    private ArrayList<Posts> arrangePosts(@NotNull ArrayList<Posts> posts) {
        ArrayList<String> arrangeDateTime = new ArrayList<>();
        System.out.println(friends);
        for (Posts post : posts) {
            String combo = post.getDate() + " " + post.getTime();
            arrangeDateTime.add(combo);
        }
        Collections.sort(arrangeDateTime);
        System.out.println(arrangeDateTime);

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

    private void sendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLogin();
        } else {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //setupIntent.putExtra("layout", "1");
        startActivity(setupIntent);
        finish();

    }

    private void SendUserToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_post:
                sendUserToPostActivity();
                break;
            case R.id.nav_profile:
                sendUserToProfileActivity(currentUserId);
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this, "Amigos", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find_friends:
                sendUserToFindFriendsActivity();
                break;

            case R.id.nav_messages:
                Toast.makeText(this, "Mensajes", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                sendUserToSettingsActivity(currentUserId);
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLogin();
                break;
        }


    }

    private void sendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }

    public void sendUserToProfileActivity(String userID) {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        profileIntent.putExtra("userID", userID);
        startActivity(profileIntent);
    }

    public void sendUserToSettingsActivity(String userID) {
        Intent settingsIntent = new Intent(MainActivity.this, AccountSettingsActivity.class);
        settingsIntent.putExtra("userID", userID);
        startActivity(settingsIntent);
    }
}
