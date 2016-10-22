package hackaton.waw.eventnotifier.event;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class EventQueryIntentService extends IntentService {

    public EventQueryIntentService() {
        super("EventQueryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

        }
    }
}
