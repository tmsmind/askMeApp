package humesis.apps.humesisdirectionapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by dhanraj on 08/10/15.
 */
public class MyApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "zfe7fnMiBLUED9SErGQvSDmba";
    private static final String TWITTER_SECRET = "UuE7vD2qQJ7IF4R6lO3iQHVwnaKBOjt3gA7iaNRkeEXnVdBTTy";


    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());
        // TODO: Get cities between source and dest. Time can be specified by user (2-3 hours).
    }
}
