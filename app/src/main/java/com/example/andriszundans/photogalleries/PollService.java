package com.example.andriszundans.photogalleries;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;


import java.util.List;

public class PollService extends IntentService {

    private static final String TAG = "TAG";
    private static final long POLL_INTERVAL = 60000;


    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }


    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        Preferences.setAlarmOn(context, isOn);
    }


    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = PollService.newIntent(context);
        // if the PendingIntent does not already exist, return null instead of creating it.
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }


    public PollService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "PollService (IntentService) has been started");
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        String query = Preferences.getSearchQuery(this);
        String lastResultId = Preferences.getLastResultId(this);
        List<GalleryItem> items;

        if (query == null) {
            items = new NetworkCon().fetchRecentPhotos();
        } else {
            items = new NetworkCon().searchPhotos(query);
        }

        if (items.size() == 0) {
            return;
        }

        String resultId = items.get(0).getId();
        if (!resultId.equals(lastResultId)) {
            NotificationUtils.showNewPicsNotification(this);
            Preferences.setLastResultId(this, resultId);
        }

    }


    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = manager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && manager.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }







}
