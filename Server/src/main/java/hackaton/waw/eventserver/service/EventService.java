package hackaton.waw.eventserver.service;

import hackaton.waw.eventserver.FacebookEventCrawler;
import hackaton.waw.eventserver.repo.EventRepository;
import hackaton.waw.eventserver.repo.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by tomek on 10/30/16.
 */

@Service
@Component
public class EventService {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired FacebookEventCrawler crawler;

    @Transactional(propagation = Propagation.REQUIRED)
    public void crawlFacebookEvents(String userId, String accessToken) {
        crawler.setUserId(userId);
        crawler.setAccessToken(accessToken);
        new Thread(crawler).start();
    }
}
