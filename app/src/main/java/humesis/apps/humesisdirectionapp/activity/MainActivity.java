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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
public class MainActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    Toolbar toolbar;
    FragmentManager fm;
    FragmentTransaction ft;
    private Drawer result = null;

    MyViewPager viewPager;
    List<Fragment> fList = new ArrayList<>();
    ViewPagerAdapter mAdapter;
    LocalProfile localProfile = new LocalProfile();
    private GoogleApiClient mGoogleApiClient;
    TabLayout tabLayout;

    private int[] imageResId = {
            R.drawable.ic_map,
            R.drawable.ic_car,
            R.drawable.ic_navigation,
            R.drawable.ic_view_list,
            R.drawable.ic_person
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
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
        humesis.apps.humesisdirectionapp.utils.LocationManager.locationChecker(mGoogleApiClient,MainActivity.this);
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

    void setupViews(){
        viewPager = (MyViewPager) findViewById(R.id.viewPager);
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
        for (int i = 0; i < 5; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(imageResId[i]));
        }
        viewPager.addOnPageChangeListener(new MyPageScrollListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new MyOnTabSelectedListener());
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class MyPageScrollListener implements ViewPager.OnPageChangeListener {
        private TabLayout mTabLayout;

        public MyPageScrollListener(TabLayout tabLayout) {
            this.mTabLayout = tabLayout;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            if (viewPager.getCurrentItem() != position) {
                viewPager.setCurrentItem(position, true);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
