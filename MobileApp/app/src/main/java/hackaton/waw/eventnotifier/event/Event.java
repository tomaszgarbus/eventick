package hackaton.waw.eventnotifier.event;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
@DatabaseTable(tableName = "event")
public class Event {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField(foreign = true)
    private Location location;

    @DatabaseField
    private String picture;

    @DatabaseField
    private Date date;
}
