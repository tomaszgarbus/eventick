package hackaton.waw.eventnotifier.event;

import android.content.Context;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import hackaton.waw.eventnotifier.R;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDetailsFragment extends Fragment implements OnMapReadyCallback {
    //private OnFragmentInteractionListener mListener;
    private Event event;
    private TextView eventNameTextView, eventDescriptionTextView, eventLocationTextView, eventTimeTextView;
    private ImageView eventPictureImageView;
    private ImageButton likeButton, dislikeButton, shareButton, participateButton;

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

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        eventNameTextView = (TextView) view.findViewById(R.id.text_view_event_name);
        eventDescriptionTextView = (TextView) view.findViewById(R.id.text_view_event_description);
        eventLocationTextView = (TextView) view.findViewById(R.id.text_view_event_location);
        eventPictureImageView = (ImageView) view.findViewById(R.id.image_view_event_pictue);
        eventTimeTextView = (TextView) view.findViewById(R.id.text_view_event_time);
        likeButton = (ImageButton) view.findViewById(R.id.button_like);
        dislikeButton = (ImageButton) view.findViewById(R.id.button_dislike);
        shareButton = (ImageButton) view.findViewById(R.id.button_share);
        participateButton = (ImageButton) view.findViewById(R.id.button_participate);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
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
        if (event.getPicture() != null) {
            eventPictureImageView.setImageBitmap(event.getPicture());
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
