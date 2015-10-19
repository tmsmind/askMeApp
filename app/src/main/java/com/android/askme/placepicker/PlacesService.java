package com.android.askme.placepicker;

import android.util.Log;

import com.android.askme.models.RouteInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by dhanraj on 10/10/15.
 */
public class PlacesService {

    private String API_KEY;

    public PlacesService(String apikey) {
        this.API_KEY = apikey;
    }

    public void setApiKey(String apikey) {
        this.API_KEY = apikey;
    }

    public ArrayList<Place> findPlaces(double latitude, double longitude,
                                       String type) {

        String urlString = makeUrl(latitude, longitude, type);

        try {
            String json = getJSON(urlString);

            System.out.println(json);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            ArrayList<Place> arrayList = new ArrayList<Place>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place
                            .jsonToPlace((JSONObject) array.get(i));
                    Log.v("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException ex) {
           ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<Place> nearbyPlaces(double latitude, double longitude,
                                       String type) {

        String urlString = makeUrlNearby(latitude, longitude, type);

        try {
            String json = getJSON(urlString);

            System.out.println(json);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            ArrayList<Place> arrayList = new ArrayList<Place>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place
                            .jsonToPlace((JSONObject) array.get(i));
                    Log.v("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String reverseGeocode(double latitude, double longitude){
        String urlString = makeUrlReverseGeoCode(latitude, longitude);

        try {
            String json = getJSON(urlString);

            System.out.println(json);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            String address = (String) ((JSONObject) array.get(0)).get("formatted_address");
            return address;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public RouteInfo getDistance(double o_latitude, double o_longitude, double d_latitude, double d_longitude){
        String urlString = makeUrlDistance(o_latitude, o_longitude,d_latitude,d_longitude);

        String json = getJSON(urlString);
        Log.e("getDistance",json);
        Gson gson = new Gson();
        return gson.fromJson(json,RouteInfo.class);
    }


    // https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
    private String makeUrl(double latitude, double longitude, String place) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");

        if (place.equals("")) {
            urlString.append("&location=");
            urlString.append(String.valueOf(latitude));
            urlString.append(",");
            urlString.append(String.valueOf(longitude));
            urlString.append("&radius=1000");
            // urlString.append("&types="+place);
            urlString.append("&sensor=false&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(String.valueOf(latitude));
            urlString.append(",");
            urlString.append(String.valueOf(longitude));
            urlString.append("&radius=500");
            urlString.append("&types=" + place);
            urlString.append("&sensor=false&key=" + API_KEY);
        }
        return urlString.toString();
    }

    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
    private String makeUrlNearby(double latitude, double longitude, String place) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        if (place.equals("")) {
            urlString.append("&location=");
            urlString.append(String.valueOf(latitude));
            urlString.append(",");
            urlString.append(String.valueOf(longitude));
            urlString.append("&radius=500");
            // urlString.append("&types="+place);
            urlString.append("&sensor=false&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(String.valueOf(latitude));
            urlString.append(",");
            urlString.append(String.valueOf(longitude));
            urlString.append("&radius=500");
            urlString.append("&types=" + place);
            urlString.append("&sensor=false&key=" + API_KEY);
        }
        return urlString.toString();
    }

    private String makeUrlReverseGeoCode(double latitude, double longitude) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/geocode/json?");

        urlString.append("latlng=");
        urlString.append(String.valueOf(latitude));
        urlString.append(",");
        urlString.append(String.valueOf(longitude));
        urlString.append("&key=" + API_KEY);
        return urlString.toString();
    }

    private String makeUrlDistance(double o_latitude, double o_longitude,double d_latitude,double d_longitude) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/distancematrix/json?");

        urlString.append("origins=");
        urlString.append(String.valueOf(o_latitude));
        urlString.append(",");
        urlString.append(String.valueOf(o_longitude));
        urlString.append("&destinations=");
        urlString.append(String.valueOf(d_latitude));
        urlString.append(",");
        urlString.append(String.valueOf(d_longitude));
        urlString.append("&key=" + API_KEY);
        return urlString.toString();
    }


    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}