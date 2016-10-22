package hackaton.waw.eventnotifier.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EventAlarmReceiver extends BroadcastReceiver {
    public EventAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, EventQueryIntentService.class);
        context.startService(serviceIntent);
    }
}
