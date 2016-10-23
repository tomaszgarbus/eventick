package hackaton.waw.eventserver.event;

import hackaton.waw.eventserver.location.Location;
import hackaton.waw.eventserver.location.LocationResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpMethod.GET;

/**
 * Created by tomek on 10/23/16.
 */
@RestController
@RequestMapping("events")
public class EventResource {
    @RequestMapping(value = "/sample", method = RequestMethod.GET)

    public Event getSampleEvent() {
        Event event = new Event();
        event.setName("sample event");
        event.setLocation(new LocationResource().getSampleLocation());
        return event;
    }
}
