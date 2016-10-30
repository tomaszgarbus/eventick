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

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(uniqueCombo = true)
    private String name;

    @DatabaseField(uniqueCombo = true)
    private String description;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Location location;

    private Bitmap picture;

    @DatabaseField(useGetSet = true, uniqueCombo = true)
    private String pictureURL;
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
        this.picture = bitmapFromCoverSource(pictureURL);

    }

    @DatabaseField
    private Date date;

    private static String formatToTodayOrTomorrow(Date date) {
        String str = new SimpleDateFormat("EEE HH:mm").format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

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

    public static Event fromJSON(JSONObject json) {
        Event event = new Event();
        event.parseJSON(json);
        return event;
    }

    public void parseJSON(JSONObject json) {
        this.setLocation(new Location());
        try {
            if (json.has("name")) {
                this.setName(json.getString("name"));
            }
            if (json.has("description")) {
                this.setDescription(json.getString("description"));
            }
            if (json.has("pictureURL")) {
                String source = json.getString("pictureURL");
                this.setPictureURL(source);
                this.setPicture(bitmapFromCoverSource(source));
            }
            if (json.has("date")) {
                Date date = new Date(json.getLong("date"));
                this.setDate(date);
                // Server sends hopefully date as long
                /*String str = json.getString("start_time");
                System.out.println(str);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                try {
                    Date date = df.parse(str);
                    this.setDate(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
            }
            if (json.has("location")) {
                this.getLocation().setName(json.getJSONObject("location").getString("name"));
                if (json.getJSONObject("location").has("lat") &&
                        json.getJSONObject("location").has("lng")) {
                    double latitude = json.getJSONObject("location").getDouble("lat");
                    double longitude = json.getJSONObject("location").getDouble("lng");
                    this.getLocation().setLatLng(new LatLng(latitude, longitude));
                }
            }
        } catch (JSONException e) {

        }
    }
}
