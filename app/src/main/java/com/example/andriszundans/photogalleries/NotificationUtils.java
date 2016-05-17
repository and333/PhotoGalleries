package com.example.andriszundans.photogalleries;


import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.NotificationCompat;

public class NotificationUtils {


    public static void showNewPicsNotification(Context context) {

        Resources resources = context.getResources();
        Intent activityIntent = PhotoGalleryActivity.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //notificationManager.notify(NOTIFICATION_ID, notification);
        showBackgroundNotification(context, notification);
    }

    private static void showBackgroundNotification(Context context, Notification notification) {

        Intent intent = new Intent(Preferences.ACTION_SHOW_NOTIFICATION);
        intent.putExtra(Preferences.REQUEST_CODE, Preferences.REQUEST_CODE_VALUE);
        intent.putExtra(Preferences.NOTIFICATION, notification);
        context.sendOrderedBroadcast(intent, Preferences.PERMISSION_PRIVATE, null, null, Activity.RESULT_OK, null, null);

    }


}
