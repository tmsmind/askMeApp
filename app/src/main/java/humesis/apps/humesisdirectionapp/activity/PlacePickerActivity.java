package humesis.apps.humesisdirectionapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.adapters.PlaceAutocompleteAdapter;
import humesis.apps.humesisdirectionapp.models.NotifyEvent;
import humesis.apps.humesisdirectionapp.placepicker.PlacesService;
import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.utils.GoogleDirection;


/**
 * Created by dhanraj on 10/10/15.
 */
public class PlacePickerActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMyLocationChangeListener, GoogleDirection.OnDirectionResponseListener {

    private PlaceAutocompleteAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView searchText;
    private GoogleMap mMap;
    private GoogleDirection googleDirection;
    private LatLng center;

    TextView placeName, placeAddress;

   /* *//**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     *//*
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @SuppressLint("LongLogTag")
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Place query did not complete. Error: ", places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.i("Place details received: ", "" + place.getName());

            places.release();
        }
    };
    *//**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     *//*
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @SuppressLint("LongLogTag")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            *//*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              *//*
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("Autocomplete item selected: ", primaryText.toString());

            *//*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              *//*
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i("Called getPlaceById to get Place details for ", placeId);
        }
    };

    */

    /**
     * Returns the list of  places nearby based on location.
     *//*
    private ResultCallback<PlaceLikelihoodBuffer> mUpdateCurrentLocationCallback = new ResultCallback<PlaceLikelihoodBuffer>() {
        @Override
        public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                Log.i("PlaceLikelihoodBuffer", String.format("Place '%s' has likelihood: %g",
                        placeLikelihood.getPlace().getName(),
                        placeLikelihood.getLikelihood()));
            }
            likelyPlaces.release();
        }
    };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_place_picker);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 34995, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        /*searchText.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(getApplicationContext(), mGoogleApiClient, Utils.setBounds(humesis.apps.humesisdirectionapp.utils.LocationManager.getLastKnownLocation(this), 500),
                null);
        searchText.setAdapter(mAdapter);*/

//        placeName = (TextView) findViewById(R.id.name);
//        placeAddress = (TextView) findViewById(R.id.description);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, AppPrefs.PLACE_PICKER);

            // Hide the pick option in the UI to prevent users from starting the picker
            // multiple times.


        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(PlacePickerActivity.this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppPrefs.PLACE_PICKER:
                if (resultCode == Activity.RESULT_OK) {
                    final Place place = PlacePicker.getPlace(data, this);
                    final CharSequence name = place.getName();
                    EventBus.getDefault().post(new NotifyEvent(NotifyEvent.NotifyType.PLACE_PICKED, (String) name));
                }
                finish();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        zoomToLocation(humesis.apps.humesisdirectionapp.utils.LocationManager.getLastKnownLocation(this));
        mMap.setOnMyLocationChangeListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
        googleDirection = new GoogleDirection(this);
        googleDirection.setOnDirectionResponseListener(this);
    }

    void zoomToLocation(Location location) {
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        center = mMap.getCameraPosition().target;
        try {
            new GetLocationAsync(center.latitude, center.longitude)
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMyLocationChange(Location location) {

    }

    @Override
    public void onResponse(String status, Document doc, GoogleDirection gd) {
        mMap.addPolyline(gd.getPolyline(doc, 3, Color.parseColor("3F51B5")));
    }

    private class GetLocationAsync extends AsyncTask<String, Void, List<Address>> {

        // boolean duplicateResponse;
        double x, y;
        String str;
        private Geocoder geocoder;

        boolean isStart;
        private List<Address> addresses;

        public GetLocationAsync(double latitude, double longitude) {
            // TODO Auto-generated constructor stub

            x = latitude;
            y = longitude;
        }


        @Override
        protected void onPreExecute() {
            placeName.setText(" Getting location ");
            placeAddress.setText("");
        }

        @Override
        protected List<Address> doInBackground(String... params) {

            try {
                geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            str = new PlacesService(getResources().getString(R.string.google_maps_key)).reverseGeocode(x,y);
            return addresses;

        }

        /**
         * @param result
         */
        @Override
        protected void onPostExecute(List<Address> result) {
            Address returnAddress = result.get(0);
            Log.e("Address",str);
            placeName.setText(returnAddress.getFeatureName());
            placeAddress.setText(returnAddress.getAddressLine(0));
        }
    }

}
