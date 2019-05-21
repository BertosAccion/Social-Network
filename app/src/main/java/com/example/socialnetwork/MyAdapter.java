package com.example.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    Context ctx;
    ArrayList<Posts> posts;

    public MyAdapter(Context ctx, ArrayList<Posts> posts){
        this.ctx = ctx;
        this.posts = posts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.all_posts_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.username.setText(posts.get(i).getUsername());
        myViewHolder.date.setText(posts.get(i).getDate());
        myViewHolder.time.setText(posts.get(i).getTime());
        myViewHolder.description.setText(posts.get(i).getDescription());
        Picasso.get().load(posts.get(i).getPostimage()).into(myViewHolder.postimage);
        Picasso.get().load(posts.get(i).getProfileimage()).into(myViewHolder.profileimage);
        if (!(this.ctx instanceof ProfileActivity)){
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView username, date, time, description;
        ImageView postimage;
        CircleImageView profileimage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.post_username);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_description);
            postimage = itemView.findViewById(R.id.post_image);
            profileimage = itemView.findViewById(R.id.post_profile_image);

        }
    }
}
