package com.example.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPostActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private CircleImageView detailPostProfilePic;
    private TextView detailPostUsername, detailPostDat, detailPostTime, detailPostDescription;
    private ImageView detailPostImage;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    public DatabaseReference postsRef;
    private String currentUserId;
    public MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
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
