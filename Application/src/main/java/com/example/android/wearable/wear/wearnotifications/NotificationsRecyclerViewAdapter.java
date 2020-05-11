package com.example.android.wearable.wear.wearnotifications;
import android.app.Notification;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.wearable.wear.wearnotifications.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<NotificationModel> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    NotificationsRecyclerViewAdapter(Context context, ArrayList<NotificationModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        NotificationModel model = new NotificationModel();
        model.headline = "test";
        // this.mData.add(model);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    public void Add(NotificationModel item)
    {
        this.mData.add(0, item);
        this.notifyItemInserted(0);
        this.notifyItemRangeChanged(0, this.mData.size());
        this.notifyItemInserted(this.mData.size()-1);
        this.notifyDataSetChanged();
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationModel animal = mData.get(position);
        // holder.myTextView.setText(animal);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            // myTextView = itemView.findViewById(R.id.tvAnimalName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    NotificationModel getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}