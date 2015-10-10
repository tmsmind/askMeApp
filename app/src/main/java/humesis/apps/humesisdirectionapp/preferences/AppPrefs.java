/*
 * Copyright (c) 2015.
 */

package humesis.apps.humesisdirectionapp.preferences;

/**
 * Created by dhanraj on 21/09/15.
 */

/**
 * Define constants like name or keys for application preferences
 */
public class AppPrefs {

    private AppPrefs() {
    }

    /**
     * Name of shared preference
     */
    public static final String NAME = "AppPrefs";

    public static final String USER_KEY = "user";

    public static final String LAST_KNOWN_LOCATION = "last_known_location";

    public static final int PLACE_PICKER = 1;

    public static final int SOURCE_PICKED = 2;

    public static final int DEST_PICKED = 3;
}
