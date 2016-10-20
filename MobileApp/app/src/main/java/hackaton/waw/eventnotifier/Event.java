package hackaton.waw.eventnotifier;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
public class Event {
    private String name;
    private Location location;

    public static List<Event> getSampleEvents() {
        List<Event> ret = new ArrayList<>();
        Event event0 = new Event();
        event0.setName("Party hard");
        event0.setLocation(new Location());
        event0.getLocation().setName("Porxima");
        Event event1 = new Event();
        event1.setName("Party soft");
        event1.setLocation(new Location());
        event1.getLocation().setName("Sopot");
        ret.add(event0);
        ret.add(event1);
        return ret;
    }
}
