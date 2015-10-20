/*
 * ==================================================================
 *   Copyright (c) 2015 Dhanraj Padmashali (Humesis, Inc.)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *  ==================================================================
 */

package com.android.askme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.askme.R;
import com.android.askme.models.LocalProfile;
import com.android.askme.preferences.AppPrefs;
import com.android.askme.preferences.SettingsUtil;
import com.android.askme.utils.ui.MaterialProgressBar;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhanraj on 08/10/15.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButtonFB;
    private TwitterLoginButton loginButtonTW;
    CallbackManager callbackManager;
    private LocalProfile localProfile = new LocalProfile();
    private AccessToken accessToken;

    Button facebook,twitter;

    private final int PROFILE_FACEBOOK = 62394025;
    private final int PROFILE_TWITTER = 83745893;

    MaterialProgressBar progressBar;
    RelativeLayout buttonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonContainer = (RelativeLayout) findViewById(R.id.login_button_container);
        progressBar = (MaterialProgressBar) findViewById(R.id.progress_bar);

        loginButtonFB = (LoginButton) findViewById(R.id.login_button_facebook);
        loginButtonTW = (TwitterLoginButton) findViewById(R.id.login_button_twitter);
        facebook = (Button) findViewById(R.id.button_facebook);
        twitter = (Button) findViewById(R.id.button_twitter);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                loginButtonFB.performClick();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                loginButtonTW.performClick();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButtonFB.setReadPermissions("public_profile");
        isFBLoggedIn();
        isTwitterLoggedIn();

        /**
         * Facebook callback
         */
        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Login Successful", "Facebook");
                Log.d("Login Result", loginResult.toString());
                ProfileTracker profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        this.stopTracking();
                        Profile.setCurrentProfile(currentProfile);
                        setUserProfile(PROFILE_FACEBOOK);
                    }
                };
                profileTracker.startTracking();
            }

            @Override
            public void onCancel() {
                Log.e("Login Cancelled", "Facebook");
                buttonContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Login Failed", "Facebook");
                Log.e("Login Error", error.toString());
                buttonContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        /**
         * Twitter Callback
         */
        loginButtonTW.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("Login Successful", "Twitter");
                Log.d("Login Result", result.toString());
                setUserProfile(PROFILE_TWITTER);
            }

            @Override
            public void failure(TwitterException e) {
                Log.e("Login Failed", "Twitter");
                Log.e("Login Error", e.toString());
                buttonContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void isFBLoggedIn() {
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void isTwitterLoggedIn() {
        if (Twitter.getSessionManager().getActiveSession() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void setUserProfile(int id) {

        switch (id) {
            case PROFILE_FACEBOOK:
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.e("Graph response", object.toString());
                                try {
                                    JSONObject cover = object.getJSONObject("cover");
                                    Log.e("Object response", cover.getString("source"));
                                    localProfile.setCoverPic(cover.getString("source"));
                                    localProfile.setEmail(object.getString("email"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (Profile.getCurrentProfile() != null) {
                                    Log.d("Setting up profile for", "Facebook");
                                    localProfile.setName(Profile.getCurrentProfile().getName());
                                    localProfile.setProfilePic(Profile.getCurrentProfile().getProfilePictureUri(36, 36).toString());
                                    Log.e("Local Profile", localProfile.toString());
                                    SettingsUtil.set(getApplicationContext(), AppPrefs.USER_KEY, new Gson().toJson(localProfile));
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,cover");
                request.setParameters(parameters);
                request.executeAsync();


                break;

            case PROFILE_TWITTER:
                if (Twitter.getSessionManager().getActiveSession() != null) {
                    Log.d("Setting up profile for", "Twitter");
                    Twitter.getApiClient(Twitter.getSessionManager().getActiveSession()).getAccountService()
                            .verifyCredentials(true, false, new Callback<User>() {
                                @Override
                                public void success(Result<User> userResult) {
                                    User user = userResult.data;
                                    localProfile.setName(user.name);
                                    localProfile.setProfilePic(user.profileImageUrl);
                                    localProfile.setEmail("@"+user.screenName);
                                    localProfile.setCoverPic(user.profileBannerUrl);
                                    SettingsUtil.set(getApplicationContext(), AppPrefs.USER_KEY, new Gson().toJson(localProfile));
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void failure(TwitterException e) {

                                }

                            });
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        loginButtonTW.onActivityResult(requestCode, resultCode, data);
    }
}
