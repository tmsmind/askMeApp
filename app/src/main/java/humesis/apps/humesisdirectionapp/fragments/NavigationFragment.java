package humesis.apps.humesisdirectionapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.activity.PlacePickerActivity;
import humesis.apps.humesisdirectionapp.adapters.DirectionsAdapter;
import humesis.apps.humesisdirectionapp.models.Event;
import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.utils.GoogleDirection;
import humesis.apps.humesisdirectionapp.utils.ui.ListItem;


/**
 * Created by dhanraj on 08/10/15.
 */
public class NavigationFragment extends Fragment implements View.OnClickListener, GoogleDirection.OnDirectionResponseListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnCameraChangeListener {
    private static View rootView;
    ListItem sourceItem,destItem,routeInfo;
    boolean srcPicked,destPicked = false;
    Place source,dest;
    private GoogleDirection googleDirection;
    CardView routeInfoCard;
    private GoogleMap mMap;
    SlidingUpPanelLayout bottomSheet;
    LinearLayout dragView;
    ListView directionLists;
    DirectionsAdapter directionsAdapter;
    public NavigationFragment() {
    }

    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(Event.PlaceEvent event){
        try {
            Log.e("Event Recieved", event.getPlace().getName().toString());
            switch (event.getEventType()) {
                case SOURCE_PICKED:
                    source = event.getPlace();
                    sourceItem.setTitle(source.getName().toString());
                    sourceItem.setSubTitle(source.getAddress().toString());
                    srcPicked = true;
                    getRouteInfo();
                    break;
                case DEST_PICKED:
                    dest = event.getPlace();
                    destItem.setTitle(dest.getName().toString());
                    destItem.setSubTitle(dest.getAddress().toString());
                    destPicked = true;
                    getRouteInfo();
                    break;
            }
        }catch (Exception e){
            Toast.makeText(getContext(),"Error getting location data, try again later",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is  */
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sourceItem = (ListItem) view.findViewById(R.id.source);
        destItem = (ListItem) view.findViewById(R.id.dest);
        routeInfo = (ListItem) view.findViewById(R.id.route_info);
        routeInfoCard = (CardView) view.findViewById(R.id.route_info_card);
        bottomSheet = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        dragView = (LinearLayout) view.findViewById(R.id.dragView);
        directionLists = (ListView) view.findViewById(R.id.list);

        bottomSheet.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("DirectionsMap");
        if (mapFragment!=null)
        mapFragment.getMapAsync(this);

        sourceItem.setOnClickListener(this);
        destItem.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.source:
                openPlacePicker(Event.EventType.SOURCE_PICKED,AppPrefs.SOURCE_PICKED);
                srcPicked = true;
                break;

            case R.id.dest:
                 openPlacePicker(Event.EventType.DEST_PICKED,AppPrefs.DEST_PICKED);
                break;
        }
    }

    public void openPlacePicker(Event.EventType code,int result) {

        Intent intent = new Intent(getContext(), PlacePickerActivity.class);
        EventBus.getDefault().postSticky(new Event(code));
        startActivityForResult(intent, result);
    }

    void getRouteInfo(){
        mMap.clear();
        if(srcPicked && destPicked){
            googleDirection.request(source.getLatLng(), dest.getLatLng(), GoogleDirection.MODE_DRIVING);
        }
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
    public void onResponse(String status, Document doc, GoogleDirection gd) {

        routeInfoCard.setVisibility(View.VISIBLE);
        routeInfo.setTitle("ETA " + gd.getTotalDurationText(doc));
        routeInfo.setSubTitle("Approx " + gd.getTotalDistanceText(doc));
        mMap.addPolyline(gd.getPolyline(doc, 4, getResources().getColor(R.color.md_green_500)));
        mMap.addMarker(new MarkerOptions().title(source.getName().toString()).position(source.getLatLng()));
        mMap.addMarker(new MarkerOptions().title(dest.getName().toString()).position(dest.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        bottomSheet.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        if(directionsAdapter==null){
            directionsAdapter = new DirectionsAdapter(getContext(),gd.getInstructions(doc));
            directionLists.setAdapter(directionsAdapter);
        }else{
            directionsAdapter.updateList(gd.getInstructions(doc));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationChangeListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
        googleDirection = new GoogleDirection(getContext());
        googleDirection.setOnDirectionResponseListener(this);
        zoomToLocation(humesis.apps.humesisdirectionapp.utils.LocationManager.getLastKnownLocation(getContext()));
    }

    @Override
    public void onMyLocationChange(Location location) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }
}
