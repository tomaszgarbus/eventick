package hackaton.waw.eventnotifier.event;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hackaton.waw.eventnotifier.location.Location;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
public class Event {
    private String name;
    private String description;
    private Location location;
    private Bitmap picture;
    private Date date;
}
