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

    @DatabaseField
    double lat;

    @DatabaseField
    double lng;

    public Location() {
        name = "";
    }
}
