package com.codepath.apps.restclienttemplate.controllers;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.MentionsFragment;
import com.codepath.apps.restclienttemplate.fragments.TimelineFragment;

/**
 * Created by Smath Cadet on 7/27/2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = {"Home", "Mentions"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new TimelineFragment();
        }else if (position == 1){
            return new MentionsFragment();
        }else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
