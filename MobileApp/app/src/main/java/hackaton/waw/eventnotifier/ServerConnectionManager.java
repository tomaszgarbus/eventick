package hackaton.waw.eventnotifier;

import android.content.Context;
import android.os.AsyncTask;

import com.facebook.AccessToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.Setter;

import static android.R.attr.data;

/**
 * Created by tomek on 10/25/16.
 */

public class ServerConnectionManager {

    private Context context;

    public ServerConnectionManager(Context context) {
        this.context = context;
    }

    @Getter
    @Setter
    public class LoginBean {
        private String userId;
        private String facebookToken;
    }

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

    private class AuthenticationTask extends AsyncTask<AccessToken, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(AccessToken... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://"+context.getString(R.string.server_address)+"/login");

            try {
                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("userId", params[0].getUserId()));
                nameValuePairs.add(new BasicNameValuePair("accessToken", params[0].getToken()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);
                return true;
            } catch (ClientProtocolException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
    }

    public boolean authenticate(AccessToken accessToken) {
        try {
            return new AuthenticationTask().execute(accessToken).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
