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
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.askme.R;


/**
 * Created by Anand Sharmaon 13/10/15.
 */
public class ListItemCard extends LinearLayout {

    String title,subTitle;
    @DrawableRes int icon;
    TextView titleTV,subTitleTV;
    ImageView iconIV;
    boolean showIcon;

    public ListItemCard(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_row_card, this, true);
        View view = inflater.inflate(R.layout.listview_row, this, true);

        iconIV = (ImageView) view.findViewById(R.id.icon);
        titleTV = (TextView) view.findViewById(R.id.name);
        subTitleTV = (TextView) view.findViewById(R.id.description);
    }

    public ListItemCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ListItem, 0, 0);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_row, this, true);

        icon = a.getResourceId(R.styleable.ListItem_li_icon, R.drawable.ic_person);
        title = a.getString(R.styleable.ListItem_li_title);
        subTitle = a.getString(R.styleable.ListItem_li_desc);
        showIcon = a.getBoolean(R.styleable.ListItem_li_show_icon, true);

        iconIV = (ImageView) view.findViewById(R.id.icon);
        titleTV = (TextView) view.findViewById(R.id.name);
        subTitleTV = (TextView) view.findViewById(R.id.description);

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        iconIV.setImageResource(icon);
        titleTV.setText(title);
        subTitleTV.setText(subTitle);
        iconIV.setVisibility(showIcon ? VISIBLE : GONE);
    }

    public void setTitle(String title){
        titleTV.setText(title);
        invalidate();
    }

    public void setSubTitle(String subTitle) {
        subTitleTV.setText(subTitle);
        invalidate();
    }

    public void setIcon(@DrawableRes int icon){
        iconIV.setImageResource(icon);
        invalidate();
    }

    public void showIcon(boolean showIcon){
        iconIV.setVisibility(showIcon ? VISIBLE : GONE);
        invalidate();
    }

    public ImageView getIconIV(){
        return iconIV;
    }

    public void setSingleLine(boolean singleLine){
        titleTV.setSingleLine(singleLine);
        invalidate();
    }

}
