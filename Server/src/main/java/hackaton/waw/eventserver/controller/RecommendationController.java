package hackaton.waw.eventserver.controller;

import hackaton.waw.eventserver.model.Event;
import hackaton.waw.eventserver.model.Recommendation;
import hackaton.waw.eventserver.repo.RecommendationRepository;
import org.apache.lucene.search.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tomek on 10/29/16.
 */
@RestController
@Component
@RequestMapping("recommendations")
public class RecommendationController {

    @Autowired RecommendationRepository recommendationRepository;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Recommendation> getAllRecommendations() {
        return recommendationRepository.findAll();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Recommendation addRecommendation(@RequestBody Recommendation recommendation) {
        recommendationRepository.save(recommendation);
        return recommendation;
    }

    @RequestMapping(value = "/{user_id}", method = RequestMethod.GET)
    public List<Recommendation> getUserRecommendations(@PathVariable(value="user_id") Long userId) {
        //TODO: filter on SQL level for efficiency
        return recommendationRepository.findAll().stream().filter(r -> r.getUser().getId() == userId).collect(Collectors.toList());
    }
}
