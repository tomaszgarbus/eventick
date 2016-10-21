package hackaton.waw.eventnotifier;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import lombok.AllArgsConstructor;
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
            GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "/" + eventId + "?fields=name,description,place,cover,type",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {
                                event.setName(response.getJSONObject().getString("name"));
                                event.setDescription(response.getJSONObject().getString("description"));
                                event.setLocation(new Location());
                                event.getLocation().setName(response.getJSONObject().getJSONObject("place").getString("name"));
                                event.setPicture(bitmapFromCoverSource(response.getJSONObject().getJSONObject("cover").getString("source")));
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
        event0.setName("Party hard");
        event0.setDescription("yo");
        event0.setLocation(new Location());
        event0.getLocation().setName("Proxima");
        Event event1 = new Event();
        event1.setName("Party soft");
        event1.setDescription("yo");
        event1.setLocation(new Location());
        event1.getLocation().setName("Sopot");
        Event event2 = new Event();
        event2.setName("Mega długa nazwa wydarzenia konkurs fotograficzny ja i moja apka");
        event2.setDescription("Zima idzie a ja w starych adidasach już miałem dziurę taką, że można było 2 palce wsadzić więc po miesiącu wyrzeczeń dzięki którym zaoszczędziłem pieniądze, poszedłem wczoraj do galerii handlowej kupić sobie porządne buty na zimę. W sklepie CCC znalazłem pic rel. Solidne wykonanie, przystępna cena, modny wygląd- nie zastanawiałem się długo. Wróciłem z butami do domu, pochodziłem w nich po pokoju, poprzeglądałem się w lustrze i czułem dobrze. Jeszcze je solidnie zaimpregnowałem, żeby nie przepuszczały wody i się nie niszczyły.\n" +
                " \n" +
                "Dzisiaj poszedłem w swoich nowych butach na uniwersytet. Czułem się dzięki nim bardziej pewny siebie, jak siedziałem na korytarzu to nogi wyciągałem daleko, żeby ludzie lepiej widzieli jakie mam eleganckie buty.\n" +
                "Po zajęciach czekam na przystanku na Krakowskim Przedmieściu na autobus a tu z kawiarni wychodzi znany podróżnik katolicki Wojciech Cejrowski. Elegancko ubrany a nie w jakąś tam koszulę hawajską, z egzotycznych motywów to miał tylko w ręce taki kubeczek na yerba mate.");
        event2.setLocation(new Location());
        event2.getLocation().setName("...................................................");
        Event event3 = new Event();
        event3.setName("Eloo");
        event3.setDescription("yo");
        event3.setLocation(new Location());
        event3.getLocation().setName("Radom");
        Event event4 = FacebookEventFetcher.getFacebookEvent("247854448900392");
        ret.add(event0);
        ret.add(event1);
        ret.add(event2);
        ret.add(event3);
        if (event4 != null) ret.add(event4);
        return ret;
    }
}
