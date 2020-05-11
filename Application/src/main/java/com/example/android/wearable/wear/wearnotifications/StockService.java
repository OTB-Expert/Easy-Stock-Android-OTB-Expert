package com.example.android.wearable.wear.wearnotifications;

import java.util.ArrayList;

public class StockService {
    public NotificationModel SelectedItem = null;
    public ArrayList<NotificationModel> Notifications = null;
    public StockService()
    {
        Notifications = new ArrayList<>();
    }

    public Boolean updateNotifications(ArrayList<NotificationModel> newNotifications){
        return true;
    }
}
