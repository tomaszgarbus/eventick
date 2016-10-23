package hackaton.waw.eventnotifier;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import hackaton.waw.eventnotifier.db.DBHelper;
import hackaton.waw.eventnotifier.event.Event;
import hackaton.waw.eventnotifier.event.EventAlarmReceiver;
import hackaton.waw.eventnotifier.event.EventDetailsFragment;
import hackaton.waw.eventnotifier.event.EventListFragment;
import hackaton.waw.eventnotifier.event.EventManager;
import hackaton.waw.eventnotifier.event.EventQueryIntentService;
import hackaton.waw.eventnotifier.user.UserInfoFragment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventListFragment.OnListFragmentInteractionListener, EventDetailsFragment.OnFragmentInteractionListener {

    private CallbackManager callbackManager;
    private EventManager eventManager;
    public DBHelper dbHelper;

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        eventManager = new EventManager(dbHelper);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getFragmentManager().beginTransaction().replace(R.id.content_main, MainFragment.newInstance()).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setStatusBarTranslucent(true);


        if(AccessToken.getCurrentAccessToken() != null){
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
            ProfilePictureView profilePictureView;
            profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.image);
            profilePictureView.setProfileId(AccessToken.getCurrentAccessToken().getUserId());
            TextView name = (TextView) headerView.findViewById(R.id.name);
            name.setText(Profile.getCurrentProfile().getName());
        }

        if (getIntent().getData() != null) {
            if (getIntent().getData().getEncodedPath().equals("/event")) {
                Long id = Long.parseLong(getIntent().getData().getQueryParameter("id"));
                Event event = eventManager.findEventById(id);
                getFragmentManager().beginTransaction().replace(R.id.content_main, EventDetailsFragment.newInstance(event)).commit();
            }
        } else {
            Intent alarmIntent = new Intent(this, EventAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            AlarmManager alarmManager =  (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 10000, 60000, pendingIntent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_event_list) {
            loadMainFragment();
        } else if (id == R.id.nav_user_info) {
            loadUserInfoFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadMainFragment() {
        getFragmentManager().beginTransaction().replace(R.id.content_main, MainFragment.newInstance()).commit();
    }

    public void loadUserInfoFragment() {
        getFragmentManager().beginTransaction().replace(R.id.content_main, UserInfoFragment.newInstance()).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Event item) {
        getFragmentManager().beginTransaction().replace(R.id.content_main, EventDetailsFragment.newInstance(item)).addToBackStack(null).commit();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
