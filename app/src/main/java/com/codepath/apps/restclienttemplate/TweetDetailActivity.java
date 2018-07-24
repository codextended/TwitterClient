package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

public class TweetDetailActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvName;
    TextView tvScreenName;
    TextView tvTweetMessage;
    ImageView ivTweetImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvTweetMessage = findViewById(R.id.tvTweet);
        ivTweetImage = findViewById(R.id.ivTweetImage);

        Tweet tweet = (Tweet) getIntent().getSerializableExtra("tweet");
        tvName.setText(tweet.user.name);
        tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        tvTweetMessage.setText(tweet.body);
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfile);

    }

    public static Intent newInstance(Context context, Tweet tweet){
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra("tweet", tweet);
        return intent;
    }
}
