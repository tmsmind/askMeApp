/*
 * ==================================================================
 *   Copyright (c) 2015 Anand Sharma (Humesis, Inc.)
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


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.askme.R;
import com.android.askme.models.LocalProfile;
import com.android.askme.preferences.AppPrefs;
import com.android.askme.preferences.SettingsUtil;
import com.android.askme.utils.ui.ProfileHeader;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


/**
 * Created by Anand Sharmaon 09/10/15.
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
        try {
            Picasso.with(getContext()).load(profile.profilePic).placeholder(R.drawable.com_facebook_profile_picture_blank_square).into(profileHeader.getProfilePhotoView());
            Picasso.with(getContext()).load(profile.coverPic).placeholder(R.drawable.header).into(profileHeader.getCoverView());
        }catch (Exception e){
            e.printStackTrace();
            Picasso.with(getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).placeholder(R.drawable.com_facebook_profile_picture_blank_square).into(profileHeader.getProfilePhotoView());
            Picasso.with(getContext()).load(R.drawable.header).placeholder(R.drawable.header).into(profileHeader.getCoverView());
            Toast.makeText(getContext(),"Unable to get Profile Photo",Toast.LENGTH_SHORT).show();
        }
    }
}
