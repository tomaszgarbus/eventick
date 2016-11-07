package hackaton.waw.eventserver.controller;

import com.google.maps.model.LatLng;
import hackaton.waw.eventserver.model.Event;
import hackaton.waw.eventserver.model.Recommendation;
import hackaton.waw.eventserver.model.User;
import hackaton.waw.eventserver.repo.EventRepository;
import hackaton.waw.eventserver.repo.RecommendationRepository;
import hackaton.waw.eventserver.repo.UserRepository;
import org.apache.commons.lang.time.DateUtils;
import org.apache.lucene.search.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
    @Autowired UserRepository userRepository;

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
    public List<Event> getFacebookUserRecommendations(@PathVariable(value="user_id") String userId) {
        //// TODO: 10/30/16
        return recommendationRepository.findAll().stream().filter(r -> r.getUser().getFacebookId() == userId).map(r -> r.getEvent()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/user/{user_id}", method = RequestMethod.GET)
    public List<Event> getUserRecommendations(@PathVariable(value="user_id") Long userId) {
        //TODO: filter on SQL level for efficiency
        //return eventRepository.findAll().subList(1, 10);
        return recommendationRepository.findAll().stream().filter(r -> r.getUser().getId() == userId).map(r -> r.getEvent()).collect(Collectors.toList());
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

    @RequestMapping(value = "like_event/{event_id}/as_user/{user_id}", method = RequestMethod.PUT)
    public Recommendation likeEvent(@PathVariable(value="event_id") Long eventId, @PathVariable(value = "user_id") Long userId) {
        Long recommendationId = recommendationRepository.findAll().stream()
                .filter(r -> r.getUser().getId() == userId && r.getEvent().getId() == eventId)
                .findFirst().get().getId();
        return likeRecommendation(recommendationId);
    }

    @RequestMapping(value = "dislike_event/{event_id}/as_user/{user_id}", method = RequestMethod.PUT)
    public Recommendation dislikeEvent(@PathVariable(value="event_id") Long eventId, @PathVariable(value = "user_id") Long userId) {
        Long recommendationId = recommendationRepository.findAll().stream()
                .filter(r -> r.getUser().getId() == userId && r.getEvent().getId() == eventId)
                .findFirst().get().getId();
        return dislikeRecommendation(recommendationId);
    }

    @RequestMapping(value = "interested_in_event/{event_id}/as_user/{user_id}", method = RequestMethod.PUT)
    public Recommendation interestedInEvent(@PathVariable(value="event_id") Long eventId, @PathVariable(value = "user_id") Long userId) {
        Long recommendationId = recommendationRepository.findAll().stream()
                .filter(r -> r.getUser().getId() == userId && r.getEvent().getId() == eventId)
                .findFirst().get().getId();
        return interestedInRecommendation(recommendationId);
    }

    @RequestMapping(value = "generate/{user_id}", method = RequestMethod.PUT)
    public void generateRecommendationsForUser(@PathVariable(value = "user_id") Long userId) {
        User user = userRepository.findOne(userId);
        List<Event> events = eventRepository.findAll();
        for (Event event : events) {

            //ignore if event already happened
            if (event.getDate().before(new Date())) {
                continue;
            }

            //ignore if event is more than week from now
            if (event.getDate().after(DateUtils.addDays(new Date(), 7))) {
                continue;
            }

            LatLng userLatLng = user.getLastLatLng();
            LatLng eventLatLng = event.getLocation().getLatLng();
            if (userLatLng != null && eventLatLng != null) {
                Double distance = event.getLocation().distance(userLatLng);
                if (distance < 20000.) {
                    Recommendation recommendation = new Recommendation();
                    recommendation.setEvent(event);
                    recommendation.setUser(user);
                    recommendationRepository.save(recommendation);
                }
            }
        }
    }

    @RequestMapping(value = "generate", method = RequestMethod.PUT)
    public void generateRecommendations() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            generateRecommendationsForUser(user.getId());
        }
    }

}
