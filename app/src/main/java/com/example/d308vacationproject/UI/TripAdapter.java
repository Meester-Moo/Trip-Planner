package com.example.d308vacationproject.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationproject.R;
import com.example.d308vacationproject.entities.Trip;

import java.util.List;

// RecyclerView adapter for displaying trip list items.
// Each item shows the trip name, hotel, and dates. Tapping an item opens TripDetails for editing.
public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> mTrips;          // Current list of trips to display
    private final Context context;
    private final LayoutInflater mInflater;

    public TripAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    // ViewHolder holds references to views for a single trip list item
    public class TripViewHolder extends RecyclerView.ViewHolder {
        private final TextView tripItemView;
        private final TextView hotelView;
        private final TextView datesView;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripItemView = itemView.findViewById(R.id.tripListItemTextView);
            hotelView = itemView.findViewById(R.id.tripListItemHotel);
            datesView = itemView.findViewById(R.id.tripListItemDates);

            // Click listener: opens TripDetails and passes the trip ID
            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    final Trip current = mTrips.get(position);
                    Intent intent = new Intent(context, TripDetails.class);
                    intent.putExtra("id", current.getTripID());
                    context.startActivity(intent);
                }
            });
        }
    }

    // Inflate the trip_list_item layout for each row
    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.trip_list_item, parent, false);
        return new TripViewHolder(itemView);
    }

    // Bind trip data to the ViewHolder's views
    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        if (mTrips != null) {
            Trip current = mTrips.get(position);
            holder.tripItemView.setText(current.getTripName());
            holder.hotelView.setText(current.getHotel());

            // Build date range string (e.g., "02/01/2026 - 02/10/2026")
            String start = current.getStartDate();
            String end = current.getEndDate();
            if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
                holder.datesView.setText(start + " - " + end);
            } else {
                holder.datesView.setText("No dates set");
            }
        } else {
            holder.tripItemView.setText("No trip name");
            holder.hotelView.setText("");
            holder.datesView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mTrips != null ? mTrips.size() : 0;
    }

    // Called by LiveData observer to update the list when trip data changes
    public void setTrips(List<Trip> trips) {
        mTrips = trips;
        notifyDataSetChanged();
    }
}
