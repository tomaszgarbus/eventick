package hackaton.waw.eventserver;

import hackaton.waw.eventserver.repo.EventRepository;
import hackaton.waw.eventserver.repo.LocationRepository;
import lombok.Getter;
import lombok.Setter;
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

    private String userId;
    private String accessToken;

    @Transactional
    public void crawlUserRecommendedEvents(String userId, String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        ((FacebookTemplate)facebook).setApiVersion("2.8");
        List<Event> events =
                facebook.eventOperations().search("*", new PagingParameters(10, 0, new Long(0), Long.MAX_VALUE));
        for (org.springframework.social.facebook.api.Event fbEvent : events) {
            hackaton.waw.eventserver.model.Event event = hackaton.waw.eventserver.model.Event.fromFacebookEvent(fbEvent);

            //get also cover to event
            JSONObject coverJson = new JSONObject(facebook.fetchObject(fbEvent.getId(), String.class, "cover"));
            if (coverJson.has("cover")) {
                String pictureURL = new JSONObject(facebook.fetchObject(fbEvent.getId(), String.class, "cover")).getJSONObject("cover").getString("source");
                event.setPictureURL(pictureURL);
            }
            if (event.getLocation() == null) {
                continue;
            }
            try {
                locationRepository.save(event.getLocation());
                eventRepository.save(event);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void run() {
        crawlUserRecommendedEvents(userId, accessToken);
    }
}
