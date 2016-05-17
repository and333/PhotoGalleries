package com.example.andriszundans.photogalleries;


import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;

public class Preferences {

    public static final String ACTION_SHOW_NOTIFICATION = "com.example.andriszundans.photogalleries.ACTION_SHOW_NOTIFICATION";
    public static final String PERMISSION_PRIVATE = "com.example.andriszundans.photogalleries.PRIVATE";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final int REQUEST_CODE_VALUE = 101;

    public static final int VERSION = Build.VERSION_CODES.M;


    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";


    public static String getSearchQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, null);
    }

    public static void setSearchQuery(Context context, String searchQuery) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, searchQuery)
                .apply();
    }


    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID, null);
    }

    public static void setLastResultId(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }


    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isAlarmOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isAlarmOn)
                .apply();
    }


}
