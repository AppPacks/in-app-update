package ps.moradi.inappupdate.help;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class CustomIntent {

    public static void installApk(Activity activity, File apkFile) {

        Uri appUri;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                appUri = FileProvider.getUriForFile(activity, activity.getApplicationInfo().packageName + ".fileprovider", apkFile);

                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setDataAndType(appUri, "application/vnd.android.package-archive");
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                appUri = Uri.fromFile(apkFile);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(appUri, "application/vnd.android.package-archive");
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
