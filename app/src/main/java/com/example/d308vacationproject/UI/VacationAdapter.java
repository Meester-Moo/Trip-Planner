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
import com.example.d308vacationproject.entities.Vacation;

import java.util.List;

// RecyclerView adapter for displaying vacation list items.
// Each item shows the vacation name. Tapping an item opens VacationDetails for editing.
public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private List<Vacation> mVacations;      // Current list of vacations to display
    private final Context context;
    private final LayoutInflater mInflater;

    public VacationAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    // ViewHolder holds references to views for a single vacation list item
    public class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;
        private final TextView hotelView;
        private final TextView datesView;


        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.vacationListItemTextView);
            hotelView = itemView.findViewById(R.id.vacationListItemHotel);
            datesView = itemView.findViewById(R.id.vacationListItemDates);


            // Click listener: opens VacationDetails and passes the vacation ID
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    final Vacation current = mVacations.get(position);
                    Intent intent = new Intent(context, VacationDetails.class);
                    intent.putExtra("id", current.getVacationID());
                    context.startActivity(intent);
                }
            });
        }
    }

    // Inflate the vacation_list_item layout for each row
    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    // Bind vacation data to the ViewHolder's views
    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        if (mVacations != null) {
            Vacation current = mVacations.get(position);
            holder.vacationItemView.setText(current.getVacationName());
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
            holder.vacationItemView.setText("No vacation name");
            holder.hotelView.setText("");
            holder.datesView.setText("");
        }
    }


    @Override
    public int getItemCount() {
        return mVacations != null ? mVacations.size() : 0;
    }

    // Called by LiveData observer to update the list when vacation data changes
    public void setVacations(List<Vacation> vacations) {
        mVacations = vacations;
        notifyDataSetChanged();
    }
}
