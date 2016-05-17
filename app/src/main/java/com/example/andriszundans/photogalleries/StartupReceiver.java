package com.example.andriszundans.photogalleries;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "TAG";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());

        if (Build.VERSION.SDK_INT >= Preferences.VERSION) {
            Log.i(TAG, "StartupReceiver returned");
            return;
        }

        boolean isOn = Preferences.isAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);

    }



}
