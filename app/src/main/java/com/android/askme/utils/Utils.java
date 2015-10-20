package com.android.askme.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.io.InputStream;

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

    /**
     * Reads the file contents and returns a JSON String
     * @param file the file containing the JSON data
     * @return File contents in JSON format
     */
    public static String readFile(Context context,String file){
        InputStream is;
        try {
            is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getPadding(Context context,int padding){
      return   (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding,
                context.getResources()
                        .getDisplayMetrics());
    }
}
