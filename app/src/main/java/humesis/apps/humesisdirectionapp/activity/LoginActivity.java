package humesis.apps.humesisdirectionapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.models.LocalProfile;
import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.preferences.SettingsUtil;

/**
 * Created by dhanraj on 08/10/15.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButtonFB;
    private TwitterLoginButton loginButtonTW;
    CallbackManager callbackManager;
    private LocalProfile localProfile = new LocalProfile();
    private AccessToken accessToken;

    private final int PROFILE_FACEBOOK = 62394025;
    private final int PROFILE_TWITTER  = 83745893;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButtonFB = (LoginButton) findViewById(R.id.login_button_facebook);
        loginButtonTW = (TwitterLoginButton) findViewById(R.id.login_button_twitter);

        callbackManager = CallbackManager.Factory.create();

        isFBLoggedIn();
        isTwitterLoggedIn();


        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Login Successful", "Facebook");
                Log.d("Login Result", loginResult.toString());
                setUserProfile(PROFILE_FACEBOOK);
            }

            @Override
            public void onCancel() {
                Log.e("Login Cancelled", "Facebook");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Login Failed", "Facebook");
                Log.e("Login Error", error.toString());
            }
        });

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
            }
        });
    }

    public void isFBLoggedIn() {
        if (AccessToken.getCurrentAccessToken()!=null && !AccessToken.getCurrentAccessToken().isExpired()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void isTwitterLoggedIn(){
        if(Twitter.getSessionManager().getActiveSession()!=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void setUserProfile(int id){

        switch (id){
            case PROFILE_FACEBOOK:
                if (Profile.getCurrentProfile() != null) {
                    Log.d("Setting up profile for", "Facebook");
                    localProfile.setName(Profile.getCurrentProfile().getName());
                    localProfile.setEmail("");
                    localProfile.setProfilePic(Profile.getCurrentProfile().getProfilePictureUri(36, 36).toString());
                    SettingsUtil.set(getApplicationContext(), AppPrefs.USER_KEY, new Gson().toJson(localProfile));
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }
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
                                    localProfile.setEmail(user.email);
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
