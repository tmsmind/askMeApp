/*
 * ==================================================================
 *   Copyright (c) 2015 Dhanraj Padmashali (Humesis, Inc.)
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

package com.android.askme.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.askme.R;
import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.Collections;
import java.util.List;



/**
 * Created by dhanraj on 10/10/15.
 */
public class PlacesAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<AutocompletePrediction> mSuggestedPlaces;
    private Context mContext;
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    public PlacesAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mSuggestedPlaces = Collections.emptyList();
    }

    public PlacesAdapter(Context context, List<AutocompletePrediction> mSuggestedPlaces) {
        mInflater = LayoutInflater.from(context);
        this.mSuggestedPlaces = mSuggestedPlaces;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mSuggestedPlaces.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return mSuggestedPlaces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        AutocompletePrediction placeInfo = mSuggestedPlaces.get(position);

        if (convertView == null) {
            view = mInflater.inflate(R.layout.listview_row, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.description = (TextView) view.findViewById(R.id.description);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setTextColor(Color.BLACK);
        holder.description.setTextColor(Color.BLACK);
        holder.name.setText(placeInfo.getPrimaryText(STYLE_BOLD));
        holder.description.setText(placeInfo.getSecondaryText(STYLE_BOLD));

        return view;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView name, description;
    }

    public void updateList(List<AutocompletePrediction> list) {
        mSuggestedPlaces = list;
        notifyDataSetChanged();
    }
}
