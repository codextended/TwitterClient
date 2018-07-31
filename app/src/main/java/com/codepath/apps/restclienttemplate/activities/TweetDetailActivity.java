package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.fragments.ComposeDialogFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TweetDetailActivity extends AppCompatActivity {

    TwitterClient client;

    ImageView ivProfile;
    TextView tvName;
    TextView tvScreenName;
    TextView tvTweetMessage;
    ImageView ivReply;
    ImageView ivRetweet;
    ImageView ivFavorite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = TwitterApp.getRestClient(this);

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvTweetMessage = findViewById(R.id.tvTweet);
        ivReply = findViewById(R.id.ivReply);
        ivRetweet = findViewById(R.id.ivRetweet);
        ivFavorite = findViewById(R.id.ivFavorite);

        final Tweet tweet = (Tweet) getIntent().getSerializableExtra("tweet");
        tvName.setText(tweet.user.name);
        tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        tvTweetMessage.setText(tweet.body);
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfile);

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeDialogFragment fragment = ComposeDialogFragment.newInstance(tweet.user.screenName);
                fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
                fragment.show(fm, "ComposeTweetFragment");
            }
        });

        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.retweet(tweet.uid, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(TweetDetailActivity.this, "Successfully retweeted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.addFavorite(tweet.uid, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(TweetDetailActivity.this, "Successfully added to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public static Intent newInstance(Context context, Tweet tweet){
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra("tweet", tweet);
        return intent;
    }
}
