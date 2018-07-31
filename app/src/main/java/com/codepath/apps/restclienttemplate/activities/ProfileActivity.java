package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvTagLine;
    private TextView tvFollowers;
    private TextView tvFollowing;

    TwitterClient client;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvTagLine = findViewById(R.id.tvTagLine);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);

        client = TwitterApp.getRestClient(this);

        //Setting toolbar
        Toolbar toolbar = findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("user")){
            mUser = (User) getIntent().getSerializableExtra("user");
            getSupportActionBar().setTitle(String.format("@%s", mUser.screenName));
            populateUserInfo();
        } else {
            //Populate the info of the user
            getUserInfo();
        }
        //Getting the Scren Name as intent extra
        String fragArgument = getIntent().getStringExtra("screen_name");

        //Creating the User Timeline Fragment
        UserTimelineFragment fragment = UserTimelineFragment.newInstance(fragArgument);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.timelineContainer, fragment);
        transaction.commit();

        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SubscriptionsActivity.newIntent(ProfileActivity.this);
                startActivity(intent);
            }
        });

        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void getUserInfo() {
        client.getUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mUser = User.fromJSON(response);
                    //Setting  the title of the toolbar
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(String.format("@%s", mUser.screenName));
                    }

                    //Populate user info
                    populateUserInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void populateUserInfo() {

        tvName.setText(mUser.name);
        tvTagLine.setText(mUser.tagLine);
        tvFollowers.setText(String.format("%s Followers", mUser.followers));
        tvFollowing.setText(String.format("%s Following", mUser.following));

        Glide.with(this).load(mUser.profileImageUrl).fitCenter().into(ivProfile);
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, ProfileActivity.class);
        return intent;
    }

    public static Intent newIntent(Context context, User user, String screenName){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("screen_name", screenName);
        return intent;
    }
}
