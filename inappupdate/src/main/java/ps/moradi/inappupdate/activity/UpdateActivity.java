package ps.moradi.inappupdate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ps.moradi.inappupdate.R;
import ps.moradi.inappupdate.dialog.AppUpdateDialog;
import ps.moradi.inappupdate.help.Helper;
import ps.moradi.inappupdate.help.SharedKeys;
import ps.moradi.inappupdate.help.SharedPrefrence;
import ps.moradi.inappupdate.model.ApplicationConfig;
import ps.moradi.inappupdate.service.APIService;
import ps.moradi.inappupdate.service.MainServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity {

    private static final String TAG = AppUpdateDialog.class.getSimpleName();
    private ApplicationConfig applicationConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getData();
        setContentView(R.layout.activity_update);

        initView();
    }

    private void getData() {

        String strObj = SharedPrefrence.getString(getApplicationContext(), SharedKeys.APPLICATION_CONFIG, null);
        applicationConfig = new Gson().fromJson(strObj, ApplicationConfig.class);

    }

    private void initView() {

        ImageView img_logo = findViewById(R.id.img_logo);
        TextView txt_app_name = findViewById(R.id.txt_app_name);
        TextView txt_size = findViewById(R.id.txt_size);
        TextView txt_changes = findViewById(R.id.txt_changes);
        Button btn_update = findViewById(R.id.btn_update);
        TextView txt_cancel = findViewById(R.id.txt_cancel);


        // Application logo
        try {
            Drawable appIcon = getPackageManager().getApplicationIcon(getApplicationInfo().packageName);
            img_logo.setImageDrawable(appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // App name
        try {
            CharSequence app_name = getPackageManager().getApplicationLabel(getApplicationInfo());
            txt_app_name.setText("Update " + app_name);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // App size
        txt_size.setText("Download size: " + applicationConfig.apkSize);


        // Changes
        txt_changes.setText(applicationConfig.changes);


        // Cancel button
        if (Helper.compareVersionNames(applicationConfig.minVersion, Helper.getVersionName(getApplicationContext())) <= 0) {
            txt_cancel.setVisibility(View.VISIBLE);
        } else {
            txt_cancel.setVisibility(View.GONE);
        }
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // Update button
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UpdateActivity.this, InstallActivity.class);
                startActivity(intent);

            }
        });
    }

//    private void downloadApp(String appUrl) {
//
//        String fileName = "APP_NAME" + "_v" + applicationConfig.versionName + ".apk";
//        final String destinationPath = Environment.getExternalStorageDirectory() + "/" + "APP_NAME" + "/" + fileName;
//
//        FileDownloadListener fileDownloadListener = new FileDownloadListener() {
//
//            @Override
//            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
//                super.connected(task, etag, isContinue, soFarBytes, totalBytes);
//                Log.d(TAG, "connected: ");
//            }
//
//            @Override
//            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
//                super.retry(task, ex, retryingTimes, soFarBytes);
//                Log.d(TAG, "retry: ");
//            }
//
//            @Override
//            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                Log.d(TAG, "pending: ");
//            }
//
//            @Override
//            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//
//                final float percent = soFarBytes / (float) totalBytes;
//                Log.d(TAG, "progress: soFarBytes=" + soFarBytes + "  totalBytes=" + totalBytes + "  curentPersent=" + (int) (percent * 100));
//
//                if (totalBytes == -1) {
//                    progressBar.setIndeterminate(true);
//                } else {
//                    progressBar.setMax(100);
//                    progressBar.setProgress((int) (percent * 100));
//                }
//
//
//                //Show ProgressBar
//                btn_send.setVisibility(View.GONE);
//
//                //Show Percent
//                progressBar.setVisibility(View.VISIBLE);
//                lnr_percent.setVisibility(View.VISIBLE);
//                txt_percent.setText((int) (percent * 100) + "");
//
//                //Hide error
//                txt_error.setVisibility(View.GONE);
//            }
//
//            @Override
//            protected void completed(BaseDownloadTask task) {
//                Log.d(TAG, "completed: ");
//
//                progressBar.setIndeterminate(false);
//
//                //Show successful message
//                txt_error.setTextColor(Color.GREEN);
//                txt_error.setText(getResources().getString(R.string.message_success_update));
//
//
//                //Install app
//                CustomIntent.installApk(UpdateActivity.this, new File(destinationPath));
//                finish();
//            }
//
//            @Override
//            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                Log.d(TAG, "paused: ");
//
//            }
//
//            @Override
//            protected void error(BaseDownloadTask task, Throwable e) {
//                Log.d(TAG, "error: ");
//                e.printStackTrace();
//                FileDownloader.getImpl().clearAllTaskData();
//                progressBar.setIndeterminate(false);
//
//                //Show Error
//                txt_error.setVisibility(View.VISIBLE);
//                txt_error.setTextColor(Color.RED);
//                txt_error.setText(getResources().getString(R.string.message_error_update));
//
//
//                //Show Btn Resume and Cancle
//                btn_send.setVisibility(View.VISIBLE);
//                btn_send.setText(getResources().getString(R.string.retry));
//            }
//
//            @Override
//            protected void warn(BaseDownloadTask task) {
//                Log.d(TAG, "warn: ");
//                progressBar.setIndeterminate(false);
//            }
//        };
//
//
//        //First clear all task data in FileDownloader
//        FileDownloader.getImpl().clearAllTaskData();
//
//
//        //Start again download
//        FileDownloader.getImpl().create(appUrl)
//                .setPath(destinationPath)
//                .setCallbackProgressTimes(300)
//                .setMinIntervalUpdateSpeed(400)
//                .setListener(fileDownloadListener)
//                .start();
//    }

    private void getApplicationConfig() {

        String url = "http://appcdn.sums.ac.ir/services/appconfig.json";

        APIService apiService = MainServiceGenerator.createService(APIService.class, url);
        Call<ApplicationConfig> call = apiService.getApplicationConfig(url);
        call.enqueue(new Callback<ApplicationConfig>() {

            @Override
            public void onResponse(Call<ApplicationConfig> call, Response<ApplicationConfig> response) {

                applicationConfig = response.body();

                //Save app config
                Gson gson = new Gson();
                SharedPrefrence.putString(getApplicationContext(), SharedKeys.APPLICATION_CONFIG, gson.toJson(applicationConfig));

                initView();
            }

            @Override
            public void onFailure(Call<ApplicationConfig> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
