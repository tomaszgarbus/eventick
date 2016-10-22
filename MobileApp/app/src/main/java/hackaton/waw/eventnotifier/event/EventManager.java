package hackaton.waw.eventnotifier.event;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hackaton.waw.eventnotifier.location.Location;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tomek on 10/21/16.
 */

@Getter
@Setter
public class EventManager {

    public static class FacebookEventFetcher {

        private static Bitmap bitmapFromCoverSource(final String source) {
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
                return asyncTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Event getFacebookEvent(String eventId) {
            if (AccessToken.getCurrentAccessToken() == null) return null;
            final Event event = new Event();
            event.setLocation(new Location());
            GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "/" + eventId + "?fields=name,description,place,cover,type",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {
                                response.getJSONObject().
                                event.setName(response.getJSONObject().getString("name"));
                                event.setDescription(response.getJSONObject().getString("description"));
                                event.setPicture(bitmapFromCoverSource(response.getJSONObject().getJSONObject("cover").getString("source")));

                                event.getLocation().setName(response.getJSONObject().getJSONObject("place").getString("name"));
                                double latitude = response.getJSONObject().getJSONObject("place").getJSONObject("location").getDouble("latitude");
                                double longitude = response.getJSONObject().getJSONObject("place").getJSONObject("location").getDouble("longitude");
                                event.getLocation().setLatLng(new LatLng(latitude, longitude));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(response.getJSONObject().toString());
                        }
                    });
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
        Event event1 = EventManager.FacebookEventFetcher.getFacebookEvent("247854448900392");
        Event event2 = EventManager.FacebookEventFetcher.getFacebookEvent("1968572576702697");
        //Event event3 = FacebookEventFetcher.getFacebookEvent("899937366817209");
        ret.add(event0);
        if (event1 != null) ret.add(event1);
        if (event2 != null) ret.add(event2);
        //if (event3 != null) ret.add(event3);
        return ret;
    }

    private List<Event> events;

    public EventManager() {
        initialize();
    }

    public void initialize() {
        events = getSampleEvents();
    }

}
