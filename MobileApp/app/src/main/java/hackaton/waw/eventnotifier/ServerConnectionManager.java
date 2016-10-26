package hackaton.waw.eventnotifier;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by tomek on 10/25/16.
 */

public class ServerConnectionManager {
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

    public boolean authenticate =
}
