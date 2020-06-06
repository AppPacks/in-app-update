package ps.moradi.inappupdate.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import ps.moradi.inappupdate.R;
import ps.moradi.inappupdate.activity.UpdateActivity;
import ps.moradi.inappupdate.help.Helper;
import ps.moradi.inappupdate.help.SharedKeys;
import ps.moradi.inappupdate.help.SharedPrefrence;
import ps.moradi.inappupdate.model.ApplicationConfig;
import ps.moradi.inappupdate.service.APIService;
import ps.moradi.inappupdate.service.MainServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUpdateDialog extends Dialog {

    private static final String TAG = AppUpdateDialog.class.getSimpleName();
    private final Activity activity;
    private TextView txt_description;
    private Button btn_update;
    private Button btn_cancel;
    private ApplicationConfig applicationConfig;

    public AppUpdateDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;

        getApplicationConfig();
    }

    private void apply() {
        View parentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        View view = LayoutInflater.from(activity).inflate(R.layout.view_update, (ViewGroup) parentView, false);
        setContentView(view);
        setCancelable(false);
//        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void initView() {
        ImageView img_logo = findViewById(R.id.img_logo);
        TextView txt_app_name = findViewById(R.id.txt_app_name);
        txt_description = findViewById(R.id.txt_description);
        btn_update = findViewById(R.id.btn_update);
        btn_cancel = findViewById(R.id.btn_cancel);


        // Application logo
        try {
            Drawable appIcon = getContext().getPackageManager().getApplicationIcon(getContext().getApplicationInfo().packageName);
            img_logo.setImageDrawable(appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // App name
        try {
            CharSequence app_name = getContext().getPackageManager().getApplicationLabel(getContext().getApplicationInfo());
            txt_app_name.setText("Update " + app_name);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Description
        CharSequence app_name = getContext().getPackageManager().getApplicationLabel(getContext().getApplicationInfo());
        txt_description.setText(String.format(getContext().getResources().getString(R.string.update_description), app_name));


        // Cancel button
        if (Helper.compareVersionNames(applicationConfig.minVersion, Helper.getVersionName(getContext())) <= 0) {
            btn_cancel.setVisibility(View.VISIBLE);
        } else {
            btn_cancel.setVisibility(View.GONE);
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        // Update button
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), UpdateActivity.class);
                getContext().startActivity(intent);
                dismiss();
            }
        });

        initLinks();
    }

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
                SharedPrefrence.putString(getContext(), SharedKeys.APPLICATION_CONFIG, gson.toJson(applicationConfig));

                apply();
            }

            @Override
            public void onFailure(Call<ApplicationConfig> call, Throwable t) {

            }
        });
    }

    private void initLinks() {
        ImageView img_cafeBazar = findViewById(R.id.img_cafeBazar);
        ImageView img_googlePlay = findViewById(R.id.img_googlePlay);

        // cafebazar download link
        img_cafeBazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (applicationConfig.cafebazarUrl == null) return;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(applicationConfig.cafebazarUrl));
                getContext().startActivity(i);

            }
        });

        // google play download link
        img_googlePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (applicationConfig.googlePlayUrl == null) return;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(applicationConfig.googlePlayUrl));
                getContext().startActivity(i);

            }
        });
    }
}
