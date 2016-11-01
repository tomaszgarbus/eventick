package hackaton.waw.eventnotifier.event;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hackaton.waw.eventnotifier.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static hackaton.waw.eventnotifier.event.EventManager.FacebookEventFetcher.bitmapFromCoverSource;

/**
 * Created by tomek on 10/18/16.
 */

@Getter
@Setter
@DatabaseTable(tableName = "event")
public class Event {

    //@DatabaseField(generatedId = true)
    @DatabaseField(id = true)
    private Long id;

    @DatabaseField(uniqueCombo = true)
    private String name;

    @DatabaseField
    private String ticketsUri;

    @DatabaseField(uniqueCombo = true)
    private String description;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Location location;

    @DatabaseField
    private Boolean liked;

    @DatabaseField
    private Boolean disliked;

    @DatabaseField
    private Boolean interested;

    private Bitmap picture;

    @DatabaseField(useGetSet = true, uniqueCombo = true)
    private String pictureURL;
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
        //this.picture = bitmapFromCoverSource(pictureURL);
    }

    @DatabaseField
    private Date date;

    private static String formatToTodayOrTomorrow(Date date, String todayStr, String tomorrowStr) {
        String str = new SimpleDateFormat("EEE HH:mm").format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return todayStr + " " + timeFormatter.format(date);
        } else if (calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
            return tomorrowStr + " " + timeFormatter.format(date);
        } else {
            return str;
        }
    }

    public String getDisplayableDate(String todayStr, String tomorrowStr) {
        return formatToTodayOrTomorrow(date, todayStr, tomorrowStr);
    }

    public static Event fromJSON(JSONObject json) {
        Event event = new Event();
        event.parseJSON(json);
        return event;
    }

    public void parseJSON(JSONObject json) {
        try {
            if (json.has("id")) {
                this.setId(json.getLong("id"));
            }
            if (json.has("name")) {
                this.setName(json.getString("name"));
            }
            if (json.has("description")) {
                this.setDescription(json.getString("description"));
            }
            if (json.has("pictureURL")) {
                String source = json.getString("pictureURL");
                this.setPictureURL(source);
                //this.setPicture(bitmapFromCoverSource(source));
            }
            if (json.has("ticketUri")) {
                this.setTicketsUri(json.getString("ticketUri"));
            }
            if (json.has("date")) {
                Date date = new Date(json.getLong("date"));
                this.setDate(date);
            }
            this.setLocation(Location.fromJSON(json.getJSONObject("location")));
        } catch (JSONException e) {

        }
    }
}
