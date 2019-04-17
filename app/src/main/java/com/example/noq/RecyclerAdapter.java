package com.example.noq;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<String> values;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Button cafeButton;
        public TextView cafeAddress;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            context = v.getContext();
            cafeButton = (Button) v.findViewById(R.id.firstLine);
            cafeAddress = (TextView) v.findViewById(R.id.secondLine);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(List<String> myDataset) {
        values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String locationInfo = values.get(position);
        final String placeId, placeName, email;
        String [] locationDetails = locationInfo.split(":");
        holder.cafeButton.setText(locationDetails[0]);
        placeId = locationDetails[1];
        placeName = locationDetails[0];
        email = locationDetails[2];


        holder.cafeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QueueActivity.class);
                intent.putExtra("placeId", placeId);
                intent.putExtra("placeName", placeName);
                intent.putExtra("email", email);
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (values == null) ? 0 : values.size();
    }

}