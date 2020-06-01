package ps.moradi.inappupdate.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
    private TextView txt_error;
    private Button btn_send;
    private TextView txt_cancel;
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
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
    }

    private void initView() {
        TextView txt_appVersion = findViewById(R.id.txt_appVersion);
        txt_cancel = findViewById(R.id.txt_cancel);
        btn_send = findViewById(R.id.btn_send);
        txt_error = findViewById(R.id.txt_error);


        //App Version
        String currentVersion = Helper.getVersionName(activity);
        txt_appVersion.setText(String.format(activity.getResources().getString(R.string.message_new_version), currentVersion, applicationConfig.versionName));


        //Cancel
        if (Helper.compareVersionNames(applicationConfig.minVersion, Helper.getVersionName(getContext())) <= 0) {
            txt_cancel.setVisibility(View.VISIBLE);
        } else {
            txt_cancel.setVisibility(View.GONE);
        }
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        //Send Button
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), UpdateActivity.class);
                getContext().startActivity(intent);

            }
        });
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
                Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
