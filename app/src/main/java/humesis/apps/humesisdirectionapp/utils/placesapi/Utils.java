package humesis.apps.humesisdirectionapp.utils.placesapi;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by dhanraj on 09/10/15.
 */
public class Utils {

    public static LatLngBounds setBounds(Location location,int mDistanceInMeters){
        double latRadian = Math.toRadians(location.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.getLatitude() - deltaLat;
        double minLong = location.getLongitude() - deltaLong;
        double maxLat = location.getLatitude() + deltaLat;
        double maxLong = location.getLongitude() + deltaLong;
        Log.d("LatLngBounds", "Min: " + Double.toString(minLat) + "," + Double.toString(minLong));
        Log.d("LatLngBounds", "Max: " + Double.toString(maxLat) + "," + Double.toString(maxLong));

        return new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong));
    }
}
