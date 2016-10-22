package hackaton.waw.eventnotifier.location;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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

    @DatabaseField(generatedId = true)
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
}
