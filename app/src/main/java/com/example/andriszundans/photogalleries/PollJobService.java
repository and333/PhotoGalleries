package com.example.andriszundans.photogalleries;


import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;


@TargetApi(21)
public class PollJobService extends JobService {

    private static final String TAG = "TAG";


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "PollJobService(JobService) has been started");
        new PollTask(this).execute(params);
        // false means job is completed, true means job is still working
        return true;

    }


    @Override
    public boolean onStopJob(JobParameters params) {
        // when your job needs to be interrupted
        // returning true means job should be rescheduled to run again in the future
        return false;
    }



    private class PollTask extends AsyncTask<JobParameters, Void, Void> {

        private Context context;

        public PollTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(JobParameters... params) {

            JobParameters jobParams = params[0];

            String query = Preferences.getSearchQuery(context);
            String lastResultId = Preferences.getLastResultId(context);
            List<GalleryItem> items;

            if (query == null) {
                items = new NetworkCon().fetchRecentPhotos();
            } else {
                items = new NetworkCon().searchPhotos(query);
            }

            if (items.size() == 0) {
                cancel(true);
            }

            String resultId = items.get(0).getId();
            if (!resultId.equals(lastResultId)) {
                NotificationUtils.showNewPicsNotification(context);
                Preferences.setLastResultId(context, resultId);
            }

            // true means job was not able to finish and should be rescheduled
            jobFinished(jobParams, false);
            return null;

        }

    }



}
