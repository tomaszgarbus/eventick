package hackaton.waw.eventnotifier.event;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.util.List;

public class EventQueryIntentService extends IntentService {

    private EventManager eventManager;

    public EventQueryIntentService() {
        super("EventQueryIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        eventManager = new EventManager();
        if (intent != null) {
            final String action = intent.getAction();
            List<Event> newEvents = eventManager.getEvents();

        }
    }
}
