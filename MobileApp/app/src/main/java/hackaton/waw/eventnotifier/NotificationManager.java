package hackaton.waw.eventnotifier;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import hackaton.waw.eventnotifier.event.Event;

/**
 * Created by tomek on 10/28/16.
 */

public class NotificationManager {
    public static void notifyAboutEvent(Context context, Event event) {
        Uri uri = new Uri.Builder().appendQueryParameter("id", event.getId().toString())
                .appendEncodedPath("event").build();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setData(uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setColor(Color.CYAN)
                .setContentTitle(context.getString(R.string.new_event_nearby))
                .setContentText(event.getName())
                .setSmallIcon(R.drawable.fa_cocktail_glass)
                .setContentIntent(pendingIntent);
        if (event.getPicture() != null) notifBuilder.setLargeIcon(event.getPicture());
        android.app.NotificationManager manager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(), notifBuilder.build());
    }
}
