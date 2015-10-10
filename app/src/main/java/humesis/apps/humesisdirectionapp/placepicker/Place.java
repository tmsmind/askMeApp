package humesis.apps.humesisdirectionapp.placepicker;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhanraj on 10/10/15.
 */
public class Place {
    private String id;
    private String icon;
    private Bitmap bitmapIcon;

    public Bitmap getBitmapIcon() {
        return bitmapIcon;
    }

    public void setBitmapIcon(Bitmap bitmapIcon) {
        this.bitmapIcon = bitmapIcon;
    }

    private String name;
    private String vicinity;
    private Double latitude;
    private Double longitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    static Place jsonToPlace(JSONObject json) {
        try {
            Place result = new Place();
            JSONObject geometry = (JSONObject) json.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(json.getString("icon"));
            result.setName(json.getString("name"));
            result.setVicinity(json.getString("vicinity"));
            result.setId(json.getString("id"));
            return result;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }
}

