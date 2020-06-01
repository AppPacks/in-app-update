package ps.moradi.inappupdate.App;

import android.content.Context;
import android.content.res.Configuration;

import androidx.multidex.MultiDexApplication;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadLog;

import ps.moradi.inappupdate.help.FontOverride;
import ps.moradi.inappupdate.help.LocaleManager;

public class App extends MultiDexApplication {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;

        //Change language
        LocaleManager.wrap(getApplicationContext());

        settingsFont();

        fileDownloaderConfig();

    }

    private void settingsFont() {
        //Override font for whole app

        FontOverride.setDefaultFont(this, "SERIF", "font/vazir_fd_wol_noen.ttf");

    }

    private void fileDownloaderConfig() {
        // just for open the log in this demo project.
        FileDownloadLog.NEED_LOG = true;
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                ))
                .commit();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.wrap(base));
    }

}
