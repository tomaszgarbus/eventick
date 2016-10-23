package hackaton.waw.eventserver.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hackaton.waw.eventserver.model.Event;

public interface EventRepository extends CrudRepository<Event, Long>{

	void delete(Event deleted);
	 
    List<Event> findAll();
    
    Event save(Event persisted);
    
}
