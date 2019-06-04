package com.example.socialnetwork;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ArrayList<FindFriends> friends;

    private ImageButton searchButton;
    private EditText searchText;
    private RecyclerView searchList;

    private DatabaseReference usersRef;
    private Query query;

    private MyFindFriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        friends = new ArrayList<>();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = findViewById(R.id.find_friends_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Encontrar anmigos");

        searchList = findViewById(R.id.find_friends_list);
        searchList.setHasFixedSize(true);
        searchList.setLayoutManager(new LinearLayoutManager(this));

        searchButton = findViewById(R.id.find_friends_button);
        searchText = findViewById(R.id.find_friends_text);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchUsername = searchText.getText().toString();
                if (TextUtils.isEmpty(searchUsername)) {
                    Toast.makeText(FindFriendsActivity.this, "Introduce el nombre de usuario a buscar", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = new MyFindFriendsAdapter(FindFriendsActivity.this, friends);
                    adapter.clear();
                    findPeopleAndFriends(searchUsername);
                }
            }
        });
    }

    private void findPeopleAndFriends(String searchUsername) {
        query = usersRef.orderByChild("username").startAt(searchUsername).endAt(searchUsername + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    FindFriends friend = dataSnapshot1.getValue(FindFriends.class);
                    friend.setUid(dataSnapshot1.getKey());
                    System.out.println(friend.getUid());
                    friends.add(friend);
                }
                searchList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
