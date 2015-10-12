package humesis.apps.humesisdirectionapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.Overlay;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.models.MapOverlayItem;
import humesis.apps.humesisdirectionapp.placepicker.Place;
import humesis.apps.humesisdirectionapp.placepicker.PlacesService;
import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.preferences.SettingsUtil;
import humesis.apps.humesisdirectionapp.utils.GoogleDirection;

/**
 * Created by dhanraj on 08/10/15.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnCameraChangeListener {

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    private GoogleMap mMap;
    LocationManager mLocationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastKnownLocation();
        mMap.setOnMyLocationChangeListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_show_gas_station:
                mMap.clear();
                Log.e("Getting Place","Gas Station");
                new GetPlacesAsync(getContext(),"gas_station",getLastKnownLocation()).execute();
                return true;
            case R.id.action_show_restaurents:
                mMap.clear();
                Log.e("Getting Place", "Restaurants");
                new GetPlacesAsync(getContext(), "cafe", getLastKnownLocation()).execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        SettingsUtil.set(getContext(), AppPrefs.LAST_KNOWN_LOCATION, bestLocation);
        zoomToLocation(bestLocation);
        return bestLocation;
    }

    void zoomToLocation(Location location) {
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to north
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onMyLocationChange(Location location) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("requestCode", "MapsFragment" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class GetPlacesAsync extends AsyncTask<Void, Void, ArrayList<Place>> {

        private Context context;
        private String places;
        private Location location;

        public GetPlacesAsync(Context context, String places, Location location) {
            this.context = context;
            this.places = places;
            this.location = location;
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... voids) {
            PlacesService service = new PlacesService(context.getResources().getString(R.string.places_web_key));
            ArrayList<Place> findPlaces = service.findPlaces(location.getLatitude(),
                    location.getLongitude(), places);
            Log.e("Nearby Places ::", places);

            for(Place place:findPlaces){
                try {
                    place.setBitmapIcon(Picasso.with(context).load(place.getIcon()).get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return findPlaces;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            if(mMap!=null)
            for (Place place:result) {
                    mMap.addMarker(new MarkerOptions()
                            .title(place.getName())
                            .position(
                                    new LatLng(place.getLatitude(), place.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(place.getBitmapIcon()))
                            .snippet(place.getVicinity()));
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(result.get(0).getLatitude(), result
                            .get(0).getLongitude())) // Sets the center of the map to
                            // Mountain View
                    .zoom(15) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }
}
