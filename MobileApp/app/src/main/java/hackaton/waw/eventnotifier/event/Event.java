package hackaton.waw.eventnotifier.event;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hackaton.waw.eventnotifier.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
@AllArgsConstructor
@DatabaseTable(tableName = "event")
public class Event {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Location location;

    private Bitmap picture;

    @DatabaseField(useGetSet = true)
    private String pictureURL;
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
        this.picture = EventManager.FacebookEventFetcher.bitmapFromCoverSource(pictureURL);

    }


    @DatabaseField
    private Date date;

}
