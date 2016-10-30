package hackaton.waw.eventserver.model;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.social.facebook.api.Place;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
@Entity
//@Table(name = "locations", uniqueConstraints = { @UniqueConstraint(columnNames = {"name"}), @UniqueConstraint(columnNames = {"lat", "lng"}) })
//@AllArgsConstructor
public class Location {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="id")
    private Long id;

    String name;

    Double lat;

    Double lng;

    public Location() {
        name = "";
    }

    public static Location fromFacebookPlace(Place place) {
        if (place == null) {
            return null;
        }
        Location location = new Location();
        location.setName(place.getName());
        if (place.getLocation() != null) {
            location.setLat(place.getLocation().getLatitude());
            location.setLng(place.getLocation().getLongitude());
        }
        return location;
    }
}
