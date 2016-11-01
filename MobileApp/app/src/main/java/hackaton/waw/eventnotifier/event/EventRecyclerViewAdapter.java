package hackaton.waw.eventnotifier.event;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import hackaton.waw.eventnotifier.BitmapCache;
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
        holder.item = events.get(position);

        holder.idView.setText(events.get(position).getName());
        if (events.get(position).getLocation() != null) {
            holder.contentView.setText(events.get(position).getLocation().getName());
        }
        if (events.get(position).getDate() != null) {
            String todayStr = eventManager.getContext().getString(R.string.today);
            String tomorrowStr = eventManager.getContext().getString(R.string.tomorrow);
            holder.dateView.setText(events.get(position).getDisplayableDate(todayStr, tomorrowStr));
        }
        if (events.get(position).getPictureURL() != null) {
            holder.imageView.setImageUrl(holder.item.getPictureURL(), holder.imageLoader);
            holder.setImageViewLayoutParams();
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.item);
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
        public final View view;
        public final TextView idView;
        public final TextView contentView;
        public final TextView dateView;
        public final RelativeLayout relativeLayout;
        public Event item;
        public BitmapCache bitmapCache;
        private ImageLoader imageLoader;
        private RequestQueue queue;
        private NetworkImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_event);
            idView = (TextView) view.findViewById(R.id.id);
            contentView = (TextView) view.findViewById(R.id.content);
            dateView = (TextView) view.findViewById(R.id.date);
            imageView = (NetworkImageView) view.findViewById(R.id.image_view_event_miniature);

            queue = Volley.newRequestQueue(view.getContext());
            bitmapCache = ((MainActivity)view.getContext()).getBitmapCache();
            imageLoader = new ImageLoader(queue, bitmapCache);
        }

        public void setImageViewLayoutParams() {
            if (bitmapCache.getBitmap(item.getPictureURL()) == null) {
                return;
            }

            ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();

            float density = view.getContext().getResources().getDisplayMetrics().density;
            float dpHeight = bitmapCache.getBitmap(item.getPictureURL()).getHeight() / density;
            float dpWidth = bitmapCache.getBitmap(item.getPictureURL()).getWidth() / density;

            dpHeight = dpHeight * 360 / dpWidth;
            dpWidth = 360;

            dpHeight = Math.max(150, Math.min(360, dpHeight));

            int height = new Float(dpHeight * density).intValue();

            params.height = height;
            relativeLayout.setLayoutParams(params);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + contentView.getText() + "'";
        }
    }
}
