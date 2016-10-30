package hackaton.waw.eventserver.authentication;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import hackaton.waw.eventserver.FacebookEventCrawler;
import hackaton.waw.eventserver.controller.EventController;
import hackaton.waw.eventserver.model.User;
import hackaton.waw.eventserver.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by tomek on 10/26/16.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    EventController eventController;

    @Autowired
    EventService eventService;

    private boolean verifyAccessToken(String userId, String accessToken) {
        Version apiVersion = Version.VERSION_2_8;
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, apiVersion);
        com.restfb.types.User object = (com.restfb.types.User) facebookClient.fetchObject("me", com.restfb.types.User.class);
        if (!object.getId().equals(userId)) {
            return false;
        }

        //Crawl events in the meantime
        try {
            eventService.crawlFacebookEvents(userId, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //TODO: consider using class extending Authentication
        String userId = authentication.getName();
        String accessToken = (String) authentication.getCredentials();

        if (!verifyAccessToken(userId, accessToken)) {
            throw new BadCredentialsException("access token not working");
        }

        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

        return new UsernamePasswordAuthenticationToken(new User(), accessToken, authorities);
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }

}