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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
        new GetPlacesAsync(getContext(),"",mCurrentLocation).execute();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetPlacesAsync(getContext(), "", mCurrentLocation).execute();
            }
        });
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
