package hackaton.waw.eventserver.controller;

import hackaton.waw.eventserver.model.Event;
import hackaton.waw.eventserver.model.Recommendation;
import hackaton.waw.eventserver.repo.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
