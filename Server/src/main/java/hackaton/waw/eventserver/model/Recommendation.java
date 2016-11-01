package hackaton.waw.eventserver.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by tomek on 10/29/16.
 */
@Getter
@Setter
@Entity
@Table(name = "recommendations", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "event_id" }))
public class Recommendation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private Boolean liked;

    private Boolean disliked;

    private Boolean interested;
}
