package com.android.askme.activity;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.askme.R;
import com.android.askme.adapters.ViewPagerAdapter;
import com.android.askme.fragments.CarsFragment;
import com.android.askme.fragments.MapFragment;
import com.android.askme.fragments.NavigationFragment;
import com.android.askme.fragments.PlacesFragment;
import com.android.askme.fragments.ProfileFragment;
import com.android.askme.models.LocalProfile;
import com.android.askme.preferences.AppPrefs;
import com.android.askme.preferences.SettingsUtil;
import com.android.askme.utils.ui.MyViewPager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


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

        com.android.askme.utils.LocationManager.locationChecker(mGoogleApiClient,MainActivity.this);
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
        fList.add(ProfileFragment.newInstance());
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fList);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(mAdapter); //binding the fragments to the view

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

        ImageView view = headerResult.getHeaderBackgroundView();
        Picasso.with(view.getContext()).load(localProfile.coverPic).placeholder(R.drawable.header).into(view);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Maps"),
                        new PrimaryDrawerItem().withName("Select Car"),
                        new PrimaryDrawerItem().withName("Get Directions"),
                        new PrimaryDrawerItem().withName("Places Nearby"),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Help"),
                        new PrimaryDrawerItem().withName("Settings"))
                .withSelectedItem(0)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        try{
                            tabLayout.getTabAt(position - 1).select();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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

    void updateFragmentAndTitle(int position){
        if (viewPager.getCurrentItem() != position) {
            viewPager.setCurrentItem(position, true);
            switch (position){
                case 0: getSupportActionBar().setTitle("Maps");
                    break;
                case 1: getSupportActionBar().setTitle("Select Car");
                    break;
                case 2: getSupportActionBar().setTitle("Get Direction");
                    break;
                case 3: getSupportActionBar().setTitle("Nearby Places");
                    break;
                case 4: getSupportActionBar().setTitle("Profile");
                    break;
            }
        }
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
            updateFragmentAndTitle(position);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
