package humesis.apps.humesisdirectionapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.adapters.ViewPagerAdapter;
import humesis.apps.humesisdirectionapp.fragments.CarsFragment;
import humesis.apps.humesisdirectionapp.fragments.NavigationFragment;
import humesis.apps.humesisdirectionapp.fragments.MapFragment;
import humesis.apps.humesisdirectionapp.fragments.PlacesFragment;
import humesis.apps.humesisdirectionapp.models.LocalProfile;
import humesis.apps.humesisdirectionapp.models.NotifyEvent;
import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.preferences.SettingsUtil;
import humesis.apps.humesisdirectionapp.utils.ui.MyViewPager;
import humesis.apps.humesisdirectionapp.utils.ui.TabButtons;

/**
 * Created by dhanraj on 08/10/15.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    Toolbar toolbar;
    FragmentManager fm;
    FragmentTransaction ft;
    private Drawer result = null;

    MyViewPager viewPager;
    List<Fragment> fList = new ArrayList<>();
    ViewPagerAdapter mAdapter;
    TabButtons mapsButton,directionButton,placeListButton,profileButton,carsButton;
    LocalProfile localProfile = new LocalProfile();
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupViews();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 34992, this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationChecker();
        setupProfileData();
        setUpNavigationDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.maps_button:
//                fm.beginTransaction()
//                        .replace(R.id.fragment_container, MapFragment.newInstance())
//                        .commit();
                viewPager.setCurrentItem(0,true);
                getSupportActionBar().setTitle("Maps");
                clearIndicator();
                mapsButton.setEnabled(true);
                break;
            case R.id.cars_button:
                viewPager.setCurrentItem(1, true);
                getSupportActionBar().setTitle("Cars");
                clearIndicator();
                carsButton.setEnabled(true);
                break;
            case R.id.direction_button:
//                fm.beginTransaction()
//                        .replace(R.id.fragment_container, NavigationFragment.newInstance())
//                        .commit();
                viewPager.setCurrentItem(2, true);
                getSupportActionBar().setTitle("Navigating");
                clearIndicator();
                directionButton.setEnabled(true);
                break;
            case R.id.places_list_button:
                viewPager.setCurrentItem(3, true);
                getSupportActionBar().setTitle("Nearby");
                clearIndicator();
                placeListButton.setEnabled(true);
                break;
            case R.id.profile_button:

                break;
        }
    }

    void clearIndicator(){
        mapsButton.setEnabled(false);
        carsButton.setEnabled(false);
        directionButton.setEnabled(false);
        placeListButton.setEnabled(false);
        profileButton.setEnabled(false);
    }

    void setupViews(){
        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        mapsButton = (TabButtons)findViewById(R.id.maps_button);
        carsButton = (TabButtons) findViewById(R.id.cars_button);
        directionButton = (TabButtons)findViewById(R.id.direction_button);
        placeListButton = (TabButtons)findViewById(R.id.places_list_button);
        profileButton = (TabButtons)findViewById(R.id.profile_button);

        mapsButton.setOnClickListener(this);
        carsButton.setOnClickListener(this);
        directionButton.setOnClickListener(this);
        placeListButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);

        setupFragments();
    }

    void setupFragments(){
        fList.add(MapFragment.newInstance());
        fList.add(CarsFragment.newInstance());
        fList.add(NavigationFragment.newInstance());
        fList.add(PlacesFragment.newInstance());
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fList);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(mAdapter);
    }

    void setupProfileData(){
        localProfile = new Gson().fromJson(SettingsUtil.get(getApplicationContext(), AppPrefs.USER_KEY,""),LocalProfile.class);

    }


    void setUpNavigationDrawer() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(localProfile.getName())
                                .withEmail(localProfile.getEmail())
                                .withIcon(localProfile.getProfilePic())
                )
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Maps"),
                        new PrimaryDrawerItem().withName("Places Nearby"),
                        new PrimaryDrawerItem().withName("Help"),
                        new PrimaryDrawerItem().withName("Settings"))
                .withSelectedItem(0)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                })
                .withShowDrawerOnFirstLaunch(true)
                .build();
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        result.getRecyclerView().setVerticalScrollBarEnabled(false);
    }


    void locationChecker(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(resultCallback);
    }

    ResultCallback<LocationSettingsResult> resultCallback = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult result) {
            final Status status = result.getStatus();
            final LocationSettingsStates state = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                MainActivity.this, 1000);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
