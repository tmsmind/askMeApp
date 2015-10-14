package humesis.apps.humesisdirectionapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.adapters.PlacesAdapter;
import humesis.apps.humesisdirectionapp.models.Event;
import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.utils.LocationManager;
import humesis.apps.humesisdirectionapp.utils.placesapi.Utils;


/**
 * Created by dhanraj on 10/10/15.
 */
public class PlacePickerActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private PlacesAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private EditText searchText;
    private ListView placesListView;
    private ArrayList<AutocompletePrediction> mResultList = new ArrayList<>();
    private CardView pickerCard,placesCard;
    private Button pickLocation, currentLocation;
    private ImageView back;
    Event.EventType notifyType;
    MaterialDialog progressDialog;

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @SuppressLint("LongLogTag")
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Place query did not complete. Error: ", places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.i("Place details received: ", "" + place.getName());
            returnResult(place);

        }
    };
    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @SuppressLint("LongLogTag")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("Autocomplete item selected: ", primaryText.toString());

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


            Log.i("Called getPlaceById to get Place details for ", placeId);
        }
    };


    /**
     * Returns the list of  places nearby based on location.
     */
    private ResultCallback<PlaceLikelihoodBuffer> mUpdateCurrentLocationCallback = new ResultCallback<PlaceLikelihoodBuffer>() {
        @SuppressLint("LongLogTag")
        @Override
        public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if (!likelyPlaces.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Place query did not complete. Error: ", likelyPlaces.getStatus().toString());
                likelyPlaces.release();
                return;
            }

            final Place place = likelyPlaces.get(0).getPlace();
            Log.e("Current Place is likely:", place.getName().toString());
            returnResult(place);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 34995, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        searchText = (EditText) findViewById(R.id.place_picker_search);
        placesListView = (ListView) findViewById(R.id.places_list_view);
        pickerCard = (CardView) findViewById(R.id.picker_card);
        placesCard = (CardView) findViewById(R.id.places_card);
        currentLocation = (Button) findViewById(R.id.button_current_location);
        pickLocation = (Button) findViewById(R.id.button_pick_from_map);
        back = (ImageView)findViewById(R.id.place_picker_back_button);

        try{
            notifyType = EventBus.getDefault().getStickyEvent(Event.class).getEventType();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 2) {
                    pickerCard.setVisibility(View.GONE);
                    placesCard.setVisibility(View.VISIBLE);
                    getAutocomplete(charSequence, Utils.setBounds(LocationManager.getLastKnownLocation(getApplicationContext()), 100));
                }else {
                    pickerCard.setVisibility(View.VISIBLE);
                    placesCard.setVisibility(View.GONE);
                    mResultList = new ArrayList<>();
                    if (mAdapter != null) {
                        mAdapter.updateList(mResultList);
                    } else {
                        mAdapter = new PlacesAdapter(getApplicationContext(), mResultList);
                        placesListView.setAdapter(mAdapter);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        placesListView.setOnItemClickListener(mAutocompleteClickListener);
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new MaterialDialog.Builder(PlacePickerActivity.this)
                        .content("Acquiring current location...")
                        .progress(true, 0)
                        .show();

                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                        .getCurrentPlace(mGoogleApiClient, null);
                result.setResultCallback(mUpdateCurrentLocationCallback);
            }
        });
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(getApplicationContext());
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, AppPrefs.PLACE_PICKER);

                    // Hide the pick option in the UI to prevent users from starting the picker
                    // multiple times.

                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil
                            .getErrorDialog(e.getConnectionStatusCode(), PlacePickerActivity.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getApplicationContext(), "Google Play Services is not available.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppPrefs.PLACE_PICKER:
                if (resultCode == Activity.RESULT_OK) {
                    final Place place = PlacePicker.getPlace(data, getApplicationContext());
                    Log.e("Place Picked", place.getName().toString());
                    returnResult(place);
                }
                break;
            default:
                super.onActivityResult(requestCode,resultCode,data);
                break;
        }
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

    void returnResult(Place place){
        Log.e("Returning:", place.getName().toString());
        Intent result = getIntent();
        setResult(RESULT_OK,result);
        EventBus.getDefault().postSticky(new Event().new PlaceEvent(notifyType, place));
        finish();
    }


    @SuppressLint("LongLogTag")
    private void getAutocomplete(CharSequence constraint,LatLngBounds mBounds) {
        if (mGoogleApiClient.isConnected()) {
            Log.i("Starting autocomplete: ", constraint.toString());

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, null);

            results.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                @Override
                public void onResult(AutocompletePredictionBuffer autocompletePredictions) {
                    final Status status = autocompletePredictions.getStatus();
                    if (!status.isSuccess()) {
                        Toast.makeText(getApplicationContext(), "Error contacting API: " + status.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.e("Error getting prediction API call:", status.toString());
                        autocompletePredictions.release();
                    }

                    Log.i("Query completed. Received ", autocompletePredictions.getCount()
                            + " predictions.");
                    // Freeze the results immutable representation that can be stored safely.
                   mResultList=  DataBufferUtils.freezeAndClose(autocompletePredictions);
                    if (mAdapter != null) {
                        mAdapter.updateList(mResultList);
                    } else {
                        mAdapter = new PlacesAdapter(PlacePickerActivity.this, mResultList);
                        placesListView.setAdapter(mAdapter);
                    }
                }
            });
        }
        Log.e("TAG", "Google API client is not connected for autocomplete query.");
    }
}
