package hackaton.waw.eventnotifier.event;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import hackaton.waw.eventnotifier.BitmapCache;
import hackaton.waw.eventnotifier.MainActivity;
import hackaton.waw.eventnotifier.R;
import hackaton.waw.eventnotifier.ServerConnectionManager;
import hackaton.waw.eventnotifier.location.Location;
import lombok.Getter;
import lombok.Setter;
import lombok.core.Main;

import static android.view.View.GONE;

@Getter
@Setter
public class EventDetailsFragment extends Fragment implements OnMapReadyCallback {
    //private OnFragmentInteractionListener mListener;
    private Event event;
    private TextView eventNameTextView, eventDescriptionTextView, eventLocationTextView, eventTimeTextView;
    private ImageButton likeButton, dislikeButton, shareButton, participateButton, ticketsButton;
    private ServerConnectionManager serverConnectionManager;
    private BitmapCache bitmapCache;
    private ImageLoader imageLoader;
    private RequestQueue queue;
    private NetworkImageView eventPictureImageView;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    public static EventDetailsFragment newInstance(Event event) {
        if (event == null) {
            throw new RuntimeException("Null event passed");
        }

        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setEvent(event);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (event.getLocation() != null) {
            if (event.getLocation().getLatLng() != null) {
                googleMap.addMarker(new MarkerOptions().position(event.getLocation().getLatLng()).title("Here"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(event.getLocation().getLatLng()));
                googleMap.setMinZoomPreference(15);
            } else {
                try {
                    List<Address> addresses = new Geocoder(getActivity()).getFromLocationName(event.getLocation().getName(), 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Here"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(), address.getLongitude())));
                        googleMap.setMinZoomPreference(15);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        serverConnectionManager = ((MainActivity) getActivity()).getServerConnectionManager();

        queue = Volley.newRequestQueue(getActivity());
        bitmapCache = ((MainActivity)getActivity()).getBitmapCache();
        imageLoader = new ImageLoader(queue, new BitmapCache());

        eventNameTextView = (TextView) view.findViewById(R.id.text_view_event_name);
        eventDescriptionTextView = (TextView) view.findViewById(R.id.text_view_event_description);
        eventLocationTextView = (TextView) view.findViewById(R.id.text_view_event_location);
        eventPictureImageView = (NetworkImageView) view.findViewById(R.id.image_view_event_pictue);
        eventTimeTextView = (TextView) view.findViewById(R.id.text_view_event_time);
        likeButton = (ImageButton) view.findViewById(R.id.button_like);
        dislikeButton = (ImageButton) view.findViewById(R.id.button_dislike);
        shareButton = (ImageButton) view.findViewById(R.id.button_share);
        participateButton = (ImageButton) view.findViewById(R.id.button_participate);
        ticketsButton = (ImageButton) view.findViewById(R.id.button_tickets);


        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setLiked(true);
                serverConnectionManager.likeEvent(event.getId());
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setDisliked(true);
                serverConnectionManager.dislikeEvent(event.getId());
            }
        });

        participateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setInterested(true);
                serverConnectionManager.interestedInEvent(event.getId());
            }
        });

        if (event.getName() != null) {
            eventNameTextView.setText(event.getName());
        }
        if (event.getDescription() != null) {
            eventDescriptionTextView.setText(event.getDescription());
        }
        if (event.getDate() != null) {
            eventTimeTextView.setText(event.getDisplayableDate());
        }
        if (event.getLocation() != null) {
            eventLocationTextView.setText(event.getLocation().getName());
        }
        if (event.getPictureURL() != null) {
            eventPictureImageView.setImageUrl(event.getPictureURL(), imageLoader);
            //eventPictureImageView.setImageBitmap(event.getPicture());
        }
        if (event.getTicketsUri() != null && event.getTicketsUri().startsWith("http")) {
            ticketsButton.setVisibility(View.VISIBLE);
            ticketsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getTicketsUri()));
                    startActivity(browserIntent);
                }
            });
        } else {
            ticketsButton.setVisibility(GONE);
        }
        //Prepare Google Map
        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

}
