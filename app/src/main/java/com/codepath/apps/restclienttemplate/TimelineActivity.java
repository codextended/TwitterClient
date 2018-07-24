package com.codepath.apps.restclienttemplate;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.SampleModel;
import com.codepath.apps.restclienttemplate.models.SampleModelDao;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.RefreshListener {

    private EndlessRecyclerViewScrollListener scrollListener;
    private long maxId;
    private long lowId;

    TwitterClient client;

    private RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;
    private TweetAdapter adapter;
    private ArrayList<Tweet> tweets;
    private FloatingActionButton fabComposeTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        maxId = Long.MAX_VALUE;
        lowId = 1;

        //get the client
        client = TwitterApp.getRestClient(this);

        //finding the recyclerview
        rvTweets = findViewById(R.id.rvTweet);
        // finding swiperefresher
        swipeContainer = findViewById(R.id.swipeContainer);
        //set colrs for the refresher
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //set OnRefresher Listener
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweets.clear();
                adapter.notifyDataSetChanged();
                populateTimeline();
            }
        });
        //initialize arraylist of tweets
        tweets = new ArrayList<>();
        //creating adapter
        adapter = new TweetAdapter(tweets, this);
        //setting layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        //setting adapter
        rvTweets.setAdapter(adapter);


        fabComposeTweet = findViewById(R.id.fabComposeTweet);
        fabComposeTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeDialogFragment fragment = ComposeDialogFragment.newInstance();
                fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
                fragment.show(fm, "ComposeTweetFragment");
            }
        });

        populateTimeline();
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        client.getMoreHomeTimeline(maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                final SampleModelDao sampleModelDao = ((TwitterApp) getApplicationContext()).getMyDatabase().sampleModelDao();
                for (int i = 0; i < response.length(); i++){
                    //get json object in the position index
                    //create a tweet object with json object
                    //add the tweet object to the arraylist of tweets
                    //notify changes to adapter
                    Tweet tweet = null;
                    try {
                        tweet = Tweet.fromJson(response.getJSONObject(i));
                        tweets.add(tweet);
                        adapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   if (tweet.uid < maxId){
                        maxId = tweet.uid;
                        Log.d("MaxId", String.valueOf(maxId));
                    }
                    AsyncTask<Tweet, Void, Void> task = new AsyncTask<Tweet, Void, Void>() {
                        @Override
                        protected Void doInBackground(Tweet... tweets) {
                            sampleModelDao.insertTweet(tweets);
                            return null;
                        };
                    };
                    task.execute(tweet);


                }
            }
        });

    }

    private void populateTimeline() {
        client.getHomeTimeline( new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Twitter Client", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Log.d("Twitter Client", response.toString());
                //iterate through the JSON response
                final SampleModelDao sampleModelDao = ((TwitterApp) getApplicationContext()).getMyDatabase().sampleModelDao();
                AsyncTask<Void, Void, Void> taskClear = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        sampleModelDao.deleteAll(tweets);
                        return null;
                    };
                };
                taskClear.execute();
                for (int i = 0; i < response.length(); i++){
                    //get json object in the position index
                    //create a tweet object with json object
                    //add the tweet object to the arraylist of tweets
                    //notify changes to adapter
                    Tweet tweet = null;
                    try {
                        tweet = Tweet.fromJson(response.getJSONObject(i));
                        tweets.add(tweet);
                        adapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (tweet.uid < maxId){
                        maxId = tweet.uid;
                        Log.d("MaxId", String.valueOf(maxId));
                    }



                    AsyncTask<Tweet, Void, Void> task = new AsyncTask<Tweet, Void, Void>() {
                        @Override
                        protected Void doInBackground(Tweet... tweets) {
                            sampleModelDao.insertTweet(tweets);
                            return null;
                        };
                    };
                    task.execute(tweet);
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Twitter Client1", errorResponse.toString());
                throwable.printStackTrace();
                final SampleModelDao sampleModelDao = ((TwitterApp) getApplicationContext()).getMyDatabase().sampleModelDao();
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        tweets = (ArrayList<Tweet>)sampleModelDao.offlineTweets();
                        adapter.notifyDataSetChanged();
                        return null;
                    };
                };
                task.execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("Twitter Client2", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Twitter Client3", responseString);
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void onRefreshList() {
        tweets.clear();
        adapter.notifyDataSetChanged();
        populateTimeline();
    }
}
