package com.kessi.statusdownloader;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ironsource.mediationsdk.IronSource;
import com.kessi.statusdownloader.util.AdUtils;
import com.kessi.statusdownloader.util.Util;

public class DownActivity extends AppCompatActivity {

    ImageView displayIV, downloadIV;
    VideoView displayVV;
    static String fName;

    public static String path="";
    LinearLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        displayIV = (ImageView) findViewById(R.id.displayIV);
        displayVV = (VideoView) findViewById(R.id.displayVV);

        fName = getResources().getString(R.string.app_name);

        if (Util.isVideoFile(DownActivity.this,path)) {
            displayVV.setVideoPath(path);

            displayIV.setVisibility(View.INVISIBLE);
            displayVV.setVisibility(View.VISIBLE);

        }else {
            Glide.with(DownActivity.this).load(path)
                    .into(displayIV);
            displayIV.setVisibility(View.VISIBLE);
            displayVV.setVisibility(View.INVISIBLE);
        }

        downloadIV = findViewById(R.id.downloadIV);
        downloadIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AdUtils.isLoadIronSourceAd) {
                    //admob
                    AdUtils.adCounter++;
                    AdUtils.showInterAd(DownActivity.this, null,0);
                }else {
                    AdUtils.adCounter++;
                    AdUtils.ironShowInterstitial(DownActivity.this, null,0);
                }

                if (Util.isVideoFile(DownActivity.this,path)){
                    displayVV.pause();
                }

                download();
            }
        });

        LinearLayout.LayoutParams dparam = LayManager.linParams(DownActivity.this, 844,160);
        downloadIV.setLayoutParams(dparam);

        adContainer = findViewById(R.id.banner_container);

        if (!AdUtils.isLoadIronSourceAd) {
            //admob
            AdUtils.initAd(DownActivity.this);
            AdUtils.loadBannerAd(DownActivity.this, adContainer);
            AdUtils.loadInterAd(DownActivity.this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.isVideoFile(DownActivity.this,path)){
            displayVV.start();
        }
        if (AdUtils.isLoadIronSourceAd) {
            AdUtils.destroyIron();
            AdUtils.ironBanner(DownActivity.this, adContainer);
            // call the IronSource onResume method
            IronSource.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.isVideoFile(DownActivity.this,path)){
            displayVV.pause();
        }
        if (AdUtils.isLoadIronSourceAd) {
            // call the IronSource onPause method
            IronSource.onPause(this);
        }
    }


    public void download(){
        if (Util.download(DownActivity.this,path)) {
            Toast.makeText(DownActivity.this, "Status is Saved Successfully", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(DownActivity.this, "Failed to Download", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        AdUtils.adCounter++;
        if (AdUtils.adCounter == AdUtils.adDisplayCounter) {
            if (!AdUtils.isLoadIronSourceAd) {
                AdUtils.showInterAd(DownActivity.this, null, 0);
            } else {
                AdUtils.ironShowInterstitial(DownActivity.this, null, 0);
            }
        } else {
            super.onBackPressed();
        }
    }

}
