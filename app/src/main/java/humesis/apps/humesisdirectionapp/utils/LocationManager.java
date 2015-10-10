package humesis.apps.humesisdirectionapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import java.util.List;

import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.preferences.SettingsUtil;

/**
 * Created by dhanraj on 10/10/15.
 */
public class LocationManager {

    public static Location getLastKnownLocation(Context context) {
        android.location.LocationManager mLocationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return null;
                }
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
