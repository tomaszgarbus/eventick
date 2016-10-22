package hackaton.waw.eventnotifier.event;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.FileDescriptor;

import hackaton.waw.eventnotifier.location.Location;
import hackaton.waw.eventnotifier.R;

public class EventQueryService extends Service {
    public EventQueryService() {
    }

    IBinder iBinder = new IBinder() {
        @Override
        public String getInterfaceDescriptor() throws RemoteException {
            return null;
        }

        @Override
        public boolean pingBinder() {
            return false;
        }

        @Override
        public boolean isBinderAlive() {
            return false;
        }

        @Override
        public IInterface queryLocalInterface(String descriptor) {
            return null;
        }

        @Override
        public void dump(FileDescriptor fd, String[] args) throws RemoteException {

        }

        @Override
        public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {

        }

        @Override
        public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return false;
        }

        @Override
        public void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException {

        }

        @Override
        public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
            return false;
        }
    };

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
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_SHORT).show();
        Event sampleEvent = new Event();
        sampleEvent.setName("EventName");
        sampleEvent.setLocation(new Location());
        sampleEvent.getLocation().setName("EventLocationName");
        notifyAboutEvent(sampleEvent);

        return iBinder;
    }
}
