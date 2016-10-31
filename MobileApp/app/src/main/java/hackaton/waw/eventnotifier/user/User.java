package hackaton.waw.eventnotifier.user;

import com.google.android.gms.maps.model.LatLng;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/30/16.
 */

@Getter
@Setter
public class User {
    private Long id;

    private String facebookId;

    private String name;

    private Double latitude;

    private Double longitude;
}
