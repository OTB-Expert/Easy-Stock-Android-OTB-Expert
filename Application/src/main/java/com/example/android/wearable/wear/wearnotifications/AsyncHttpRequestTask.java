package com.example.android.wearable.wear.wearnotifications;

import android.os.AsyncTask;
import android.widget.ListAdapter;

import com.example.android.wearable.wear.wearnotifications.NotificationModel;
import com.example.android.wearable.wear.wearnotifications.StockService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AsyncHttpRequestTask extends AsyncTask<String, String, String> {

    String ServiceBaseUrl = "https://otb.expert/api/notification/demo/";
    Boolean UseMockedData = true;
    Random random = new Random();

    StockService stockService = null;
    AlertAdaptorClassic listViewAdapter = null;
    NotificationsRecyclerViewAdapter recyclerViewAdapter = null;

    static GsonBuilder gsonb = null;

    public static int LastId = 0;
    String lastRead = null;

    public AsyncHttpRequestTask(
            StockService stockService,
            AlertAdaptorClassic listViewAdapter,
            NotificationsRecyclerViewAdapter recyclerViewAdapter) {
        this.stockService = stockService;
        this.listViewAdapter = listViewAdapter;
        this.recyclerViewAdapter = recyclerViewAdapter;
    }

    @Override
    protected String doInBackground(String... uri) {
        if (gsonb == null) {
            gsonb = new GsonBuilder();
        }
        Gson gson = gsonb.create();
        if (uri[0].equals("list")) {
            return GetNotificationUpdates();
        } else if (uri[0].equals("invest") && uri[1].equals("" + stockService.SelectedItem.notificationId)) {
            return Invest(stockService.SelectedItem);
        } else if (uri[0].equals("sell") && uri[1].equals("" + stockService.SelectedItem.notificationId)) {
            return Sell(stockService.SelectedItem);
        }
        return "";
    }


    /**
     * Update list ui after process finished.
     */
    protected void onPostExecute(String responseString) {
        super.onPostExecute(responseString);

        if(UseMockedData) {
            if (stockService != null) {
                if(stockService.Notifications.size() < 20) {
                    NotificationModel notification = new NotificationModel();
                    random.setSeed(System.currentTimeMillis());
                    notification.notificationId = random.nextInt();
                    notification.headline = "This item changed " + String.format("%.2f", (random.nextFloat() * 10 - 5)) + "%";
                    notification.triggerPrice = random.nextFloat() * 100 - 50;
                    notification.message = "You might want to invest now!";
                    notification.state = AlertState.Created.ordinal();
                    notification.datetime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    stockService.Notifications.add(0, notification);
                    listViewAdapter.notifyDataSetChanged();
                    if (recyclerViewAdapter != null) {
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                    if (notification.notificationId > LastId) {
                        LastId = notification.notificationId;
                    }
                }
                return;
            }
        }

        // Real API response processing

        JSONArray j;
        Type listType = new TypeToken<ArrayList<NotificationModel>>() {
        }.getType();
        try {
            Gson gson = gsonb.create();
            j = new JSONArray(responseString);
            ArrayList<NotificationModel> items = gson.fromJson(j.toString(), listType);
            if (stockService != null) {
                for (NotificationModel item : items) {
                    stockService.Notifications.add(0, item);
                    listViewAdapter.notifyDataSetChanged();
                    if (recyclerViewAdapter!= null){
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                    if (item.notificationId > LastId) {
                        LastId = item.notificationId;
                    }
                    return;
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private String GetNotificationUpdates() {
        if(UseMockedData) {
            return null;
        }

        // Real API request

        String lastError = "";
        URL url = null;
        String responseString = "";
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(ServiceBaseUrl + "lasts/" + String.valueOf(LastId));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            responseString = ReadStream(in);

            return responseString;
        }catch(Exception ex){
            lastError = ex.getMessage();
        } finally {
            urlConnection.disconnect();
        }
        return responseString;
    }

    private String Invest(NotificationModel model) {
        if(UseMockedData) {
            if(stockService != null) {
                NotificationModel notification = stockService.GetNotificationById(model.notificationId);

                // On request arriving on server
                random.setSeed(System.currentTimeMillis());
                notification.state = AlertState.AcceptedRequested.ordinal();
                notification.acceptedRequestedPrice = notification.AcceptedRequestedPrice + (random.nextFloat() * notification.AcceptedRequestedPrice / 10 - notification.AcceptedRequestedPrice / 10 / 2);
                notification.acceptedRequestedValue = notification.AcceptedRequestedValue + (random.nextFloat() * notification.AcceptedRequestedValue / 10 - notification.AcceptedRequestedValue / 10 / 2);
                notification.acceptedRequestedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

                // On request honored (delay it random to look real)
                notification.state = AlertState.Accepted.ordinal();
                notification.acceptedCompletedPrice = notification.acceptedRequestedPrice + (random.nextFloat() * notification.acceptedRequestedPrice / 10 - notification.acceptedRequestedPrice / 10 / 2);
                notification.acceptedCompletedValue = notification.acceptedRequestedValue + (random.nextFloat() * notification.acceptedRequestedValue / 10 - notification.acceptedRequestedValue / 10 / 2);
                notification.acceptedCompletedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

                return null;
            }
        }

        // Real API request

        String lastError = "";
        URL url = null;
        String responseString = "";
        HttpURLConnection urlConnection = null;
        try {
            Gson gson = gsonb.create();
            model.LocalState = AlertState.RequestingAccept;
            url = new URL(ServiceBaseUrl + "buy/" + model.notificationId + "/" + model.AcceptedRequestedPrice + "/" + model.AcceptedRequestedValue);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            responseString = ReadStream(in);

            JSONObject j;
            Type listType = new TypeToken<NotificationModel>()
            {
            }.getType();
            try
            {
                j = new JSONObject(responseString);
                NotificationModel item = gson.fromJson(j.toString(), listType);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }catch(Exception ex){
            lastError = ex.getMessage();
        } finally {
            urlConnection.disconnect();
        }
        return responseString;
    }

    private String Sell(NotificationModel model) {
        if(UseMockedData) {
            if(stockService != null) {
                NotificationModel notification = stockService.GetNotificationById(model.notificationId);

                // On request arriving on server
                random.setSeed(System.currentTimeMillis());
                notification.state = AlertState.SellRequest.ordinal();
                notification.sellRequestPrice = notification.SellRequestPrice + (random.nextFloat() * notification.SellRequestPrice / 10 - notification.SellRequestPrice / 10 / 2);
                notification.sellRequestValue = notification.SellRequestValue + (random.nextFloat() * notification.SellRequestValue / 10 - notification.SellRequestValue / 10 / 2);
                notification.sellRequestTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

                // On request honored (delay it random to look real)
                notification.state = AlertState.Sold.ordinal();
                notification.sellCompletedPrice = notification.sellRequestPrice + (random.nextFloat() * notification.sellRequestPrice / 10 - notification.sellRequestPrice / 10 / 2);
                notification.sellCompletedValue = notification.sellRequestValue + (random.nextFloat() * notification.sellRequestValue / 10 - notification.sellRequestValue / 10 / 2);
                notification.sellComplededTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

                return null;
            }
        }

        // Real API request

        String lastError = "";
        URL url = null;
        String responseString = "";
        HttpURLConnection urlConnection = null;
        try {
            Gson gson = gsonb.create();
            model.LocalState = AlertState.RequestingAccept;
            url = new URL(ServiceBaseUrl + "sell/" + model.notificationId + "/" + model.SellRequestPrice + "/" + model.SellRequestValue);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            responseString = ReadStream(in);

            JSONObject j;
            Type listType = new TypeToken<NotificationModel>()
            {
            }.getType();
            try
            {
                j = new JSONObject(responseString);
                NotificationModel item = gson.fromJson(j.toString(), listType);
                if(stockService != null) {

                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }catch(Exception ex){
            lastError = ex.getMessage();
        } finally {
            urlConnection.disconnect();
        }
        return responseString;
    }

    private String ReadStream(InputStream inputStream) {
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String eachStringLine = null;
            while ((eachStringLine = bufferReader.readLine()) != null) {
                stringBuilder.append(eachStringLine).append("\n");
            }
        }catch (Exception ex){

        }
        return stringBuilder.toString();
    }
}