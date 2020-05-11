package com.example.android.wearable.wear.wearnotifications;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    public int notificationId;
    public long datetime; // Order on dashboard
    public String headline;
    // public Symbol Symbol;
    //public int[][] Data;
    public long acceptedRequestedTime;
    public float AcceptedRequestedPrice;
    public float AcceptedRequestedValue;
    public float acceptedRequestedPrice;
    public float acceptedRequestedValue;
    public float acceptedCompletedPrice;
    public float acceptedCompletedValue;
    public long acceptedCompletedTime;
    public float triggerPrice;
    // public float CurrentPrice;
    public float ConfirmedPrice;
    public float SellRequestValue;
    public float SellRequestPrice;
    public float sellRequestValue;
    public float sellRequestPrice;
    public long sellRequestTime;
    public long sellComplededTime;
    public float sellCompletedValue;
    public float sellCompletedPrice;
    public long currentTime;
    public float CurrentValue;
    public float currentPrice;
    public long SoldTime;
    public float RejectedPrice;
    public long RejectedTime;
    public float SoldPrice;
    public int state;
    public AlertState State;
    public String message;
    public AlertState LocalState;

}
