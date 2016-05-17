package com.example.andriszundans.photogalleries;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;


public abstract class VisibleFragment extends Fragment {

    private static final String TAG = "TAG";

    private BroadcastReceiver photoUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
            setResultData("Notification was canceled");
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(Preferences.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(photoUpdateReceiver, intentFilter, Preferences.PERMISSION_PRIVATE, null);
    }


    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(photoUpdateReceiver);
    }



}
