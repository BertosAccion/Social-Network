package com.example.socialnetwork;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFindFriendsAdapter extends RecyclerView.Adapter<MyFindFriendsAdapter.MyViewHolder> {
    Context ctx;
    ArrayList<FindFriends> result;

    public MyFindFriendsAdapter(Context ctx, ArrayList<FindFriends> result) {
        this.ctx = ctx;
        this.result = result;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.all_users_display_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.username.setText(result.get(i).getUsername());
        myViewHolder.level.setText("Nivel: " + result.get(i).getLevel());
        myViewHolder.team.setText("Equipo: " + result.get(i).getTeam());
        Picasso.get().load(result.get(i).getProfileimage()).placeholder(R.drawable.profile).into(myViewHolder.profileimage);
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


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, level, team;
        CircleImageView profileimage;
        LinearLayout friendLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.all_users_username);
            level = itemView.findViewById(R.id.all_users_lvl);
            team = itemView.findViewById(R.id.all_users_team);
            profileimage = itemView.findViewById(R.id.all_users_profile_image);
            friendLayout = itemView.findViewById(R.id.all_users_background);
        }
    }

}
