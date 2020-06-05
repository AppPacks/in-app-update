package ps.moradi.inappupdate;

import android.app.Activity;

import ps.moradi.inappupdate.dialog.AppUpdateDialog;

public class CheckNewVersion {

    private final Activity activity;

    public CheckNewVersion(Activity activity) {
        this.activity = activity;
    }

    public void apply() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(activity);
        appUpdateDialog.show();
    }
}
