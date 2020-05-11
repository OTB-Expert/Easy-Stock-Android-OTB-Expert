package com.example.android.wearable.wear.wearnotifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.RecyclerView;

import static java.security.AccessController.getContext;

public class AlertAdaptorClassic extends ArrayAdapter<NotificationModel> implements View.OnClickListener{

        private ArrayList<NotificationModel> Notifications;
        Context mContext;
    StockService stockService;

        public void SetStockService(StockService stockService)
        {
            this.stockService = stockService;
        }
        // View lookup cache
        private static class ViewHolder {
            LinearLayout item;
            TextView headline;
            TextView notificationId;
            // Initial
            TextView datetime;
            TextView triggerPrice;
            // Accepted
            LinearLayout acceptedLayout;
            TextView acceptedTime;
            TextView acceptedPrice;
            TextView acceptedValue;
            // Placed
            LinearLayout acceptCompletedLayout;
            TextView acceptCompletedTime;
            TextView acceptCompletedPrice;
            TextView acceptCompletedValue;
            // Sell requested
            LinearLayout sellRequestedLayout;
            TextView sellRequestedTime;
            TextView sellRequestedPrice;
            TextView sellRequestedValue;
            // Sold
            LinearLayout sellCompletedLayout;
            TextView sellCompletedTime;
            TextView sellCompletedPrice;
            TextView sellCompletedValue;
            // Last
            LinearLayout curentLayout;
            TextView currentTime;
            TextView currentPrice;
            // Sell
            Button sellButton;

            // Actions
            LinearLayout acceptLayout;
            LinearLayout sellLayout;

            TextView state;
            Button acceptMin;
            Button acceptMax;
            Button reject;
        }

        public AlertAdaptorClassic(ArrayList<NotificationModel> data, Context context) {
            super(context, R.layout.row_item, data);
            this.Notifications = data;

            NotificationModel newItem = new NotificationModel();
            newItem.headline = "Item " + String.valueOf(this.Notifications.size());
            // this.Notifications.add(newItem);
            this.mContext=context;
        }

        NotificationModel SelectedItem = null;
        @Override
        public void onClick(View view) {
           int id = view.getId();
            Log.d("Adaptor", "onItemSelected(): id: " + id);
            if(id == R.id.item){
                TextView notificationId = (TextView) view.findViewById(R.id.notificationId);
                LinearLayout moreLayout = (LinearLayout) view.findViewById(R.id.moreLayout);
                stockService.SelectedItem = null;
                String val = notificationId.getText().toString();
                int value = Integer.valueOf(val);
                for (NotificationModel notification: stockService.Notifications) {
                    if(notification.notificationId == value){
                        stockService.SelectedItem = notification;
                    }
                }

                moreLayout = (LinearLayout) view.findViewById(R.id.moreLayout);
                moreLayout.setVisibility(moreLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE );
                return;
            }
            if(id == R.id.acceptMax){
                if(stockService.SelectedItem != null) {
                    stockService.SelectedItem.AcceptedRequestedPrice = stockService.SelectedItem.currentPrice > 0 ? stockService.SelectedItem.currentPrice : stockService.SelectedItem.triggerPrice;
                    stockService.SelectedItem.AcceptedRequestedValue = 10 * stockService.SelectedItem.currentPrice > 0 ? stockService.SelectedItem.currentPrice : stockService.SelectedItem.triggerPrice;
                    new AsyncHttpRequestTask(stockService, this, null).execute("invest", "" + stockService.SelectedItem.notificationId);
                }
                return;
            }
            if(id == R.id.acceptMin){
                if(stockService.SelectedItem != null) {
                    stockService.SelectedItem.AcceptedRequestedPrice = stockService.SelectedItem.currentPrice > 0 ? stockService.SelectedItem.currentPrice : stockService.SelectedItem.triggerPrice;
                    stockService.SelectedItem.AcceptedRequestedValue = 30 * stockService.SelectedItem.currentPrice > 0 ? stockService.SelectedItem.currentPrice : stockService.SelectedItem.triggerPrice;
                    new AsyncHttpRequestTask(stockService, this, null).execute("invest", "" + stockService.SelectedItem.notificationId);
                }
                return;
            }
            if(id == R.id.sellButton){
                if(stockService.SelectedItem != null) {
                    stockService.SelectedItem.SellRequestPrice = stockService.SelectedItem.currentPrice > 0 ? stockService.SelectedItem.currentPrice : stockService.SelectedItem.triggerPrice;
                    stockService.SelectedItem.SellRequestValue = 30 * stockService.SelectedItem.currentPrice > 0 ? stockService.SelectedItem.currentPrice : stockService.SelectedItem.triggerPrice;
                    new AsyncHttpRequestTask(stockService, this, null).execute("sell", "" + stockService.SelectedItem.notificationId);
                }
                return;
            }


            switch (id)
            {
                case R.id.acceptMin:
                    Snackbar.make(view, "Accepted", Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                    break;
                case R.id.reject:
                    Snackbar.make(view, "Rejected", Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                    break;
            }
        }

    public String GetState(NotificationModel model) {
            if(model == null){
                return "--";
            }

        switch (AlertState.values()[model.state]){
            case Accepted:
                return "Accepted";
            case AcceptedRequested:
                return "Order sent";
            case Created:
                return "Received";
            case Expired:
                return "Expired";
            case Rejected:
                return "You Rejected";
            case RequestingAccept:
                return "RequestingAccept";
            case Retracted:
                return "You Retracted";
            case SellRequest:
                return "You Requested to sell";
            case Sold:
                return "Sold !!!";
        }
        return "Unknown status!";
    }
    public String Round(NotificationModel model, int times) {
        float value = (model.currentPrice != 0 ? model.currentPrice : model.triggerPrice) * times;
        if(value > 100) {
            return "£ " + String.format("%.1f", value);
        }
        else {
            return "£ " + String.format("%.2f", value);
        }
    }
        public String TimeAgo(long seconds) {
            long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            if (seconds > now || seconds <= 0) {
                return "in the future";
            }
            String timeAgo = "";
            long diff = now - seconds;
            if(diff / (60*60*24) >= 1) {
                int units = (int)Math.floor(diff/(60*60*24));
                timeAgo = String.valueOf(units) + " day" + (units>1?"s":"") + " ago";
                return timeAgo;
            }
            if(diff / (60*60) >= 1) {
                int units = (int)Math.floor(diff/(60*60));
                timeAgo = String.valueOf(units) + " hour" + (units>1?"s":"") + " ago";
                return timeAgo;
            }
            if(diff / 60 >= 1) {
                int units = (int)Math.floor(diff/(60));
                timeAgo = String.valueOf(units) + " minute" + (units>1?"s":"") + " ago";
                return timeAgo;
            }
            timeAgo = String.valueOf(diff) + " seconds ago";
            return timeAgo;
        };

    public void Add(NotificationModel item)
    {
        this.Notifications.add(0, item);
        this.notifyDataSetChanged();
    }

        private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return InflateView(position, convertView, parent, true);
    }


        public View InflateView(int position, View convertView, ViewGroup parent, Boolean isList) {
            // Get the data item for this position
            NotificationModel dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.row_item, parent, false);
                viewHolder.item = (LinearLayout) convertView.findViewById(R.id.item);
                viewHolder.notificationId = (TextView) convertView.findViewById(R.id.notificationId);
                viewHolder.headline = (TextView) convertView.findViewById(R.id.headline);
                // Initial
                viewHolder.datetime = (TextView) convertView.findViewById(R.id.datetime);
                viewHolder.triggerPrice = (TextView) convertView.findViewById(R.id.triggerPrice);
                // Accepted
                viewHolder.acceptedLayout = (LinearLayout) convertView.findViewById(R.id.acceptedLayout);
                viewHolder.acceptedTime = (TextView) convertView.findViewById(R.id.acceptedTime);
                viewHolder.acceptedPrice = (TextView) convertView.findViewById(R.id.acceptedPrice);
                viewHolder.acceptedValue = (TextView) convertView.findViewById(R.id.acceptedValue);
                // Placed
                viewHolder.acceptCompletedLayout = (LinearLayout) convertView.findViewById(R.id.acceptCompletedLayout);
                viewHolder.acceptCompletedTime = (TextView) convertView.findViewById(R.id.acceptCompletedTime);
                viewHolder.acceptCompletedPrice = (TextView) convertView.findViewById(R.id.acceptCompletedPrice);
                viewHolder.acceptCompletedValue = (TextView) convertView.findViewById(R.id.acceptCompletedValue);
                // Sell
                viewHolder.sellRequestedLayout = (LinearLayout) convertView.findViewById(R.id.sellRequestLayout);
                viewHolder.sellRequestedTime = (TextView) convertView.findViewById(R.id.sellRequestedTime);
                viewHolder.sellRequestedPrice = (TextView) convertView.findViewById(R.id.sellRequestedPrice);
                viewHolder.sellRequestedValue = (TextView) convertView.findViewById(R.id.sellRequestValue);
                // Sold
                viewHolder.sellCompletedLayout = (LinearLayout) convertView.findViewById(R.id.sellCompletedLayout);
                viewHolder.sellCompletedTime = (TextView) convertView.findViewById(R.id.sellCompletedTime);
                viewHolder.sellCompletedPrice = (TextView) convertView.findViewById(R.id.sellCompletedPrice);
                viewHolder.sellCompletedValue = (TextView) convertView.findViewById(R.id.sellCompletedValue);
                // Last
                viewHolder.curentLayout = (LinearLayout) convertView.findViewById(R.id.curentLayout);
                viewHolder.currentTime = (TextView) convertView.findViewById(R.id.currentTime);
                viewHolder.currentPrice = (TextView) convertView.findViewById(R.id.currentPrice);

                viewHolder.sellButton = (Button) convertView.findViewById(R.id.sellButton);

                viewHolder.acceptLayout = (LinearLayout) convertView.findViewById(R.id.acceptLayout);
                viewHolder.sellLayout = (LinearLayout) convertView.findViewById(R.id.sellLayout);

                viewHolder.state = (TextView) convertView.findViewById(R.id.state);
                viewHolder.acceptMin = (Button) convertView.findViewById(R.id.acceptMin);
                viewHolder.acceptMax = (Button) convertView.findViewById(R.id.acceptMax);
                viewHolder.reject = (Button) convertView.findViewById(R.id.reject);

                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result=convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            viewHolder.item.setOnClickListener(this);
            viewHolder.headline.setText(dataModel.headline);
            viewHolder.notificationId.setText(String.valueOf(dataModel.notificationId));
            // Initial
            viewHolder.datetime.setText(TimeAgo(dataModel.datetime));
            viewHolder.triggerPrice.setText(String.valueOf(dataModel.triggerPrice));
            // Accepted
            viewHolder.acceptedLayout.setVisibility(dataModel.acceptedRequestedValue != 0 ? View.VISIBLE : View.GONE);
            viewHolder.acceptedTime.setText(TimeAgo(dataModel.acceptedRequestedTime));
            viewHolder.acceptedPrice.setText(String.valueOf(dataModel.acceptedRequestedPrice));
            viewHolder.acceptedValue.setText(String.valueOf(dataModel.acceptedRequestedValue));
            // Accepted
            viewHolder.acceptCompletedLayout.setVisibility(dataModel.acceptedCompletedValue != 0 ? View.VISIBLE : View.GONE);
            viewHolder.acceptCompletedTime.setText(TimeAgo(dataModel.acceptedCompletedTime));
            viewHolder.acceptCompletedPrice.setText(String.valueOf(dataModel.acceptedCompletedPrice));
            viewHolder.acceptCompletedValue.setText(String.valueOf(dataModel.acceptedCompletedValue));
            // Accepted
            viewHolder.sellRequestedLayout.setVisibility(dataModel.sellRequestValue != 0 ? View.VISIBLE : View.GONE);
            viewHolder.sellRequestedTime.setText(TimeAgo(dataModel.sellRequestTime));
            viewHolder.sellRequestedPrice.setText(String.valueOf(dataModel.sellRequestPrice));
            viewHolder.sellRequestedValue.setText(String.valueOf(dataModel.sellRequestValue));
            // Sold
            viewHolder.sellCompletedLayout.setVisibility(dataModel.sellCompletedValue != 0 ? View.VISIBLE : View.GONE);
            viewHolder.sellCompletedTime.setText(TimeAgo(dataModel.sellComplededTime));
            viewHolder.sellCompletedPrice.setText(String.valueOf(dataModel.sellCompletedPrice));
            viewHolder.sellCompletedValue.setText(String.valueOf(dataModel.sellCompletedValue));
            // Last
            viewHolder.curentLayout.setVisibility(dataModel.currentPrice != 0 ? View.VISIBLE : View.GONE);
            viewHolder.currentTime.setText(TimeAgo(dataModel.currentTime));
            viewHolder.currentPrice.setText(String.valueOf(dataModel.currentPrice));

            viewHolder.sellLayout.setVisibility(dataModel.sellCompletedValue == 0 && dataModel.acceptedCompletedValue != 0 ? View.VISIBLE : View.GONE);
            viewHolder.acceptLayout.setVisibility(dataModel.acceptedCompletedValue != 0 ? View.GONE : View.VISIBLE);

            viewHolder.sellButton.setText("Sell at £ " + String.format("%.1f",((dataModel.currentPrice/(dataModel.acceptedCompletedPrice/100)) * dataModel.acceptedCompletedValue + dataModel.acceptedCompletedValue)));

            viewHolder.state.setText(GetState(dataModel));
            viewHolder.sellButton.setOnClickListener(this);
            viewHolder.acceptMin.setOnClickListener(this);
            viewHolder.acceptMin.setText(Round(dataModel, 10));
            viewHolder.acceptMax.setOnClickListener(this);
            viewHolder.acceptMax.setText(Round(dataModel, 30));
            viewHolder.reject.setOnClickListener(this);
            // Return the completed view to render on screen
            return convertView;
        }
    }
