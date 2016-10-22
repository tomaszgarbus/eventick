package hackaton.waw.eventnotifier.event;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import hackaton.waw.eventnotifier.R;

public class EventQueryIntentService extends IntentService {

    private EventManager eventManager;
    private AlarmManager alarmManager;

    public EventQueryIntentService() {
        super("EventQueryIntentService");
    }

    public void notifyAboutEvent(Event event) {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setColor(Color.CYAN)
                .setContentTitle(getString(R.string.new_event_in_warsaw))
                .setContentText(event.getName())
                .setSmallIcon(R.drawable.placeholder);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notifBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        eventManager = new EventManager();
        if (intent != null) {
            final String action = intent.getAction();
            List<Event> newEvents = eventManager.getEvents();
            for (Event event : newEvents) {
                System.out.println("eloszki");
                eventManager.storeEvent(event);
                notifyAboutEvent(event);
            }
        }
    }
}
