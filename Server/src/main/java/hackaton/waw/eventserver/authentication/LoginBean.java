package hackaton.waw.eventserver.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.social.facebook.api.Facebook;

/**
 * Created by tomek on 10/26/16.
 */
@Getter
@Setter
public class LoginBean  {
    private String facebookToken;

}
