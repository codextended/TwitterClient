package com.codepath.apps.restclienttemplate.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.ArrayList;

/**
 * Created by Smath Cadet on 7/30/2018.
 */

public class FollowsAdapter extends RecyclerView.Adapter<FollowsAdapter.FollowsHolder> {

    Context mContext;
    ArrayList<User> mUsers;

    public FollowsAdapter(Context context, ArrayList<User> users){
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public FollowsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_follows, parent, false);
        return new FollowsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowsHolder holder, int position) {

        User user = mUsers.get(position);

        holder.tvScreenNameFollows.setText(String.format("@%s", user.screenName));
        holder.tvDescription.setText(user.tagLine);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class FollowsHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfileFollows;
        private TextView tvScreenNameFollows;
        private TextView tvDescription;

        public FollowsHolder(View itemView) {
            super(itemView);

            ivProfileFollows = itemView.findViewById(R.id.ivProfileFollows);
            tvScreenNameFollows = itemView.findViewById(R.id.tvScreenNameFollows);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
