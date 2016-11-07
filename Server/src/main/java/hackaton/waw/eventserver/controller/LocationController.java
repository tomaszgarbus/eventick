package hackaton.waw.eventserver.controller;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import hackaton.waw.eventserver.service.GoogleMapsApiService;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tomek on 10/23/16.
 */

@RestController
@Component
@RequestMapping("locations")
public class LocationController {
	
	@Autowired
    LocationRepository locationRepository;

    @Autowired
    GoogleMapsApiService googleMapsApiService;
	
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

    @RequestMapping(value = "/facebook/{facebook_id}", method = RequestMethod.GET)
    public Location getLocationByFacebookId(@PathVariable(value = "facebook_id") String facebookId) {
        List<Location> allLocations = locationRepository.findAll().stream().filter(l -> l.getFacebookId() != null && l.getFacebookId().equals(facebookId)).collect(Collectors.toList());
        if (allLocations.isEmpty()) {
            return null;
        }
        return allLocations.get(0);
    }

    @RequestMapping(value = "/update_lat_lng", method = RequestMethod.PUT)
    public void updateLocationsLatLng() {
        googleMapsApiService.updateLocationsLatLng();
    }
}
