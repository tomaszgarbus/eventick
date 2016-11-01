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
@Table(name = "locations", uniqueConstraints = { @UniqueConstraint(columnNames = {"facebookId"}) })
//@AllArgsConstructor
public class Location {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="id")
    private Long id;

    String facebookId;

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
        location.setFacebookId(place.getId());
        if (place.getLocation() != null) {
            location.setLat(place.getLocation().getLatitude());
            location.setLng(place.getLocation().getLongitude());
        }
        return location;
    }

    public Double distance(Location destination) {
        //http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
        double lat1 = lat;
        double lat2 = destination.getLat();
        double lon1 = lng;
        double lon2 = destination.getLng();

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }
}
