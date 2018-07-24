package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Smath Cadet on 7/22/2018.
 */

public class ComposeDialogFragment extends DialogFragment {

    public interface RefreshListener{
        void onRefreshList();
    }

    RefreshListener mRefresh;

    TwitterClient client;

    EditText etStatus;
    TextView tvCountChar;
    TextView tvName;
    TextView tvScreenName;
    ImageView ivProfile;
    Button btnSend;

    public ComposeDialogFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etStatus = view.findViewById(R.id.etStatus);
        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCountChar.setText(String.valueOf(140 - s.toString().length()));
            }
        });
        tvCountChar = view.findViewById(R.id.tvCountChar);
        tvName = view.findViewById(R.id.tvName);
        tvScreenName = view.findViewById(R.id.tvScreenName);
        ivProfile = view.findViewById(R.id.ivProfile);
        btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etStatus.getText().toString();
                client.postTweet(message, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mRefresh.onRefreshList();
                        dismiss();
                    }
                });
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get the client
        client = TwitterApp.getRestClient(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRefresh = (RefreshListener) getContext();
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.90), (int) (size.y * 0.90));
        window.setGravity(Gravity.CENTER);

        populateViews();

       // Call super onResume after sizing
        super.onResume();
    }

    private void populateViews() {
        client.getUser(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user = null;
                try {
                    user = User.fromJSON(response);
                    tvName.setText(user.name);
                    tvScreenName.setText(user.screenName);
                    Glide.with(getContext()).load(user.profileImageUrl).into(ivProfile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static ComposeDialogFragment newInstance(){
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        return fragment;
    }
}
