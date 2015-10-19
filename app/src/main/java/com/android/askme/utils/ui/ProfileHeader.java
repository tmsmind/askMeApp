package com.android.askme.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.askme.R;


/**
 * Created by dhanraj on 15/10/15.
 */
public class ProfileHeader extends LinearLayout {

    ImageView coverIV,profileIV;
    String name,email;
    ListItem profileInfo;

    public ProfileHeader(Context context) {
        super(context);
    }

    public ProfileHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ListItem, 0, 0);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.profile_header, this, true);

        name = a.getString(R.styleable.ListItem_li_title);
        email = a.getString(R.styleable.ListItem_li_desc);

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        coverIV = (ImageView) findViewById(R.id.profile_background);
        profileIV = (ImageView) findViewById(R.id.profile_icon);
        profileInfo = (ListItem) findViewById(R.id.userName);

        profileInfo.setTitle(name);
        profileInfo.setSubTitle(email);
    }

    public ImageView getCoverView() {
        return coverIV;
    }

    public ImageView getProfilePhotoView() {
        return profileIV;
    }

    public void setName(String name) {
        this.name = name;
        profileInfo.setTitle(name);
        invalidate();
    }

    public void setEmail(String email) {
        this.email = email;
        profileInfo.setSubTitle(email);
        invalidate();
    }
}
