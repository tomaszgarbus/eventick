package hackaton.waw.eventserver.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
//@AllArgsConstructor
public class Location {

    private Long id;

    String name;

    Double lat;

    Double lng;

    public Location() {
        name = "";
    }
}
