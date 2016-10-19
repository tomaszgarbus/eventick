package hackaton.waw.eventnotifier;

import com.google.android.gms.maps.model.LatLng;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
public class Location {
    String name;
    LatLng latLng;
}
