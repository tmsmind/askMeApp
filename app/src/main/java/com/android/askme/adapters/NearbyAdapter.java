package com.android.askme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.askme.R;
import com.android.askme.placepicker.Place;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by dhanraj on 10/10/15.
 */
public class NearbyAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Place> mSuggestedPlaces;
    private Context mContext;


    public NearbyAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mSuggestedPlaces = Collections.emptyList();
    }

    public NearbyAdapter(Context context, List<Place> mSuggestedPlaces) {
        mInflater = LayoutInflater.from(context);
        this.mSuggestedPlaces = mSuggestedPlaces;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mSuggestedPlaces.size();
    }

    @Override
    public Object getItem(int position) {
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
        Place placeInfo = mSuggestedPlaces.get(position);

        if (convertView == null) {
            view = mInflater.inflate(R.layout.listview_row_card, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.description = (TextView) view.findViewById(R.id.description);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(placeInfo.getName());
        holder.description.setText(placeInfo.getVicinity());
        Picasso.with(mContext).load(placeInfo.getIcon()).into(holder.icon);

        return view;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView name, description;
    }

    public void updateList(List<Place> list) {
        mSuggestedPlaces = list;
        notifyDataSetChanged();
    }
}
