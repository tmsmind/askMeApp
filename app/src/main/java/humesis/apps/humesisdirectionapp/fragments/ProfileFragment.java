package humesis.apps.humesisdirectionapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.models.LocalProfile;
import humesis.apps.humesisdirectionapp.preferences.AppPrefs;
import humesis.apps.humesisdirectionapp.preferences.SettingsUtil;
import humesis.apps.humesisdirectionapp.utils.ui.ProfileHeader;


/**
 * Created by dhanraj on 09/10/15.
 */
public class ProfileFragment extends Fragment {

    ProfileHeader profileHeader;
    LocalProfile profile;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,
                container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileHeader = (ProfileHeader) view.findViewById(R.id.profile_header);

        String user = SettingsUtil.get(getContext(), AppPrefs.USER_KEY, "");

        profile = new Gson().fromJson(user, LocalProfile.class);

        profileHeader.setName(profile.name);
        profileHeader.setEmail(profile.email);
        Picasso.with(getContext()).load(profile.profilePic).placeholder(R.drawable.com_facebook_profile_picture_blank_square).into(profileHeader.getProfilePhotoView());
        Picasso.with(getContext()).load(profile.coverPic).placeholder(R.drawable.header).into(profileHeader.getCoverView());
    }
}
