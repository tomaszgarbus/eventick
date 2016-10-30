package hackaton.waw.eventserver.controller;

import hackaton.waw.eventserver.model.Event;
import hackaton.waw.eventserver.model.Recommendation;
import hackaton.waw.eventserver.repo.EventRepository;
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
    @Autowired EventRepository eventRepository;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Recommendation> getAllRecommendations() {
        return recommendationRepository.findAll();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Recommendation addRecommendation(@RequestBody Recommendation recommendation) {
        recommendationRepository.save(recommendation);
        return recommendation;
    }

    @RequestMapping(value = "/facebook_user/{user_id}", method = RequestMethod.GET)
    public List<Event> getFacebookUserRecommendations(@PathVariable(value="user_id") Long userId) {
        //// TODO: 10/30/16
        return null;
    }

    @RequestMapping(value = "/user/{user_id}", method = RequestMethod.GET)
    public List<Event> getUserRecommendations(@PathVariable(value="user_id") Long userId) {
        //TODO: filter on SQL level for efficiency
        return eventRepository.findAll();
        //return recommendationRepository.findAll().stream().filter(r -> r.getUser().getId() == userId).collect(Collectors.toList());
    }

    @RequestMapping(value = "/like/{recommendation_id}", method = RequestMethod.PUT)
    public Recommendation likeRecommendation(@PathVariable(value="recommendation_id") Long recommendationId) {
        Recommendation persistedRecommendation = recommendationRepository.findOne(recommendationId);
        persistedRecommendation.setLiked(true);
        return persistedRecommendation;
    }

    @RequestMapping(value = "/dislike/{recommendation_id}", method = RequestMethod.PUT)
    public Recommendation dislikeRecommendation(@PathVariable(value="recommendation_id") Long recommendationId) {
        Recommendation persistedRecommendation = recommendationRepository.findOne(recommendationId);
        persistedRecommendation.setDisliked(true);
        return persistedRecommendation;
    }

    @RequestMapping(value = "/interested/{recommendation_id}", method = RequestMethod.PUT)
    public Recommendation interestedInRecommendation(@PathVariable(value="recommendation_id") Long recommendationId) {
        Recommendation persistedRecommendation = recommendationRepository.findOne(recommendationId);
        persistedRecommendation.setInterested(true);
        return persistedRecommendation;
    }
}
