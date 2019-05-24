package com.example.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.MyViewHolder> {

    Context ctx;
    ArrayList<Posts> posts;
    FirebaseAuth mAuth;
    DatabaseReference ref, likesRef;
    String currentUserId;
    boolean liking;

    public MyPostsAdapter(Context ctx, ArrayList<Posts> posts) {
        this.ctx = ctx;
        this.posts = posts;
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Likes");
        liking = true;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, date, time, description, amountLikes, amountReplies;
        ImageView postimage;
        CircleImageView profileimage;
        ImageView like, reply;
        LinearLayout likeLayout, replyLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.post_username);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_description);
            postimage = itemView.findViewById(R.id.post_image);
            profileimage = itemView.findViewById(R.id.post_profile_image);
            like = itemView.findViewById(R.id.like_button);
            reply = itemView.findViewById(R.id.comment_button);
            amountLikes = itemView.findViewById(R.id.amount_of_likes);
            amountReplies = itemView.findViewById(R.id.amount_of_replies);
            likeLayout = itemView.findViewById(R.id.action_like);
            replyLayout = itemView.findViewById(R.id.action_comment);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder mwh = new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.all_posts_layout, viewGroup, false));
        return mwh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        myViewHolder.username.setText(posts.get(i).getUsername());
        myViewHolder.date.setText(posts.get(i).getDate());
        myViewHolder.time.setText(posts.get(i).getTime());
        myViewHolder.description.setText(posts.get(i).getDescription());
        Picasso.get().load(posts.get(i).getPostimage()).into(myViewHolder.postimage);
        Picasso.get().load(posts.get(i).getProfileimage()).into(myViewHolder.profileimage);
        final String postId = posts.get(i).getUid() + "_" + posts.get(i).getDate() + posts.get(i).getTime();
        if (!(this.ctx instanceof ProfileActivity)) {
            myViewHolder.profileimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profileIntent = new Intent(ctx, ProfileActivity.class);
                    profileIntent.putExtra("userID", posts.get(i).getUid());
                    ctx.startActivity(profileIntent);
                }
            });
        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntente = new Intent(ctx, DetailPostActivity.class);
                detailIntente.putExtra("userID", posts.get(i).getUid());
                detailIntente.putExtra("date", posts.get(i).getDate());
                detailIntente.putExtra("time", posts.get(i).getTime());
                ctx.startActivity(detailIntente);
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(postId)){
                    myViewHolder.amountLikes.setText(String.valueOf(dataSnapshot.child(postId).getChildrenCount()));
                    if (dataSnapshot.child(postId).hasChild(currentUserId)){
                        myViewHolder.like.setImageResource(R.drawable.like);
                    } else {
                        myViewHolder.like.setImageResource(R.drawable.dislike);
                    }
                } else {
                    myViewHolder.amountLikes.setText("");
                    myViewHolder.like.setImageResource(R.drawable.dislike);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myViewHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myViewHolder.like.getDrawable().getConstantState().equals(R.drawable.like)){
                    System.out.println("YAAAAAAAAAAAAAAAAAA");
                }
                if (liking){
                    likesRef = ref.child(postId);
                    HashMap likeMap = new HashMap();
                    likeMap.put(currentUserId, true);
                    likesRef.updateChildren(likeMap);
                    liking = false;
                } else {
                    ref.child(postId).child(currentUserId).removeValue();
                    liking = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
