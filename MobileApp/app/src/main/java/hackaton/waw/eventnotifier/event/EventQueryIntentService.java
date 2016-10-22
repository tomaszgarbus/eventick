package hackaton.waw.eventnotifier.event;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;


import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import hackaton.waw.eventnotifier.R;
import hackaton.waw.eventnotifier.db.DBHelper;

public class EventQueryIntentService extends IntentService {

    private EventManager eventManager;

    public EventQueryIntentService() {
        super("EventQueryIntentService");
    }

    public void notifyAboutEvent(Event event) {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setColor(Color.CYAN)
                .setContentTitle(getString(R.string.new_event_in_warsaw))
                .setContentText(event.getName())
                .setSmallIcon(R.drawable.placeholder);
        if (event.getPicture() != null) notifBuilder.setLargeIcon(event.getPicture());
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(), notifBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBHelper dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        eventManager = new EventManager(dbHelper);
        if (intent != null) {
            final String action = intent.getAction();
            List<Event> newEvents = eventManager.queryRecommendedEvents();
            Iterator<Event> iter = newEvents.iterator();
            while (iter.hasNext()) {
                Event event = iter.next();
                System.out.println("eloszki");
                try {
                    eventManager.storeEvent(event);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                notifyAboutEvent(event);
            }
        }

    }
}
