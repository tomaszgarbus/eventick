package hackaton.waw.eventserver.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.google.maps.model.LatLng;
import hackaton.waw.eventserver.repo.LocationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
@Entity
@Table(name = "events", uniqueConstraints = { @UniqueConstraint(columnNames = {"name", "date"}) })
//@AllArgsConstructor
public class Event {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="id")
    private Long id;

    private String facebookId;

    private String name;

    @Column(length = 10000)
    private String description;

    private String ticketUri;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Recommendation> recommendations;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private String pictureURL;

    private Date date;

    public static Event fromFacebookEvent(org.springframework.social.facebook.api.Event fbEvent) {
        Event event = new Event();
        Location location = Location.fromFacebookPlace(fbEvent.getPlace());
        event.setLocation(location);
        event.setFacebookId(fbEvent.getId());
        event.setName(fbEvent.getName());
        event.setDescription(fbEvent.getDescription());
        event.setDate(fbEvent.getStartTime());
        if (fbEvent.getTicketUri() != null) {
            event.setTicketUri(fbEvent.getTicketUri());
        }
        if (fbEvent.getCover() != null) {
            event.setPictureURL(fbEvent.getCover().getSource());
        }
        return event;
    }

}
