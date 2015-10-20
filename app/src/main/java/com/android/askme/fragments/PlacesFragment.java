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

package com.android.askme.fragments;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.askme.R;
import com.android.askme.adapters.NearbyAdapter;
import com.android.askme.placepicker.Place;
import com.android.askme.placepicker.PlacesService;
import com.android.askme.preferences.SettingsUtil;
import com.android.askme.utils.LocationManager;

import java.util.ArrayList;

/**
 * Created by dhanraj on 09/10/15.
 */
public class PlacesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    Location mCurrentLocation;
    ListView placesList;
    NearbyAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public PlacesFragment() {
    }

    public static PlacesFragment newInstance() {
        return new PlacesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentLocation = LocationManager.getLastKnownLocation(getContext());
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places,
                container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        placesList = (ListView) view.findViewById(R.id.suggestions_list_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mAdapter = new NearbyAdapter(getContext());
        placesList.setAdapter(mAdapter);
        if(mCurrentLocation!=null)
        new GetPlacesAsync(getContext(),"",mCurrentLocation).execute();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetPlacesAsync(getContext(), "", mCurrentLocation).execute();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.nearby_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filter:
                Integer[] values = SettingsUtil.get(getContext(),"place_filter", (Integer[]) null);

                new MaterialDialog.Builder(getContext())
                        .title("Place Type")
                        .items(R.array.filter_nearby_name)
                        .itemsCallbackMultiChoice(values, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                                StringBuilder places = new StringBuilder();
                                String[] values = getResources().getStringArray(R
                                        .array.filter_nearby_values);
                                for (int id : which) {
                                    places.append(values[id]).append("|");
                                }
                                new GetPlacesAsync(getContext(), places.toString(),
                                        mCurrentLocation)
                                        .execute();
                                SettingsUtil.set(getContext(), "place_filter", which);
                                return true;
                            }
                        })
                        .positiveText("Done")
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... voids) {
            PlacesService service = new PlacesService(context.getResources().getString(R.string.places_web_key));
            ArrayList<Place> findPlaces = service.nearbyPlaces(location.getLatitude(),
                    location.getLongitude(), places);
            Log.e("Nearby Places ::", places);
            return findPlaces;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.updateList(result);
        }
    }
}
