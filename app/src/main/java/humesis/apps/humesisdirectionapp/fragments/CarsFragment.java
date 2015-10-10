package humesis.apps.humesisdirectionapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import humesis.apps.humesisdirectionapp.R;

/**
 * Created by dhanraj on 09/10/15.
 */
public class CarsFragment extends Fragment {

    public CarsFragment() {
    }

    public static CarsFragment newInstance(){
        return new CarsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car,
                container, false);
        return view;
    }
}
