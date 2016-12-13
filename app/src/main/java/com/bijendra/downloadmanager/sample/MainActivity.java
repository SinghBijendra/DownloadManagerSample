package com.bijendra.downloadmanager.sample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {

    private String TAG=MainActivity.this.getClass().toString();
    private static final String DIRECTORY = "Download/MyPictures";
    private static final String URI_STRING = "https://cdn-images-1.medium.com/max/800/1*eDEAhfvPzlUerPe-4GE-zw.png";

    private Button download;
    private long enqueue=-1;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.activity_main);

        download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(URI_STRING);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir(DIRECTORY, uri.getLastPathSegment());
                enqueue = downloadManager.enqueue(request);

            }
        });
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }



    @Override
    protected void onStop() {
        cancel();
        super.onStop();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                downloadQueryStatus(intent);
            }
        }
    };

    private void downloadQueryStatus(Intent intent)
    {
        try {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(enqueue);
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                    case DownloadManager.STATUS_PENDING:
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        ImageView view = (ImageView) findViewById(R.id.iv_downloaded);
                        String uriString = c
                                .getString(c
                                        .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        view.setImageURI(Uri.parse(uriString));
                        break;
                    case DownloadManager.STATUS_FAILED:
                        break;
                }

            }
            c.close();
            enqueue=-1;
        }
        catch(Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
    }

    boolean isDownloading() {
        return enqueue >= 0;
    }
    void unregister() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        receiver = null;
    }
   private void cancel() {
        if (isDownloading()) {
           downloadManager.remove(enqueue);
            enqueue = -1;
        }
    }

    @Override
    protected void onDestroy() {
        unregister();
        super.onDestroy();
    }
}

