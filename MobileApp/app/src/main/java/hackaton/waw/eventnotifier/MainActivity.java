package hackaton.waw.eventnotifier;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.json.JSONObject;

import java.util.Arrays;

import hackaton.waw.eventnotifier.db.DBHelper;
import hackaton.waw.eventnotifier.event.Event;
import hackaton.waw.eventnotifier.event.EventAlarmReceiver;
import hackaton.waw.eventnotifier.event.EventDetailsFragment;
import hackaton.waw.eventnotifier.event.EventListFragment;
import hackaton.waw.eventnotifier.event.EventManager;
import hackaton.waw.eventnotifier.event.EventRecyclerViewAdapter;
import hackaton.waw.eventnotifier.user.User;
import hackaton.waw.eventnotifier.user.UserInfoFragment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventListFragment.OnListFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private CallbackManager callbackManager;
    private EventManager eventManager;
    private ServerConnectionManager serverConnectionManager;
    private DBHelper dbHelper;
    private RecyclerView mRecyclerView;
    private BitmapCache bitmapCache;
    private Location lastLocation;
    private GoogleApiClient googleApiClient;

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
        //Initialize Facebook and shit
        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);
        setUpAccessTokenTracker();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        eventManager = new EventManager(this, dbHelper);
        serverConnectionManager = new ServerConnectionManager(getApplicationContext());
        bitmapCache = new BitmapCache();

        //Ask for permision to external storage
        requestPermissions();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getFragmentManager().beginTransaction().replace(R.id.content_main, MainFragment.newInstance()).commit();

        setStatusBarTranslucent(true);

        if (getIntent().getData() != null) {
            if (getIntent().getData().getEncodedPath().equals("/event")) {
                loadEventFromNotification();
            }
        } else {
            //Things that need to be done only once
            setUpAlarmManager();
        }

        setUpGoogleApiClient();
    }

    private void setUpGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void requestPermissions() {
        // Storage Permissions
        final int REQUEST_EXTERNAL_STORAGE = 1;
        final String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        // Location permissions
        final int REQUEST_LOCATION = 2;
        final String[] PERMISSIONS_LOCATION = {
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        // Check if we have write permission
        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATION
            );
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void loadEventFromNotification() {
        Long id = Long.parseLong(getIntent().getData().getQueryParameter("id"));
        Event event = eventManager.findEventById(id);
        getFragmentManager().beginTransaction().replace(R.id.content_main, EventDetailsFragment.newInstance(event)).commit();
    }

    private void setUpAlarmManager() {
        Intent alarmIntent = new Intent(this, EventAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1800000, 1800000, pendingIntent);
    }

    private void setUpAccessTokenTracker() {
        new AccessTokenTracker() {

            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    setUpProfilePictureView();
                    setUpCoverPhoto();

                    serverConnectionManager.authenticate(currentAccessToken);
                    serverConnectionManager.setCurrentUserId();
                    serverConnectionManager.getRecommendedEvents();
                    serverConnectionManager.setBitmapCache(bitmapCache);

                    if (serverConnectionManager != null && lastLocation != null) {
                        serverConnectionManager.getUser().setLatitude(lastLocation.getLatitude());
                        serverConnectionManager.getUser().setLongitude(lastLocation.getLongitude());
                        serverConnectionManager.sendUserLocation();
                    }
                }
            }
        }.startTracking();
    }

    private void setUpProfilePictureView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (AccessToken.getCurrentAccessToken() != null) {
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
            ProfilePictureView profilePictureView;
            profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.image);
            profilePictureView.setProfileId(AccessToken.getCurrentAccessToken().getUserId());
            TextView name = (TextView) headerView.findViewById(R.id.name);
            name.setText(Profile.getCurrentProfile().getName());
        }
    }

    @TargetApi(value = 16)
    private void setUpCoverPhoto() {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout_nav_header);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me?fields=cover",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject json = response.getJSONObject();
                        try {
                            if (!json.has("cover") || !json.getJSONObject("cover").has("source")) {
                                return;
                            }
                            String source = json.getJSONObject("cover").getString("source");
                            Bitmap photo = EventManager.FacebookEventFetcher.bitmapFromCoverSource(source);
                            layout.setBackground(new BitmapDrawable(getResources(), photo));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
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
        } else if (id == R.id.nav_rate_app) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                //UtilityClass.showAlertDialog(context, ERROR, "Couldn't launch the market", null, 0);
            }
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
    public void onListFragmentInteraction(Event item) {
        getFragmentManager().beginTransaction().replace(R.id.content_main, EventDetailsFragment.newInstance(item)).addToBackStack(null).commit();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setUpSwiping() {
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    ((EventRecyclerViewAdapter) mRecyclerView.getAdapter()).remove(position);
                                    mRecyclerView.getAdapter().notifyItemRemoved(position);
                                }
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    ((EventRecyclerViewAdapter) mRecyclerView.getAdapter()).remove(position);
                                    mRecyclerView.getAdapter().notifyItemRemoved(position);
                                }
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        });

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (lastLocation != null) {
            if (serverConnectionManager.getUser() != null) {
                User user = serverConnectionManager.getUser();
                user.setLongitude(lastLocation.getLongitude());
                user.setLatitude(lastLocation.getLatitude());
                serverConnectionManager.sendUserLocation();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
