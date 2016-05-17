package com.example.andriszundans.photogalleries;


import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "TAG";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "received result: " + getResultCode());
        if (getResultCode() != Activity.RESULT_OK) {
            // A foreground activity cancelled the broadcast
            return;
        }

        int requestCode = intent.getIntExtra(Preferences.REQUEST_CODE, Preferences.REQUEST_CODE_VALUE);
        Notification notification = (Notification) intent.getParcelableExtra(Preferences.NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Preferences.REQUEST_CODE_VALUE, notification);


    }




}
