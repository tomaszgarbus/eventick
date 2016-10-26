package hackaton.waw.eventserver.authentication;

import hackaton.waw.eventserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by tomek on 10/26/16.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        if (false) {
            //TODO: actually check username
            throw new BadCredentialsException("Username not found.");
        }

        if (false) {
            //TODO: actually check password
            throw new BadCredentialsException("Wrong password.");
        }

        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

        return new UsernamePasswordAuthenticationToken(new User(), password, authorities);
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }

}