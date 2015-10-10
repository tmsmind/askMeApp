package humesis.apps.humesisdirectionapp.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;

import com.google.android.maps.OverlayItem;

import java.util.ArrayList;

/**
 * Created by dhanraj on 10/10/15.
 */
public class MapOverlayItem extends ItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();

    private Context context;

    public MapOverlayItem(Drawable drawable) {
        super(drawable);
    }

    public MapOverlayItem(Drawable drawable, ArrayList<OverlayItem> mapOverlays, Context context) {
        super(drawable);
        this.mapOverlays = mapOverlays;
        this.context = context;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mapOverlays.get(i);
    }

    @Override
    public int size() {
        return mapOverlays.size();
    }

    public void addOverlay(OverlayItem overlay) {
        mapOverlays.add(overlay);
    }

    public void populateNow() {
        this.populate();
    }
}
