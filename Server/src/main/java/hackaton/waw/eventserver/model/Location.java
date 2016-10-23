package hackaton.waw.eventserver.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
@Entity
@Table(name = "locations")
//@AllArgsConstructor
public class Location {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="id")
    private Long id;

    String name;

    Double lat;

    Double lng;

    public Location() {
        name = "";
    }
}
