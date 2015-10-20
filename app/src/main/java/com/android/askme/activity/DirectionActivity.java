/*
 * ==================================================================
 *   Copyright (c) 2015 Dhanraj Padmashali (Humesis, Inc.)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *  ==================================================================
 */

package com.android.askme.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.askme.R;
import com.android.askme.models.DirectionInstructions;
import com.android.askme.models.Event;
import com.android.askme.utils.GoogleDirection;
import com.android.askme.utils.ui.ListItemCard;
import com.android.askme.utils.ui.WrapContentViewPager;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by dhanraj on 08/10/15.
 */
public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap;
    LocationManager mLocationManager;
    Marker currentLocationMarker;

    Place source, dest;
    GoogleDirection googleDirection;
    Document document;

    ImageButton buttonPrevious;
    ImageButton buttonNext;
    WrapContentViewPager viewPager;


    ArrayList<LatLng> sections = new ArrayList<>();
    ArrayList<DirectionInstructions> directionInstructions = new ArrayList<>();
    private float lastX;

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

    public void onEvent(Event.DirectionEvent event) {
        if(event!=null){
            Log.e("Event Called","");
            source = event.getSource();
            dest = event.getDest();
            googleDirection = event.getGoogleDirection();
            document = event.getDocument();
           }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate Called", "");
        setContentView(R.layout.activity_maps);

        //onEvent(EventBus.getDefault().getStickyEvent(Event.DirectionEvent.class));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buttonPrevious = (ImageButton) findViewById(R.id.button_previous);
        buttonNext = (ImageButton) findViewById(R.id.button_next);

        viewPager = (WrapContentViewPager) findViewById(R.id.view_pager);


        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("onMapReady Called", "");
        mMap = googleMap;

        mMap.setOnMyLocationChangeListener(this);
        mMap.setMyLocationEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.setOnCameraChangeListener(this);
        mMap.addPolyline(googleDirection.getPolyline(document, 6, getResources().getColor(R.color.colorPrimaryDark)));
        mMap.addMarker(new MarkerOptions().title(source.getName().toString()).position(source.getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_from)));
        mMap.addMarker(new MarkerOptions().title(dest.getName().toString()).position(dest.getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_to)));

        directionInstructions = googleDirection.getInstructions(document);
        sections = googleDirection.getSection(document);

        viewPager.setAdapter(new CustomPagerAdapter(this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sections.get(position), 18.5f));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        zoomToLocation(source.getLatLng());

    }

    @Override
    public void onMyLocationChange(Location location) {
        if (currentLocationMarker != null) currentLocationMarker.remove();

        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .title("My Location")
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker))
                .rotation(location.getBearing())
                .flat(true));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.e("Zoom",""+cameraPosition.zoom);
    }

    private void getLastKnownLocation() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
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
        onMyLocationChange(bestLocation);
    }

    void zoomToLocation(LatLng latLng) {
        if (latLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)      // Sets the center of the map to location user
                    .zoom(18.5f)                   // Sets the zoom
                    .tilt(67.5f)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    void animateToLocation(LatLng latLng,float bearing,float zoom,float tilt){
        if (latLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(zoom)
                    .tilt(tilt)
                    .bearing(bearing)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public class CustomPagerAdapter extends PagerAdapter{

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ListItemCard item = new ListItemCard(mContext);
            item.setSingleLine(false);
            item.setTitle(String.valueOf(Html.fromHtml(directionInstructions.get(position).getInstruction())));
            item.setSubTitle("in " + directionInstructions.get(position).getDurationText() + "(" + directionInstructions.get(position).getDistanceText() + ")");
            item.showIcon(false);
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return directionInstructions.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
