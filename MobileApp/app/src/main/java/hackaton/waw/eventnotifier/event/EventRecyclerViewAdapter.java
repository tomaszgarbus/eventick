package hackaton.waw.eventnotifier.event;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hackaton.waw.eventnotifier.MainActivity;
import hackaton.waw.eventnotifier.event.EventListFragment.OnListFragmentInteractionListener;
import hackaton.waw.eventnotifier.R;

import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private EventManager eventManager;
    private final List<Event> events;
    private final OnListFragmentInteractionListener mListener;

    public EventRecyclerViewAdapter(EventManager eventManager, OnListFragmentInteractionListener listener) {
        this.eventManager = eventManager;
        this.events = eventManager.getEvents();
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = events.get(position);
        holder.mIdView.setText(events.get(position).getName());
        if (events.get(position).getLocation() != null) {
            holder.mContentView.setText(events.get(position).getLocation().getName());
        }
        if (events.get(position).getDate() != null) {
            holder.mDateView.setText(events.get(position).getDisplayableDate());
        }
        if (events.get(position).getPicture() != null) {
            holder.mImageView.setImageBitmap(events.get(position).getPicture());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void remove(int position) {
        Long id = events.get(position).getId();
        events.remove(position);
        eventManager.deleteEvent(id);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDateView;
        public final ImageView mImageView;
        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDateView = (TextView) view.findViewById(R.id.date);
            mImageView = (ImageView) view.findViewById(R.id.image_view_event_miniature);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
