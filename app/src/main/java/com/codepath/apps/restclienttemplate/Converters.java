package com.codepath.apps.restclienttemplate;

import android.arch.persistence.room.TypeConverter;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Smath Cadet on 7/23/2018.
 */

public class Converters {
    @TypeConverter
    public static User fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromTweet(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        return json;
    }
}
