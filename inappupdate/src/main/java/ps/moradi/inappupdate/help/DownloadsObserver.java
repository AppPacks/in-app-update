package ps.moradi.inappupdate.help;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;

public class DownloadsObserver extends ContentObserver {

    public static final String LOG_TAG = DownloadsObserver.class.getSimpleName();

    private static final int flags =
            FileObserver.CLOSE_WRITE
                    | FileObserver.OPEN
                    | FileObserver.MODIFY
                    | FileObserver.DELETE
                    | FileObserver.MOVED_FROM;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownloadsObserver(Handler handler) {
        super(handler);
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d(LOG_TAG, "Download " + uri + " updated");
    }
}