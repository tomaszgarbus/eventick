package hackaton.waw.eventserver.repo;

import hackaton.waw.eventserver.model.Recommendation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by tomek on 10/29/16.
 */
public interface RecommendationRepository extends CrudRepository<Recommendation, Long> {

    void delete(Recommendation deleted);

    List<Recommendation> findAll();

    Recommendation save(Recommendation persisted);

    @Override
    Recommendation findOne(Long aLong);

    @Override
    boolean exists(Long aLong);

    @Override
    Iterable<Recommendation> findAll(Iterable<Long> longs);

    @Override
    long count();

    @Override
    void delete(Long aLong);

    @Override
    void delete(Iterable<? extends Recommendation> entities);

    @Override
    void deleteAll();
}