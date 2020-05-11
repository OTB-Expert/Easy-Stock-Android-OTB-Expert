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

    public NotificationModel GetNotificationById(int notificationId) {
        for (NotificationModel notification: Notifications) {
            if(notification.notificationId == notificationId){
                return notification;
            }
        }
        return null;
    }

    public void SelectNotificationById(int notificationId) {
        SelectedItem = GetNotificationById(notificationId);
    }
}
