package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.controllers.ViewPagerAdapter;
import com.codepath.apps.restclienttemplate.fragments.ComposeDialogFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.MentionsFragment;
import com.codepath.apps.restclienttemplate.fragments.TimelineFragment;

public class TimelineActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        if (fragment == null) {
//            fragment = new MentionsFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
//        }
        //Initialize the ViewPager
        ViewPager pager = findViewById(R.id.pager);
        //Create a ViewPager Adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Set the ViewPager to the adapter
        pager.setAdapter(adapter);
        //Initialize Tabs
        TabLayout tabLayout = findViewById(R.id.timelineTabs);
        //Attaching the TabLayout to the ViewPager
        tabLayout.setupWithViewPager(pager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.personProfile:
                Intent intent = ProfileActivity.newIntent(this);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
