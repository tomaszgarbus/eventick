package hackaton.waw.eventserver.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by tomek on 10/26/16.
 */
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = {"facebookId"})})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    Long id;

    private String accessToken;

    private String facebookId;

    private Double lastLatitude;

    private Double lastLongitude;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Recommendation> recommendations;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
