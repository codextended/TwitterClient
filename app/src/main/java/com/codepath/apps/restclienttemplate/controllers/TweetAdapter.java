package com.codepath.apps.restclienttemplate.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.activities.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Smath Cadet on 7/22/2018.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetHolder> {

    private ArrayList<Tweet> mTweets;
    private Context mContext;

    public TweetAdapter(ArrayList<Tweet> tweets, Context context){
        mContext = context;
        mTweets = tweets;
    }

    @NonNull
    @Override
    public TweetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Crreate the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);

        return new TweetHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetHolder holder, final int position) {
        //Bind the views to the holder
        final Tweet tweet = mTweets.get(position);

        //Set the textviews
        holder.tvUserName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        holder.tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt));

        //set imageview with glide
        Glide.with(mContext).load(tweet.user.profileImageUrl).asBitmap().centerCrop().into(holder.ivProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = TweetDetailActivity.newInstance(mContext, tweet);
                mContext.startActivity(intent);
            }
        });

        holder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ProfileActivity.newIntent(mContext, tweet.user, tweet.user.screenName);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    //Create the holder inner class

    public class TweetHolder extends RecyclerView.ViewHolder{
        ImageView ivProfile;
        TextView tvUserName;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTime;

        public TweetHolder(View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
//            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
//                    System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            long span = Math.max(System.currentTimeMillis() - dateMillis, 0);
            if (span >= DateUtils.YEAR_IN_MILLIS) {
                return (span / DateUtils.YEAR_IN_MILLIS) + "y";
            }
            if (span >= DateUtils.WEEK_IN_MILLIS) {
                return (span / DateUtils.WEEK_IN_MILLIS) + "w";
            }
            if (span >= DateUtils.DAY_IN_MILLIS) {
                return (span / DateUtils.DAY_IN_MILLIS) + "d";
            }
            if (span >= DateUtils.HOUR_IN_MILLIS) {
                return (span / DateUtils.HOUR_IN_MILLIS) + "h";
            }
            return (span / DateUtils.MINUTE_IN_MILLIS) + "m";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


}
