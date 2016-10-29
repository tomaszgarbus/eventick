package hackaton.waw.eventserver.controller;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import hackaton.waw.eventserver.model.Event;
import hackaton.waw.eventserver.repo.EventRepository;

import hackaton.waw.eventserver.repo.LocationRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tomek on 10/23/16.
 */
@RestController
@Component
@RequestMapping("events")
public class EventController {
	
	@Autowired EventRepository eventRepository;
    @Autowired LocationRepository locationRepository;
	
    @RequestMapping(value = "/sample", method = RequestMethod.GET)
    public Event getSampleEvent() {
        Event event = new Event();
        event.setName("sample event");
        event.setDescription("test description of test event");
        event.setLocation(new LocationController().getSampleLocation());
        return event;
    }
    
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Event> getAllEvents() {
    	return eventRepository.findAll();
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Event addEvent(@RequestBody Event event) {
    	eventRepository.save(event);
    	return event;
    }
    
    @RequestMapping(value = "/{event_id}", method = RequestMethod.GET)
    public Event getEventById(@PathVariable(value="event_id") Long id) {
    	return eventRepository.findOne(id);
    }

    @RequestMapping(value = "/recommended", method = RequestMethod.GET)
    public List<Event> getRecommendedEvents() {
        return eventRepository.findAll();
    }
    
    public void crawlUserRecommendedEvents(String userId, String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        List<org.springframework.social.facebook.api.Event> events =
                facebook.eventOperations().search("*", new PagingParameters(2, 0, new Long(0), Long.MAX_VALUE));
        for (org.springframework.social.facebook.api.Event fbEvent : events) {
            Event event = Event.fromFacebookEvent(fbEvent);
            facebook.eventOperations().getEventImage(event.getFacebookId());
            locationRepository.save(event.getLocation());
            eventRepository.save(event);
        }
    }
}
