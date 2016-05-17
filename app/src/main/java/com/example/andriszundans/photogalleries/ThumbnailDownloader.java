package com.example.andriszundans.photogalleries;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler requestHandler;
    private Handler responseHandler;

    private ConcurrentMap<T,String> requestMap = new ConcurrentHashMap<>();
    private ThumbnailDownloadListener<T> downloadListener;


    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }


    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        this.responseHandler = responseHandler;
    }


    public void setDownloadListener(ThumbnailDownloadListener<T> downloadListener) {
        this.downloadListener = downloadListener;
    }


    @Override
    protected void onLooperPrepared() {
        requestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + requestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }



    private void handleRequest(final T target) {
        try {
            final String url = requestMap.get(target);
            if (url == null) {
                return;
            }

            byte[] bitmapBytes = new NetworkCon().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.e(TAG, "Bitmap created");

            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(target) != url) {
                        return;
                    }
                    requestMap.remove(url);
                    downloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });

        } catch (IOException ex) {
            Log.e(TAG, "Error downloading image", ex);
        }
    }



    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            requestMap.remove(target);
        } else {
            requestMap.put(target, url);
            Message msg = requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target);
            msg.sendToTarget();
        }
    }


    public void clearQueue() {
        requestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }



}
