package com.example.noq;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<String> values;
    private Context context;
    private String userType;
    private boolean queueStarted = false;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View layout;
        public Button cafeButton;
        public TextView cafeAddress, cafeName;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            context = v.getContext();
            cafeButton = (Button) v.findViewById(R.id.btn_viewPlace);
            cafeAddress = (TextView) v.findViewById(R.id.place_address);
            cafeName = (TextView) v.findViewById(R.id.place_name);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(List<String> myDataset, String loginUser) {
        values = myDataset;
        userType = loginUser;
    }

    // Create new views (invoked by the layout_card manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout_card parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout_card manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String locationInfo = values.get(position);
        final String placeId, placeName, email;
        Log.d("Location Info", locationInfo);
        String [] locationDetails = locationInfo.split(":");
        holder.cafeName.setText(locationDetails[0]);
        placeId = locationDetails[1];
        placeName = locationDetails[0];
        email = locationDetails[2];


        holder.cafeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.equals("admin")) {
                    Log.d("User Type Check", "admin");
                    Intent intent = new Intent(context, PlaceActivity.class);
                    intent.putExtra("placeId", placeId);
                    intent.putExtra("placeName", placeName);
                    intent.putExtra("email", email);
                    context.startActivity(intent);
                } else {
                    Log.d("User Type Check", "user");
                    Intent intent = new Intent(context, QueueActivity.class);
                    intent.putExtra("placeId", placeId);
                    intent.putExtra("placeName", placeName);
                    intent.putExtra("email", email);
                    context.startActivity(intent);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout_card manager)
    @Override
    public int getItemCount() {
        return (values == null) ? 0 : values.size();
    }

}