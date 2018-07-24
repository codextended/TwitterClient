package com.codepath.apps.restclienttemplate.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Smath Cadet on 7/22/2018.
 */

@Entity
public class Tweet implements Serializable{
    @ColumnInfo
    public String body;
    @ColumnInfo
    @PrimaryKey
    public long uid;
    @ColumnInfo
    public User user;
    @ColumnInfo
    public String createdAt;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.createdAt = jsonObject.getString("created_at");
        return tweet;
    }
}
