package hackaton.waw.eventserver.repo;

import hackaton.waw.eventserver.model.Location;
import hackaton.waw.eventserver.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by tomek on 10/26/16.
 */

import java.util.List;
public interface UserRepository extends CrudRepository<User, Long> {

    void delete(User deleted);

    List<User> findAll();

    User save(User persisted);

}
