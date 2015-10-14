package humesis.apps.humesisdirectionapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import humesis.apps.humesisdirectionapp.R;
import humesis.apps.humesisdirectionapp.placepicker.Place;

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

        holder.name.setText(placeInfo.getPrimaryText(null));
        holder.description.setText(placeInfo.getSecondaryText(null));

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
