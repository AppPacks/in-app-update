package ps.moradi.inappupdate.help;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class DownloadAPK {

    private static final String TAG = DownloadAPK.class.getSimpleName();
    private final Context context;
    private long downloadID = -0L;
    private DownloadManager downloadManager;
    private int totalBytes;
    private OnDownloadListener onDownloadListener;
    private File file;

    public DownloadAPK(Context context, OnDownloadListener onDownloadListener) {
        this.context = context;
        this.onDownloadListener = onDownloadListener;
    }

    public void beginDownload(String appUrl) {

        file = new File(context.getExternalFilesDir(null), "app.apk");

        /*
       Create a DownloadManager.Request with all the information necessary to start the download
        */
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(appUrl))
                .setTitle("Update " + Helper.appName)// Title of the Download Notification
                .setDescription("Downloading...")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true)// Set if download is allowed on roaming network
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);


        /*
        Download
         */
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.

        displayLoading();

        context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void displayLoading() {
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(downloadID);
                Cursor cursor = downloadManager.query(q);
                cursor.moveToFirst();
                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                Log.d(TAG, "run: " + statusMessage(cursor));

                cursor.close();
                final int dl_progress = (bytes_downloaded * 100 / bytes_total);


                if (onDownloadListener != null) {
                    onDownloadListener.onDownloading(dl_progress, bytes_downloaded, bytes_total);
                }

            }

        }, 0, 10);
    }

    private String statusMessage(Cursor cursor) {
        String msg = "???";

        Log.d(getClass().getName(), "COLUMN_ID: " + cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
        Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: " + cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
        Log.d(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: " + cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
        Log.d(getClass().getName(), "COLUMN_LOCAL_URI: " + cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
        Log.d(getClass().getName(), "COLUMN_STATUS: " + cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)));
        Log.d(getClass().getName(), "COLUMN_REASON: " + cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)));

        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg = "Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg = "Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = "Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "Download complete!";


                break;

            default:
                msg = "Download is nowhere in sight";
                break;
        }

        return (msg);
    }


    public void setDownloadListner(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    public interface OnDownloadListener {
        void onDownloading(int dlProgress, int downloadedBytes, int totalBytes);

        void onComplete(File downloadUri);
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();

                if (onDownloadListener != null) {
                    onDownloadListener.onComplete(file);
                }
            }
        }
    };


    public void stop() {

        // Delete download
        downloadManager.remove(downloadID);

        //
    }

}
