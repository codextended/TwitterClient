package com.codepath.apps.restclienttemplate.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.controllers.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.controllers.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment{

    private EndlessRecyclerViewScrollListener scrollListener;
    private long maxId;
    private long lowId;

    TwitterClient client;

    private RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar pbNetwork;
    private TweetAdapter adapter;
    private ArrayList<Tweet> tweets;

    private FloatingActionButton fabComposeTweet;


    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 1){
                boolean refresh = data.getBooleanExtra("refresh", false);
                if (refresh){
                    tweets.clear();
                    adapter.notifyDataSetChanged();
                    populateTimeline();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create the View for the fragment
        View v = inflater.inflate(R.layout.fragment_timeline, container, false);

        //Inflate the views
        rvTweets = v.findViewById(R.id.rvTweet);
        pbNetwork = v.findViewById(R.id.pbNetwork);
        fabComposeTweet = v.findViewById(R.id.fabComposeTweet);
        // finding swiperefresher
        swipeContainer = v.findViewById(R.id.swipeContainer);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        adapter = new TweetAdapter(tweets, getContext());
        //setting layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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

        fabComposeTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                ComposeDialogFragment fragment = ComposeDialogFragment.newInstance();
                fragment.setTargetFragment(TimelineFragment.this, 1);
                fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
                fragment.show(fm, "ComposeTweetFragment");
            }
        });


        populateTimeline();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        maxId = Long.MAX_VALUE;
        lowId = 1;

        //get the client
        client = TwitterApp.getRestClient(getContext());
    }


    private void populateTimeline() {
        pbNetwork.setVisibility(View.VISIBLE);
        client.getHomeTimeline( new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Twitter Client", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Log.d("Twitter Client", response.toString());
                //iterate through the JSON response
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
                    swipeContainer.setRefreshing(false);
                    pbNetwork.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Twitter Client1", errorResponse.toString());
                throwable.printStackTrace();

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

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        client.getMoreHomeTimeline(maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

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

                }
            }
        });

    }


}
