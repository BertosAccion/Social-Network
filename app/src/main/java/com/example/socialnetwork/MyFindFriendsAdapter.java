package com.example.socialnetwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFindFriendsAdapter extends RecyclerView.Adapter<MyFindFriendsAdapter.MyViewHolder> {
    Context ctx;
    ArrayList<FindFriends> result;
    FirebaseAuth mAuth;
    String currentUserId;
    DatabaseReference ref;

    public MyFindFriendsAdapter(Context ctx, ArrayList<FindFriends> result) {
        this.ctx = ctx;
        this.result = result;
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.all_users_display_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        myViewHolder.username.setText(result.get(i).getUsername());
        myViewHolder.level.setText("Nivel: " + result.get(i).getLevel());
        myViewHolder.team.setText("Equipo: " + result.get(i).getTeam());
        Picasso.get().load(result.get(i).getProfileimage()).placeholder(R.drawable.profile).into(myViewHolder.profileimage);
        myViewHolder.friendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ctx, ProfileActivity.class);
                profileIntent.putExtra("userID", result.get(i).getUid());
                ctx.startActivity(profileIntent);
            }
        });
        myViewHolder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable)myViewHolder.addFriend.getDrawable()).getBitmap();
                Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.add_friend);
                if (bitmap.sameAs(icon)){
                    DatabaseReference friendRef = ref.child(currentUserId);
                    HashMap likeMap = new HashMap();
                    likeMap.put(result.get(i).getUid(), true);
                    friendRef.updateChildren(likeMap);
                } else {
                    DatabaseReference unfollowRef = ref.child(currentUserId).child(result.get(i).getUid());
                    confirmUnfollow(unfollowRef);
                }
            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentUserId)){
                    if (dataSnapshot.child(currentUserId).hasChild(result.get(i).getUid())){
                        myViewHolder.addFriend.setImageResource(R.drawable.friend);
                    }else{
                        myViewHolder.addFriend.setImageResource(R.drawable.add_friend);
                    }
                }else{
                    myViewHolder.addFriend.setImageResource(R.drawable.add_friend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public void clear() {
        int size = result.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                result.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    public void confirmUnfollow(final DatabaseReference unfollowRef){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("¿Dejar de seguir?").setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color='#000000'>Sí</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unfollowRef.removeValue();
                    }
                })
                .setNegativeButton(Html.fromHtml("<font color='#000000'>No0</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, level, team;
        ImageView addFriend;
        CircleImageView profileimage;
        LinearLayout friendLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.all_users_username);
            level = itemView.findViewById(R.id.all_users_lvl);
            team = itemView.findViewById(R.id.all_users_team);
            profileimage = itemView.findViewById(R.id.all_users_profile_image);
            friendLayout = itemView.findViewById(R.id.all_users_background);
            addFriend = itemView.findViewById(R.id.all_users_add_friend);
        }
    }

}
