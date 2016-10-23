package hackaton.waw.eventserver.controller;

import hackaton.waw.eventserver.model.Event;
import hackaton.waw.eventserver.model.Location;
import hackaton.waw.eventserver.repo.EventRepository;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tomek on 10/23/16.
 */
@RestController
@RequestMapping("events")
public class EventResource {
	
	@Autowired EventRepository eventRepository;
	
    @RequestMapping(value = "/sample", method = RequestMethod.GET)
    public Event getSampleEvent() {
        Event event = new Event();
        event.setName("sample event");
        event.setLocation(new LocationResource().getSampleLocation());
        return event;
    }
    
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void addEvent(@RequestBody Event event) {
    	eventRepository.save(event);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event getEventById(@PathVariable(value="someID") Long id) {
    	return eventRepository.findOne(id);
    }
    
    
}
