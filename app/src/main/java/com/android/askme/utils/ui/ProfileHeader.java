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

package com.android.askme.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.askme.R;


/**
 * Created by Anand Sharmaon 15/10/15.
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
