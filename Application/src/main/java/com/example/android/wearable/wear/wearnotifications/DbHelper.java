package com.example.android.wearable.wear.wearnotifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "notifications";
    public static final String CONTACTS_COLUMN_ID = "notificationId";
    private HashMap hp;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table notifications " +
                        "(notificationId integer, " +
                        "datetime integer, " +
                        "headline text, " +
                        "acceptedRequestedTime integer, " +
                        "acceptedRequestedPrice real," +
                        "acceptedRequestedValue real," +
                        "acceptedCompletedPrice real," +
                        "acceptedCompletedValue real," +
                        "acceptedCompletedTime  integer," +
                        "triggerPrice real," +
                        "sellRequestValue real," +
                        "sellRequestPrice real," +
                        "sellRequestTime integer," +
                        "sellComplededTime integer," +
                        "sellCompletedValue real," +
                        "sellCompletedPrice real," +
                        "currentPrice real," +
                        "state integer," +
                        "message text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS notifications");
        onCreate(db);
    }

    public boolean upsertItem(NotificationModel nodel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        boolean isUpdate = countById(nodel.notificationId) > 0;

        if(nodel.notificationId > 0) {
            contentValues.put("notificationId", nodel.notificationId);
        }
        contentValues.put("datetime", nodel.datetime);
        contentValues.put("headline", nodel.headline);
        contentValues.put("acceptedRequestedTime", nodel.acceptedRequestedTime);
        contentValues.put("acceptedRequestedPrice", nodel.acceptedRequestedPrice);
        contentValues.put("acceptedRequestedValue", nodel.acceptedRequestedValue);
        contentValues.put("acceptedCompletedPrice", nodel.acceptedCompletedPrice);
        contentValues.put("acceptedCompletedValue", nodel.acceptedCompletedValue);
        contentValues.put("acceptedCompletedTime", nodel.acceptedCompletedTime);
        contentValues.put("triggerPrice", nodel.triggerPrice);
        contentValues.put("sellRequestValue", nodel.sellRequestValue);
        contentValues.put("sellRequestPrice", nodel.sellRequestPrice);
        contentValues.put("sellRequestTime", nodel.sellRequestTime);
        contentValues.put("sellComplededTime", nodel.sellComplededTime);
        contentValues.put("sellCompletedValue", nodel.sellCompletedValue);
        contentValues.put("sellCompletedPrice", nodel.sellCompletedPrice);
        contentValues.put("currentPrice", nodel.currentPrice);
        contentValues.put("state", nodel.state);
        contentValues.put( "message", nodel.message);

        if(isUpdate) {
            db.update("notifications", contentValues, "notificationId = ? ", new String[] { Integer.toString(nodel.notificationId) } );
        } else {
            db.insert("notifications", null, contentValues);
        }
        return true;
    }

    public int countById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select notificationId from notifications where notificationId="+id+"", null );
        return res.getCount();
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notifications where notificationId="+id+"", null );
        return res;
    }

    public int numberOfItems(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public Integer deleteItem(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("notifications",
                "notificationId = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<NotificationModel> getAllItems() {
        ArrayList<NotificationModel> array_list = new ArrayList<NotificationModel>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notifications", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            NotificationModel item = new NotificationModel();
            item.notificationId = res.getInt(res.getColumnIndex("notificationId"));
            item.datetime = res.getLong(res.getColumnIndex("datetime"));
            item.headline = res.getString(res.getColumnIndex("headline"));
            item.acceptedRequestedTime = res.getLong(res.getColumnIndex("acceptedRequestedTime"));
            item.acceptedRequestedPrice = res.getFloat(res.getColumnIndex("acceptedRequestedPrice"));
            item.acceptedRequestedValue = res.getFloat(res.getColumnIndex("acceptedRequestedValue"));
            item.acceptedCompletedPrice = res.getFloat(res.getColumnIndex("acceptedCompletedPrice"));
            item.acceptedCompletedValue = res.getFloat(res.getColumnIndex("acceptedCompletedValue"));
            item.acceptedCompletedTime = res.getLong(res.getColumnIndex("acceptedCompletedTime"));
            item.triggerPrice = res.getFloat(res.getColumnIndex("triggerPrice"));
            item.sellRequestValue = res.getFloat(res.getColumnIndex("sellRequestValue"));
            item.sellRequestPrice = res.getFloat(res.getColumnIndex("sellRequestPrice"));
            item.sellRequestTime = res.getLong(res.getColumnIndex("sellRequestTime"));
            item.sellComplededTime = res.getLong(res.getColumnIndex("sellComplededTime"));
            item.sellCompletedValue = res.getFloat(res.getColumnIndex("sellCompletedValue"));
            item.sellCompletedPrice = res.getFloat(res.getColumnIndex("sellCompletedPrice"));
            item.currentPrice = res.getFloat(res.getColumnIndex("currentPrice"));
            item.state = res.getInt(res.getColumnIndex("state"));
            item.message = res.getString(res.getColumnIndex("message"));

            array_list.add(item);
            res.moveToNext();
        }
        return array_list;
    }

    public void upsertAllItems(ArrayList<NotificationModel> notifications) {
        int count = 0;
        for (NotificationModel item: notifications) {
            if(count < 5) {
                upsertItem(item);
            }
            count++;
        }
    }
}
