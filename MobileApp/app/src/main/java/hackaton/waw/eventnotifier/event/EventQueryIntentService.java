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
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import hackaton.waw.eventnotifier.MainActivity;
import hackaton.waw.eventnotifier.R;
import hackaton.waw.eventnotifier.ServerConnectionManager;
import hackaton.waw.eventnotifier.db.DBHelper;

public class EventQueryIntentService extends IntentService {

    private EventManager eventManager;
    private ServerConnectionManager serverConnectionManager;

    public EventQueryIntentService() {
        super("EventQueryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBHelper dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        eventManager = new EventManager(dbHelper);
        serverConnectionManager = new ServerConnectionManager(this);
        serverConnectionManager.authenticate(AccessToken.getCurrentAccessToken());

        if (intent != null) {
            if (AccessToken.getCurrentAccessToken() == null) {
                return;
            }
            serverConnectionManager.getRecommendedEvents();
        }

    }
}
