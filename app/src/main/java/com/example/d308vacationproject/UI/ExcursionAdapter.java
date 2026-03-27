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
import com.example.d308vacationproject.entities.Excursion;

import java.util.ArrayList;
import java.util.List;

// RecyclerView adapter for displaying excursion list items within a trip.
// Each item shows the excursion name and date. Tapping an item opens ExcursionDetails for editing.
// Also stores the parent trip's date range so ExcursionDetails can validate excursion dates.
public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private String tripStart;               // Parent trip's start date (for validation in ExcursionDetails)
    private String tripEnd;                 // Parent trip's end date (for validation in ExcursionDetails)
    private List<Excursion> mExcursions;    // Current list of excursions to display
    private final Context context;
    private final LayoutInflater mInflater;

    // ViewHolder holds references to views for a single excursion list item
    public class ExcursionViewHolder extends RecyclerView.ViewHolder {

        private final TextView excursionItemView;   // Excursion name
        private final TextView excursionItemView2;  // Excursion date

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionItemView = itemView.findViewById(R.id.excursionListItemName);
            excursionItemView2 = itemView.findViewById(R.id.excursionListItemDate);

            // Click listener: opens ExcursionDetails with all excursion data and trip date range
            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Excursion current = mExcursions.get(position);

                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("excursionID", current.getExcursionID());
                    intent.putExtra("name", current.getExcursionName());
                    intent.putExtra("date", current.getExcursionDate());
                    intent.putExtra("tripID", current.getTripID());
                    intent.putExtra("tripStart", tripStart);
                    intent.putExtra("tripEnd", tripEnd);
                    intent.putExtra("notify", current.isNotify());
                    context.startActivity(intent);
                }
            });
        }
    }

    // Updates the stored trip date range (called when trip dates change in TripDetails)
    public void updateTripDateRange(String start, String end) {
        this.tripStart = start != null ? start : "";
        this.tripEnd = end != null ? end : "";
        notifyDataSetChanged();
    }

    public ExcursionAdapter(Context context, String tripStart, String tripEnd) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.tripStart = tripStart;
        this.tripEnd = tripEnd;
    }

    // Inflate the excursion_list_item layout for each row
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    // Bind excursion data (name and date) to the ViewHolder's views
    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null && position < mExcursions.size()) {
            Excursion current = mExcursions.get(position);
            holder.excursionItemView.setText(current.getExcursionName());
            holder.excursionItemView2.setText(current.getExcursionDate());
        } else {
            holder.excursionItemView.setText("No excursion name");
            holder.excursionItemView2.setText("No date");
        }
    }

    // Called by LiveData observer to update the list when excursion data changes
    public void setExcursions(List<Excursion> excursions) {
        if (excursions == null) {
            mExcursions = new ArrayList<>();
        } else {
            mExcursions = new ArrayList<>(excursions); // Defensive copy
        }
        notifyDataSetChanged();
    }

    // Returns the current excursion list (used by TripDetails for the share feature)
    public List<Excursion> getmExcursions() {
        return mExcursions;
    }

    @Override
    public int getItemCount() {
        return mExcursions != null ? mExcursions.size() : 0;
    }
}
