package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Smath Cadet on 7/22/2018.
 */

public class User implements Serializable{
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public static User fromJSON(JSONObject jsonObject) throws JSONException{
        User user = new User();
        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");
        return user;
    }

}
