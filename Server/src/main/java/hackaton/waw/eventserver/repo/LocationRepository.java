package hackaton.waw.eventserver.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import hackaton.waw.eventserver.model.Event;
import hackaton.waw.eventserver.model.Location;

public interface LocationRepository extends CrudRepository<Location, Long>{

	void delete(Location deleted);
	 
    List<Location> findAll();
    
    Location save(Location persisted);
}
