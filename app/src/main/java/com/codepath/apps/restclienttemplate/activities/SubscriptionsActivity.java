package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.controllers.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.controllers.FollowsAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SubscriptionsActivity extends AppCompatActivity {

    RecyclerView rvFollows;
    FollowsAdapter mFollowsAdapter;
    ArrayList<User> mSubscriptions;

    TwitterClient client;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        Toolbar toolbar = findViewById(R.id.followstoolbar);
        setSupportActionBar(toolbar);

        client = TwitterApp.getRestClient(this);

        mSubscriptions = new ArrayList<>();
        rvFollows = findViewById(R.id.rvFollows);
        mFollowsAdapter = new FollowsAdapter(this, mSubscriptions);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFollows.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                loadNextDataFromApi(page);
            }
        };
        rvFollows.addOnScrollListener(scrollListener);
        rvFollows.setAdapter(mFollowsAdapter);

        populateSubscription();

    }

    private void populateSubscription() {
        client.getSubscriptions(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++){
                    User user = null;

                    try {
                        user = User.fromJSON(response.getJSONObject(i));
                        mSubscriptions.add(user);
                        mFollowsAdapter.notifyItemInserted(mSubscriptions.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, SubscriptionsActivity.class);
        return intent;
    }
}
