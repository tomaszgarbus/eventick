package hackaton.waw.eventnotifier.event;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.Dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import hackaton.waw.eventnotifier.MainActivity;
import hackaton.waw.eventnotifier.db.DBHelper;
import hackaton.waw.eventnotifier.location.Location;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/21/16.
 */

@Getter
@Setter
public class EventManager {

    Context context;
    Dao<Event, Long> eventDao;
    Dao<Location, Long> locDao;
    DBHelper dbHelper;

    public EventManager(DBHelper dbHelper){
        try {
            this.dbHelper = dbHelper;
            eventDao = dbHelper.getEventDao();
            locDao = dbHelper.getLocationDao();
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            //TODO handling
        }
    }

    public static class ServerConnection {
        private JSONObject getJSON(final URL url) {
            AsyncTask<URL, Integer, JSONObject> asyncTask = new AsyncTask<URL, Integer, JSONObject>() {
                @Override
                protected JSONObject doInBackground(URL... params) {
                    try {
                        Scanner scanner = new Scanner(url.openStream());
                        String s = scanner.nextLine();
                        JSONObject json = new JSONObject(s);
                        return json;
                    } catch (Exception e) {
                        return null;
                    }
                }
            };
            try {
                return asyncTask.execute(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    public static class FacebookEventFetcher {

        public static Bitmap bitmapFromCoverSource(final String source) {
            AsyncTask<String, Integer, Bitmap> asyncTask = new AsyncTask<String, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    System.out.println("source + " + source);
                    try {
                        URL url = new URL(source);
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        System.out.println(bitmap.toString());
                        return bitmap;
                    } catch (Exception e) {
                        System.out.println(e.toString());
                        e.printStackTrace();
                        return null;
                    }
                }
            };
            try {
                return asyncTask.execute().get(); //not really async, get() waits for result
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static GraphRequest createGraphRequest(String eventId, final Event event) {
            GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "/" + eventId + "?fields=name,description,place,cover,type,start_time",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            System.out.print(response.toString());
                            JSONObject json = response.getJSONObject();
                            event.parseJSON(json);
                            System.out.println(response.getJSONObject().toString());
                        }
                    });
            return request;
        }

        public static Event getFacebookEvent(String eventId) {
            if (AccessToken.getCurrentAccessToken() == null) return null;
            final Event event = new Event();
            event.setLocation(new Location());
            GraphRequest request = createGraphRequest(eventId, event);
            request.executeAndWait();
            return event;
        }

        public static Event getFacebookEventAsync(String eventId) {
            if (AccessToken.getCurrentAccessToken() == null) return null;
            final Event event = new Event();
            event.setLocation(new Location());
            GraphRequest request = createGraphRequest(eventId, event);
            request.executeAsync();
            return event;
        }
    };

    public static List<Event> getSampleEvents() {
        List<Event> ret = new ArrayList<>();
        Event event0 = new Event();
        event0.setName("Party soft");
        event0.setDescription("yo");
        event0.setLocation(new Location());
        event0.getLocation().setName("Proxima");
        Event event1 = FacebookEventFetcher.getFacebookEventAsync("247854448900392");
        Event event2 = FacebookEventFetcher.getFacebookEventAsync("1968572576702697");
        Event event3 = new Event();
        event3.setName("Party soft long hard dick nigger shit ");
        event3.setDescription("yo");
        event3.setLocation(new Location());
        event3.getLocation().setName("Proxima");
        Event event4 = FacebookEventFetcher.getFacebookEventAsync("187877934986121");
        ret.add(event0);
        ret.add(event3);
        if (event1 != null) ret.add(event1);
        if (event2 != null) ret.add(event2);
        if (event4 != null) ret.add(event4);
        //if (event3 != null) ret.add(event3);
        return ret;
    }

    private List<Event> events;


    public void initialize() throws SQLException {
        if (events != null) {
            events.clear();
        } else {
            events = Collections.synchronizedList(new ArrayList<Event>());
        }
        events.addAll(eventDao.queryBuilder().orderBy("date", true).query());
        Iterator<Event> iter = events.iterator();
        while (iter.hasNext()) {
            Event event = iter.next();
            event.setPicture(FacebookEventFetcher.bitmapFromCoverSource(event.getPictureURL()));
        }
    }

    public Event findEventById(Long id) {
        Iterator<Event> iter = events.iterator();
        while (iter.hasNext()) {
            Event e = iter.next();
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public /*static*/ List<Event> queryRecommendedEvents() {
        Event event1 = FacebookEventFetcher.getFacebookEvent("1968572576702697");
        Event event2 = FacebookEventFetcher.getFacebookEvent("1802718633280217");
        Event event3 = FacebookEventFetcher.getFacebookEvent("1798419067053418");
        Event event4 = FacebookEventFetcher.getFacebookEvent("1734050926845064");
        return Collections.synchronizedList(Arrays.asList(event1, event2, event3, event4));
    }

    public void storeEvent(Event event) throws SQLException {
        eventDao.create(event);
        //eventDao.commit(dbHelper.getConnectionSource().getReadWriteConnection());
        //locDao.commit(dbHelper.getConnectionSource().getReadWriteConnection());
        //TODO: update
    }
}
