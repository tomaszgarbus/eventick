package hackaton.waw.eventnotifier.event;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private static String formatToTodayOrTomorrow(Date date) {
        String str = new SimpleDateFormat("EEE hh:mma").format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        DateFormat timeFormatter = new SimpleDateFormat("hh:mma");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "Today " + timeFormatter.format(date);
        } else if (calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
            return "Tomorrow " + timeFormatter.format(date);
        } else {
            return str;
        }
    }

    public String getDisplayableDate() {
        return formatToTodayOrTomorrow(date);
    }
}
