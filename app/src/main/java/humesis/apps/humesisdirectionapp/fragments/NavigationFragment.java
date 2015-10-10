package humesis.apps.humesisdirectionapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.w3c.dom.Document;

import java.util.List;

import de.greenrobot.event.EventBus;
import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.activity.MainActivity;
import humesis.apps.humesisdirectionapp.activity.PlacePickerActivity;
import humesis.apps.humesisdirectionapp.adapters.PlaceAutocompleteAdapter;
import humesis.apps.humesisdirectionapp.models.NotifyEvent;
import humesis.apps.humesisdirectionapp.models.RouteInfo;
import humesis.apps.humesisdirectionapp.placepicker.PlacesService;
import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.utils.GoogleDirection;
import humesis.apps.humesisdirectionapp.utils.placesapi.Utils;



/**
 * Created by dhanraj on 08/10/15.
 */
public class NavigationFragment extends Fragment implements View.OnClickListener, GoogleDirection.OnDirectionResponseListener {
    TextView sourceText,sourceDesc, destinationText,destDesc,routeInfo;
    Button srcSetCurrentLocation,srcPickLocation,destPickLocation;
    boolean srcPicked,destPicked = false;
    Place source,dest;
    private GoogleDirection googleDirection;
    CardView routeInfoCard;

    public NavigationFragment() {
    }

    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        EventBus.getDefault().register(this);
        googleDirection = new GoogleDirection(getContext());
        googleDirection.setOnDirectionResponseListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Navigation Fragment", "onDestroy");
        EventBus.getDefault().unregister(this);
    }


    public void onEvent(NotifyEvent event){
        Log.e("Event", event.getClip());
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation,
                container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sourceText = (TextView) view.findViewById(R.id.starting_point);
        sourceDesc = (TextView) view.findViewById(R.id.starting_point_description);
        destinationText = (TextView) view.findViewById(R.id.ending_point);
        destDesc = (TextView) view.findViewById(R.id.ending_point_description);
        routeInfo = (TextView) view.findViewById(R.id.route_info_text);
        routeInfoCard = (CardView) view.findViewById(R.id.route_info_card);

        srcSetCurrentLocation = (Button) view.findViewById(R.id.src_button_current_location);
        srcPickLocation = (Button) view.findViewById(R.id.src_button_pick_from_map);
        destPickLocation = (Button) view.findViewById(R.id.dest_button_pick_from_map);

        srcSetCurrentLocation.setOnClickListener(this);
        srcPickLocation.setOnClickListener(this);
        destPickLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.src_button_current_location:
                sourceText.setText("My Location");
                srcPicked = true;
                break;

            case R.id.src_button_pick_from_map:
                openPlacePicker(AppPrefs.SOURCE_PICKED);
                break;

            case R.id.dest_button_pick_from_map:
                openPlacePicker(AppPrefs.DEST_PICKED);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("NavFrg requestCode", "" + requestCode);
        Log.e("NavFrg AppPrefs", "" + AppPrefs.PLACE_PICKER);
        switch (requestCode) {
            case AppPrefs.SOURCE_PICKED:
                if (resultCode == Activity.RESULT_OK) {
                    final Place place = PlacePicker.getPlace(data, getContext());
                    source = place;
                    final CharSequence name = place.getName();
                    sourceText.setText(name);
                    sourceDesc.setText(place.getAddress());
                    Log.e("Source Is",name.toString());
                    srcPicked = true;
                    getRouteInfo();
                }
                break;
            case AppPrefs.DEST_PICKED:
                if (resultCode == Activity.RESULT_OK) {
                    final Place place = PlacePicker.getPlace(data, getContext());
                    dest = place;
                    final CharSequence name = place.getName();
                    destinationText.setText(name);
                    destDesc.setText(place.getAddress());
                    Log.e("Source Is", name.toString());
                    destPicked = true;
                    getRouteInfo();
                }
                break;
        }
    }

    void getRouteInfo(){
        if(srcPicked && destPicked){
            googleDirection.request(source.getLatLng(),dest.getLatLng(),GoogleDirection.MODE_DRIVING);
        }
    }

    public void openPlacePicker(int code) {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getContext());
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, code);

            // Hide the pick option in the UI to prevent users from starting the picker
            // multiple times.


        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(getContext(), "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
//        Intent intent = new Intent(getContext(), PlacePickerActivity.class);
//        startActivityForResult(intent, AppPrefs.PLACE_PICKER);
    }

    @Override
    public void onResponse(String status, Document doc, GoogleDirection gd) {
        routeInfoCard.setVisibility(View.VISIBLE);
        routeInfo.setText(gd.getTotalDurationText(doc)+" ("+gd.getTotalDistanceText(doc)+")");
    }
}
