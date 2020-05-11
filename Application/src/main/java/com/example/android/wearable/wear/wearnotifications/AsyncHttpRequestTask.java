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
import java.util.stream.Collectors;

public class AsyncHttpRequestTask extends AsyncTask<String, String, String> {

    String ServiceBaseUrl = "https://otb.expert/api/notification/demo/";

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

    private String Sell(NotificationModel model) {
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