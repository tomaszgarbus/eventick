package hackaton.waw.eventserver;

import hackaton.waw.eventserver.controller.LocationController;
import hackaton.waw.eventserver.model.Location;
import hackaton.waw.eventserver.repo.EventRepository;
import hackaton.waw.eventserver.repo.LocationRepository;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Event;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by tomek on 10/30/16.
 */
@Component
@Getter
@Setter
public class FacebookEventCrawler implements Runnable {

    @Autowired LocationRepository locationRepository;
    @Autowired EventRepository eventRepository;
    @Autowired LocationController locationController;

    public enum Action {
        CRAWL_USER,
        CRAWL_PLACE;
    };

    private String userId;
    private String accessToken;
    private String placeId;
    private Action action = Action.CRAWL_USER;

    @Transactional
    public void crawlUserRecommendedEvents(String userId, String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        ((FacebookTemplate)facebook).setApiVersion("2.8");
        List<Event> events =
                facebook.eventOperations().search("*", new PagingParameters(100, 0, new Long(0), Long.MAX_VALUE));
        for (org.springframework.social.facebook.api.Event fbEvent : events) {
            hackaton.waw.eventserver.model.Event event = hackaton.waw.eventserver.model.Event.fromFacebookEvent(fbEvent);

            //get also cover to event
            JSONObject extraFieldsJson = new JSONObject(facebook.fetchObject(fbEvent.getId(), String.class, "cover", "ticket_uri"));
            if (extraFieldsJson.has("cover")) {
                String pictureURL = new JSONObject(facebook.fetchObject(fbEvent.getId(), String.class, "cover")).getJSONObject("cover").getString("source");
                event.setPictureURL(pictureURL);
            }
            if (extraFieldsJson.has("ticket_uri")) {
                String ticketsUri = extraFieldsJson.getString("ticket_uri");
                event.setTicketUri(ticketsUri);
            }
            if (event.getLocation() == null) {
                continue;
            }
            try {
                Location foundLocation = locationController.getLocationByFacebookId(event.getLocation().getFacebookId());
                if (foundLocation != null) {
                    event.setLocation(foundLocation);
                }
                locationRepository.save(event.getLocation());
                eventRepository.save(event);
            } catch (Exception e) {

            }
        }
    }

    @Transactional
    public void crawlPlaceEvents(String placeId) {
        Facebook facebook = new FacebookTemplate(accessToken);
        ((FacebookTemplate)facebook).setApiVersion("2.8");
        JSONArray jsonArray = new JSONObject(facebook.fetchObject(placeId + "/events?limit=1000", String.class)).getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            Event fbEvent = facebook.fetchObject(jsonArray.getJSONObject(i).getString("id"), Event.class);

            hackaton.waw.eventserver.model.Event event = hackaton.waw.eventserver.model.Event.fromFacebookEvent(fbEvent);

            //get also cover to event
            JSONObject extraFieldsJson = new JSONObject(facebook.fetchObject(fbEvent.getId(), String.class, "cover", "ticket_uri"));
            if (extraFieldsJson.has("cover")) {
                String pictureURL = new JSONObject(facebook.fetchObject(fbEvent.getId(), String.class, "cover")).getJSONObject("cover").getString("source");
                event.setPictureURL(pictureURL);
            }
            if (extraFieldsJson.has("ticket_uri")) {
                String ticketsUri = extraFieldsJson.getString("ticket_uri");
                event.setTicketUri(ticketsUri);
            }
            if (event.getLocation() == null) {
                continue;
            }
            try {
                Location foundLocation = locationController.getLocationByFacebookId(event.getLocation().getFacebookId());
                if (foundLocation != null) {
                    event.setLocation(foundLocation);
                }
                locationRepository.save(event.getLocation());
                eventRepository.save(event);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void run() {
        switch (action) {
            case CRAWL_PLACE:
                crawlPlaceEvents(placeId);
                break;
            case CRAWL_USER:
                crawlUserRecommendedEvents(userId, accessToken);
                break;
        }
    }
}
