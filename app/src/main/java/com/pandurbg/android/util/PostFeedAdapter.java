package com.pandurbg.android.util;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pandurbg.android.R;
import com.pandurbg.android.model.Post;

import java.util.ArrayList;

public class PostFeedAdapter extends RecyclerView.Adapter<PostFeedAdapter.ViewHolder>  implements OnMapReadyCallback {



    private ArrayList<Post> mDataset;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView mtvUsername;
        public TextView mtvStreet;
        public TextView mtvTime;
        public TextView mtvApprovals;
        public TextView mtvDescription;
        public ImageView mivCategoryIcon;
        public MapView mMapView;
        public ImageButton mibApprove;
        public ImageButton mibOpenMap;

        public ViewHolder(View v) {
            super(v);

            mtvStreet = v.findViewById(R.id.tvStreet);
            mtvTime = v.findViewById(R.id.tvTime);
            mtvUsername = v.findViewById(R.id.tvUsername);
            mtvApprovals = v.findViewById(R.id.tvApprovals);
            mtvDescription = v.findViewById(R.id.tvDescription);
            mivCategoryIcon = v.findViewById(R.id.ivCategory);
            mMapView = v.findViewById(R.id.postMap);
            mibApprove = v.findViewById(R.id.ibApprove);
            mibOpenMap = v.findViewById(R.id.ibMap);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostFeedAdapter(ArrayList<Post> myDataset) {
        mDataset = myDataset;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_post2, parent, false);


        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mtvStreet.setText(mDataset.get(position).getStreet());
        holder.mtvApprovals.setText(Integer.toString(mDataset.get(position).getApprovals())+ "Approvals");
        holder.mtvDescription.setText(mDataset.get(position).getDescription());
        holder.mtvTime.setText(mDataset.get(position).getTime());
        holder.mtvUsername.setText(mDataset.get(position).getUser().getUserName());
        holder.mibOpenMap.setBackgroundResource(R.drawable.marker_icon);
        holder.mibApprove.setBackgroundResource(android.R.drawable.ic_input_add);
        holder.mMapView.getMapAsync(this);
        setCategoryIconDrawable(holder, position);
        Log.d("location:" , mDataset.get(position).getLocation().getLatitude()+ " , " + mDataset.get(position).getLocation().getLongitude());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng initialLocation = new LatLng(44, 21);
        googleMap.addMarker(new MarkerOptions().position(initialLocation)
                .title("Marker on initial location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));
    }

    private void setCategoryIconDrawable(ViewHolder holder, int positionIndex){

        String categoryName = mDataset.get(positionIndex).getCategory().getName();

        switch(categoryName){

            case "Police Alert":
                holder.mivCategoryIcon.setBackgroundResource(R.drawable.police_icon);
                break;
            case "Camera Alert":
                holder.mivCategoryIcon.setBackgroundResource(R.drawable.camera_icon);
                break;
            case "Danger Alert":
                holder.mivCategoryIcon.setBackgroundResource(R.drawable.road_danger_icon);
                break;
            default: Log.d("category error", "Wrong category name detected");

        }

    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
