package hackaton.waw.eventserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.annotation.Secured;

import hackaton.waw.eventserver.model.Event;
import hackaton.waw.eventserver.model.Location;
import hackaton.waw.eventserver.repo.EventRepository;
import hackaton.waw.eventserver.repo.LocationRepository;

/**
 * Created by tomek to chuj on 10/23/16.
 */

@RestController
@Component
@RequestMapping("locations")
public class LocationController {
	
	@Autowired
    LocationRepository locationRepository;
	
    @RequestMapping(value = "sample", method = RequestMethod.GET)
    public Location getSampleLocation() {
        Location location = new Location();
        location.setName("sample location");
        return location;
    }     
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Location addLocation(@RequestBody Location location) {
    	locationRepository.save(location);
    	return location;
    }
    
    @RequestMapping(value = "/{someID}", method = RequestMethod.GET)
    public Location getLocationById(@PathVariable(value="someID") Long id) {
    	return locationRepository.findOne(id);
    }
}
