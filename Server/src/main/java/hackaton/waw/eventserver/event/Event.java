package hackaton.waw.eventserver.event;

import java.util.Date;

import hackaton.waw.eventserver.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
//@AllArgsConstructor
public class Event {

    private Long id;

    private String name;

    private String description;

    private Location location;

    private String pictureURL;

    private Date date;

}
