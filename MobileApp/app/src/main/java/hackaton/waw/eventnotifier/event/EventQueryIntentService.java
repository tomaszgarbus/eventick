package hackaton.waw.eventnotifier.event;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;


import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import hackaton.waw.eventnotifier.BitmapCache;
import hackaton.waw.eventnotifier.MainActivity;
import hackaton.waw.eventnotifier.R;
import hackaton.waw.eventnotifier.ServerConnectionManager;
import hackaton.waw.eventnotifier.db.DBHelper;
import hackaton.waw.eventnotifier.user.User;

public class EventQueryIntentService extends IntentService {

    private BitmapCache bitmapCache;
    private EventManager eventManager;
    private ServerConnectionManager serverConnectionManager;

    public EventQueryIntentService() {
        super("EventQueryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FacebookSdk.sdkInitialize(this);
        DBHelper dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        bitmapCache = new BitmapCache();
        eventManager = new EventManager(dbHelper);
        serverConnectionManager = new ServerConnectionManager(this);
        serverConnectionManager.authenticate(AccessToken.getCurrentAccessToken());
        serverConnectionManager.setCurrentUserId();
        serverConnectionManager.setBitmapCache(bitmapCache);

        if (intent != null) {
            if (AccessToken.getCurrentAccessToken() == null) {
                return;
            }
            serverConnectionManager.getRecommendedEvents();
        }

    }
}
