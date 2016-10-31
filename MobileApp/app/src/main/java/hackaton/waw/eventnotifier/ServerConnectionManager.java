package hackaton.waw.eventnotifier;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import hackaton.waw.eventnotifier.db.DBHelper;
import hackaton.waw.eventnotifier.event.Event;
import hackaton.waw.eventnotifier.event.EventManager;
import hackaton.waw.eventnotifier.event.EventQueryIntentService;
import hackaton.waw.eventnotifier.user.User;
import lombok.Getter;
import lombok.Setter;

import static android.R.attr.data;
import static hackaton.waw.eventnotifier.NotificationManager.notifyAboutEvent;

/**
 * Created by tomek on 10/25/16.
 */

@Getter
@Setter
public class ServerConnectionManager {

    private Context context;
    private BitmapCache bitmapCache;
    private HttpClient httpClient;
    private EventManager eventManager;
    private HttpClientStack httpClientStack;
    private User user;

    public ServerConnectionManager(Context context) {
        this.context = context;
        httpClient = new DefaultHttpClient();
        httpClientStack = new HttpClientStack(httpClient);
        DBHelper dbHelper = OpenHelperManager.getHelper(context, DBHelper.class);
        eventManager = new EventManager(dbHelper);
        user = new User();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
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
            HttpPost httppost = new HttpPost("http://"+context.getString(R.string.server_address)+"/login");

            try {
                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("username", params[0].getUserId()));
                nameValuePairs.add(new BasicNameValuePair("password", params[0].getToken()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpClient.execute(httppost);
                return true;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
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

    public void setCurrentUserId() {
        RequestQueue queue = Volley.newRequestQueue(context, httpClientStack);
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        String url = "http://" + context.getString(R.string.server_address) + "/users/facebook/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Long id = new JSONObject(response).getLong("id");
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    user.setId(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
        queue.add(stringRequest);
        //TODO: ^ fix this ugly workaround
    }

    public void getRecommendedEvents() {
        RequestQueue queue = Volley.newRequestQueue(context, httpClientStack);
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        String url = "http://" + context.getString(R.string.server_address) + "/recommendations/user/" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Event event = Event.fromJSON(jsonObject);
                        event.setPicture(EventManager.FacebookEventFetcher.bitmapFromCoverSource(event.getPictureURL()));
                        bitmapCache.putBitmap(event.getPictureURL(), event.getPicture());
                        if (eventManager.storeEvent(event)) {
                            notifyAboutEvent(context, event);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void basicHttpPut(String url) {
        RequestQueue queue = Volley.newRequestQueue(context, httpClientStack);
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(putRequest);
    }

    public void basicHttpPost(String url) {
        RequestQueue queue = Volley.newRequestQueue(context, httpClientStack);
        StringRequest putRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(putRequest);
    }

    public void likeEvent(Long eventId) {
        String url = "http://" + context.getString(R.string.server_address) + "/recommendations/like_event/" + eventId + "/as_user/" + user.getId();
        basicHttpPut(url);
    }

    public void dislikeEvent(Long eventId) {
        String url = "http://" + context.getString(R.string.server_address) + "/recommendations/dislike_event/" + eventId + "/as_user/" + user.getId();
        basicHttpPut(url);
    }

    public void interestedInEvent(Long eventId) {
        String url = "http://" + context.getString(R.string.server_address) + "/recommendations/interested_in_event/" + eventId + "/as_user/" + user.getId();
        basicHttpPut(url);
    }

    public void sendUserLocation() {
        basicHttpPost("http://" + context.getString(R.string.server_address) + "/users/check_in/" + user.getId() + "/" + user.getLatitude() + "/" + user.getLongitude());
    }
}
