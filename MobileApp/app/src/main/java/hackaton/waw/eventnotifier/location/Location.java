package hackaton.waw.eventnotifier.location;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
@DatabaseTable(tableName = "loc")
public class Location {

    @DatabaseField(id = true)
    private Long id;

    @DatabaseField
    String name;

    @DatabaseField()
    Double lat;

    @DatabaseField
    Double lng;

    public Location() {
        name = "";
    }

    public LatLng getLatLng() {
        if (lat != null && lng != null) {
            return new LatLng(lat, lng);
        } else {
            return null;
        }
    }

    public void setLatLng(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        lat = latLng.latitude;
        lng = latLng.longitude;
    }

    public void parseJSON(JSONObject json) {
        try {
            if (json.has("id")) {
                this.setId(json.getLong("id"));
            }
            if (json.has("name")) {
                this.setName(json.getString("name"));
            }
            if (json.has("lat")) {
                this.setLat(json.getDouble("lat"));
            }
            if (json.has("lng")) {
                this.setLng(json.getDouble("lng"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Location fromJSON(JSONObject json) {
        Location location = new Location();
        location.parseJSON(json);
        return location;
    }
}
