package ps.moradi.inappupdate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ps.moradi.inappupdate.R;
import ps.moradi.inappupdate.help.DownloadAPK;
import ps.moradi.inappupdate.help.SharedKeys;
import ps.moradi.inappupdate.help.SharedPrefrence;
import ps.moradi.inappupdate.model.ApplicationConfig;

public class InstallActivity extends AppCompatActivity {

    private ApplicationConfig applicationConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getData();
        setContentView(R.layout.activity_install);

        initView();

        DownloadAPK downloadAPK = new DownloadAPK(getApplicationContext());
        downloadAPK.beginDownload();


    }

    private void getData() {

        String strObj = SharedPrefrence.getString(getApplicationContext(), SharedKeys.APPLICATION_CONFIG, null);
        applicationConfig = new Gson().fromJson(strObj, ApplicationConfig.class);

    }

    private void initView() {

        ImageView img_loading = findViewById(R.id.img_loading);
        TextView txt_size = findViewById(R.id.txt_size);
        TextView txt_percent = findViewById(R.id.txt_percent);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ImageView btn_cancel = findViewById(R.id.btn_cancel);


        // Loading
        loading(img_loading);

        // Size
        txt_size.setText(applicationConfig.apkSize);


        // Percent
        txt_percent.setText("0%");


        // Cancel button
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(InstallActivity.this, "Download canceled", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void loading(ImageView img_loading) {
        LayerDrawable layerDrawable = (LayerDrawable) img_loading.getDrawable();
        RotateDrawable drawable = ((RotateDrawable) layerDrawable.getDrawable(0));
        ObjectAnimator mAnimator = ObjectAnimator.ofInt(drawable, "level", 0, 10000);
        mAnimator.setDuration(400);
        mAnimator.setRepeatCount(Integer.MAX_VALUE);
        mAnimator.start();
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animator) {
                // do nothing
            }

            @Override
            public void onAnimationEnd(final Animator animator) {
                animator.start();
            }

            @Override
            public void onAnimationCancel(final Animator animator) {
                Log.v("animation", "onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(final Animator animator) {
                // do nothing
                animator.start();
            }
        });
    }


}