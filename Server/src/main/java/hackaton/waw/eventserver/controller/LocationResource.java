package hackaton.waw.eventserver.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import hackaton.waw.eventserver.model.Location;

/**
 * Created by tomek to chuj on 10/23/16.
 */
@RestController
@RequestMapping("locations")
public class LocationResource {
    @RequestMapping(value = "sample", method = RequestMethod.GET)
    public Location getSampleLocation() {
        Location location = new Location();
        location.setName("sample location");
        return location;
    }
}
