package com.android.askme.fragments;

import android.Manifest;
import android.app.Activity;
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
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.askme.R;
import com.android.askme.adapters.NearbyAdapter;
import com.android.askme.placepicker.Place;
import com.android.askme.placepicker.PlacesService;
import com.android.askme.preferences.AppPrefs;
import com.android.askme.preferences.SettingsUtil;
import com.android.askme.utils.ui.ListItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhanraj on 08/10/15.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnCameraChangeListener {

    private static View rootView;
    SlidingUpPanelLayout bottomSheet;
    ListItem placeInfo;
    private GoogleMap mMap;
    LocationManager mLocationManager;
    ListView placesList;
    NearbyAdapter mAdapter;
    ArrayList<Place> nearbyPlaces = new ArrayList<>();
    Marker currentLocationMarker;
    ArrayList<Marker> markers = new ArrayList<>();
    private Menu menu;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is  */
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        placeInfo = (ListItem) view.findViewById(R.id.place_info);
        bottomSheet = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        placesList = (ListView) view.findViewById(R.id.place_list);

        bottomSheet.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        if (mAdapter == null) {
            mAdapter = new NearbyAdapter(getContext());
            placesList.setAdapter(mAdapter);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);


        bottomSheet.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("onPanelSlide, offset ", "" + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i("onPanelExpanded", "");

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i("", "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i("", "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i("", "onPanelHidden");
            }
        });
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
        mMap.setMyLocationEnabled(false);
        mMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_fragment_menu, menu);
        this.menu = menu;
        if(!(markers.size()>0))
        menu.findItem(R.id.clear_map).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Location location = com.android.askme.utils.LocationManager.getLastKnownLocation(getContext());
        switch (id) {
            case R.id.action_show_gas_station:
                menu.findItem(R.id.clear_map).setVisible(true);
                Log.e("Getting Place", "Gas Station");
                if (location != null)
                    new GetPlacesAsync(getContext(), "gas_station", location).execute();
                else
                    Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_show_restaurents:
                menu.findItem(R.id.clear_map).setVisible(true);
                Log.e("Getting Place", "Restaurants");
                if (location != null)
                    new GetPlacesAsync(getContext(), "cafe", location).execute();
                else
                    Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_my_location:
                zoomToLocation(getLastKnownLocation());
                return true;

            case R.id.clear_map:
                menu.findItem(R.id.clear_map).setVisible(false);
                for (Marker marker : markers) {
                    marker.remove();
                }
                markers = new ArrayList<>();
                bottomSheet.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
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
        onMyLocationChange(bestLocation);
        return bestLocation;
    }

    void zoomToLocation(Location location) {
        if (location != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
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
        if(location!=null) {
            if (currentLocationMarker != null) currentLocationMarker.remove();

            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .title("My Location")
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker))
                    .rotation(location.getBearing())
                    .flat(true));
        }
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
        protected void onPreExecute() {
            super.onPreExecute();
            for(Marker marker:markers){
                marker.remove();
            }
            markers = new ArrayList<>();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... voids) {
            PlacesService service = new PlacesService(context.getResources().getString(R.string.places_web_key));
            ArrayList<Place> findPlaces = service.findPlaces(location.getLatitude(),
                    location.getLongitude(), places);
            Log.e("Nearby Places ::", places);

            for (Place place : findPlaces) {
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
            if (result.size() > 0) {
                nearbyPlaces = result;
                if (mMap != null)
                    for (Place place : result) {
                        markers.add(
                                mMap.addMarker(new MarkerOptions()
                                        .title(place.getName())
                                        .position(
                                                new LatLng(place.getLatitude(), place.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(place.getBitmapIcon()))
                                        .snippet(place.getVicinity()))
                        );
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

                if (places.contentEquals("cafe")) {
                    placeInfo.setIcon(R.drawable.ic_local_restaurant_red_500_24dp);
                    placeInfo.setTitle("Found " + result.size() + " eateries near you");
                } else if (places.contentEquals("gas_station")) {
                    placeInfo.setIcon(R.drawable.ic_local_gas_station_red_500_24dp);
                    placeInfo.setTitle("Found " + result.size() + " Gas stations near you");
                }
                placeInfo.setSubTitle("");
                mAdapter.updateList(result);
                bottomSheet.setAnchorPoint(0.5f);
                bottomSheet.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }else{
                Toast.makeText(context,"Error connecting to Google Services",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
