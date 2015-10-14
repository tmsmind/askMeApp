package humesis.apps.humesisdirectionapp.fragments;

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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.adapters.NearbyAdapter;
import humesis.apps.humesisdirectionapp.placepicker.Place;
import humesis.apps.humesisdirectionapp.placepicker.PlacesService;
import humesis.apps.humesisdirectionapp.utils.LocationManager;

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
                new MaterialDialog.Builder(getContext())
                        .title("Filter Listing")
                        .items(R.array.filter_nearby_values)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                /**
                                 * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected check box to actually be selected.
                                 * See the limited multi choice dialog example in the sample project for details.
                                 **/
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
