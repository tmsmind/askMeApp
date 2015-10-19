package com.android.askme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.askme.R;
import com.android.askme.models.SavedCars;

import java.util.Collections;
import java.util.List;

/**
 * Created by dhanraj on 10/10/15.
 */
public class CarsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SavedCars> mCars;
    private Context mContext;


    public CarsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mCars = Collections.emptyList();
    }

    public CarsAdapter(Context context, List<SavedCars> mCars) {
        mInflater = LayoutInflater.from(context);
        this.mCars = mCars;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCars.size();
    }

    @Override
    public Object getItem(int position) {
        return mCars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        SavedCars carsInfo = mCars.get(position);

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

        holder.name.setText(carsInfo.getCarMake());
        holder.description.setText(carsInfo.getCarModel());
        holder.icon.setImageResource(R.drawable.ic_car_48dp);

        return view;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView name, description;
    }

    public void updateList(List<SavedCars> list) {
        mCars = list;
        notifyDataSetChanged();
    }
}
