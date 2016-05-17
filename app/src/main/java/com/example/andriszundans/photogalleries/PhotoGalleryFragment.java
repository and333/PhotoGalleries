package com.example.andriszundans.photogalleries;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PhotoGalleryFragment extends VisibleFragment {

    private static final int JOB_SERVICE_ID = 1001;
    private static final long JOB_SERVICE_PERIOD = 60000;

    private RecyclerView photoRecyclerView;
    private ProgressBar progressBar;
    private List<GalleryItem> galleryItems = new ArrayList<>();



    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        photoRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return view;
    }


   private void setupAdapter() {
        if (isAdded()) {
            photoRecyclerView.setAdapter(new PhotoAdapter(galleryItems));
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchText) {
                Preferences.setSearchQuery(getActivity(), searchText);
                updateItems();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = Preferences.getSearchQuery(getActivity());
                searchView.setQuery(searchText, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);

        if(Build.VERSION.SDK_INT >= Preferences.VERSION) {

            if (hasBeenJobScheduled(getActivity())) {
                toggleItem.setTitle(R.string.stop_polling);
            } else {
                toggleItem.setTitle(R.string.start_polling);            }

        } else {
            if (PollService.isServiceAlarmOn(getActivity())) {
                toggleItem.setTitle(R.string.stop_polling);
            } else {
                toggleItem.setTitle(R.string.start_polling);
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                Preferences.setSearchQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                setUpServices();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void setUpServices() {

        if (Build.VERSION.SDK_INT >= Preferences.VERSION) {
            if(!hasBeenJobScheduled(getActivity())) {
                setJobService(getActivity());
            } else {
                stopJobService(getActivity());
            }

        } else {
            boolean shouldStartAlarm = !(PollService.isServiceAlarmOn(getActivity()));
            PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
        }

    }


    @TargetApi(21)
    private void setJobService(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(JOB_SERVICE_ID, new ComponentName(context, PollJobService.class))
                .setPeriodic(JOB_SERVICE_PERIOD)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                // survive a reboot
                .setPersisted(true)
                .build();
        jobScheduler.schedule(jobInfo);
    }


    @TargetApi(21)
    private boolean hasBeenJobScheduled(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JOB_SERVICE_ID) {
                return true;
            }
        }
        return false;
    }


    @TargetApi(21)
    private void stopJobService(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_SERVICE_ID);
    }


    private void updateItems() {
        String searchText = Preferences.getSearchQuery(getActivity());
        new NetworkConnection(searchText).execute();
    }


    private class NetworkConnection extends AsyncTask<Void, Void, List<GalleryItem>> {

        private String searchText;

        NetworkConnection(String searchText) {
            this.searchText = searchText;
        }

        @Override
        protected void onPreExecute() {
            if (isAdded()) {
                photoRecyclerView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if (searchText == null) {
                return new NetworkCon().fetchRecentPhotos();
            } else {
                return new NetworkCon().searchPhotos(searchText);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            photoRecyclerView.setVisibility(View.VISIBLE);
            galleryItems = items;
            setupAdapter();
        }

    }


    private class PhotoHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photo_gallery_item);
        }

    }


    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> galleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            this.galleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = galleryItems.get(position);
            Picasso.with(getActivity()).load(galleryItem.getUrl()).resize(120, 120)
                    .centerCrop().into(photoHolder.imageView);
        }

        @Override
        public int getItemCount() {
            return galleryItems.size() - 70;
        }

    }










}
