package com.kessi.statusdownloader;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ironsource.mediationsdk.IronSource;
import com.kessi.statusdownloader.util.AdUtils;
import com.kessi.statusdownloader.util.SharedPrefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String[] permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    ImageView wappBtn, recentBtn, statusBtn, rateBtn, shareBtn, policyBtn, moreBtn, helpBtn, modeIV;
    Animation blink;

    Dialog alert_dialog;
    LinearLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!checkPermissions(this, permissionsList)) {
            ActivityCompat.requestPermissions(this, permissionsList, 21);
        }

        wappBtn = findViewById(R.id.wappBtn);
        blink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        wappBtn.startAnimation(blink);

        wappBtn.setOnClickListener(this);
        LinearLayout.LayoutParams paramsW = LayManager.linParams(MainActivity.this, 561, 346);
        wappBtn.setLayoutParams(paramsW);

        recentBtn = findViewById(R.id.recentBtn);
        recentBtn.setOnClickListener(this);

        statusBtn = findViewById(R.id.statusBtn);
        statusBtn.setOnClickListener(this);

        modeIV = findViewById(R.id.modeBtn);
        modeIV.setOnClickListener(this);

        int mode = SharedPrefs.getAppNightDayMode(this);
        if (mode == AppCompatDelegate.MODE_NIGHT_YES){
            modeIV.setImageResource(R.drawable.light_mode);
        }else {
            modeIV.setImageResource(R.drawable.dark_mode);
        }

        LinearLayout.LayoutParams paramsBtn = LayManager.linParams(MainActivity.this, 532, 442);
        recentBtn.setLayoutParams(paramsBtn);
        statusBtn.setLayoutParams(paramsBtn);

        rateBtn = findViewById(R.id.rateBtn);
        rateBtn.setOnClickListener(this);

        shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(this);

        policyBtn = findViewById(R.id.policyBtn);
        policyBtn.setOnClickListener(this);

        moreBtn = findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(this);

        LinearLayout.LayoutParams paramsSh = LayManager.linParams(MainActivity.this, 140, 220);
        rateBtn.setLayoutParams(paramsSh);
        shareBtn.setLayoutParams(paramsSh);
        policyBtn.setLayoutParams(paramsSh);
        moreBtn.setLayoutParams(paramsSh);
        modeIV.setLayoutParams(paramsSh);

        helpBtn = findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(this);
        LinearLayout.LayoutParams paramsH = LayManager.linParams(MainActivity.this, 844, 160);
        helpBtn.setLayoutParams(paramsH);

        wappAlert();

        adContainer = findViewById(R.id.banner_container);

        if (!AdUtils.isLoadIronSourceAd) {
            //admob
            AdUtils.initAd(MainActivity.this);
            AdUtils.loadBannerAd(MainActivity.this, adContainer);
            AdUtils.loadInterAd(MainActivity.this);
        } else {
            //ironSource Ads
            AdUtils.inItIron(MainActivity.this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AdUtils.isLoadIronSourceAd) {
            AdUtils.destroyIron();
            AdUtils.ironBanner(MainActivity.this, adContainer);
            // call the IronSource onResume method
            IronSource.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (AdUtils.isLoadIronSourceAd) {
            // call the IronSource onPause method
            IronSource.onPause(this);
        }
    }

    void wappAlert(){
        alert_dialog = new Dialog(MainActivity.this);
        alert_dialog.setContentView(R.layout.popup_lay);

        alert_dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout alertLay =  alert_dialog.findViewById(R.id.alertLay);
        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 900 / 1080,
                getResources().getDisplayMetrics().heightPixels * 600 / 1920);
        alertLay.setLayoutParams(para);


        ImageView btn_wapp = alert_dialog.findViewById(R.id.btn_wapp);
        ImageView btn_wapp_bus = alert_dialog.findViewById(R.id.btn_wapp_bus);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 822 / 1080,
                getResources().getDisplayMetrics().heightPixels * 230 / 1920);
        btn_wapp.setLayoutParams(params2);
        btn_wapp_bus.setLayoutParams(params2);


        btn_wapp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                try {
                    startActivity(getPackageManager().getLaunchIntentForPackage("com.whatsapp"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.install_wapp), Toast.LENGTH_SHORT).show();
                }
                alert_dialog.dismiss();

            }
        });

        btn_wapp_bus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    startActivity(getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.install_wbapp), Toast.LENGTH_SHORT).show();
                }
                alert_dialog.dismiss();

            }
        });

    }


    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21) {
            if (!checkPermissions(this, permissionsList)) {
                ActivityCompat.requestPermissions(this, permissionsList, 21);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wappBtn:
                alert_dialog.show();
                break;

            case R.id.recentBtn:
                    startActivityes(new Intent(MainActivity.this, RecStatusActivity.class));
                break;

            case R.id.statusBtn:
                startActivityes(new Intent(MainActivity.this, MyStatusActivity.class));
                break;

            case R.id.rateBtn:
                rateUs();
                break;

            case R.id.shareBtn:
                shareApp();
                break;

            case R.id.policyBtn:
                startActivityes(new Intent(MainActivity.this, PrivacyActivity.class));
                break;

            case R.id.moreBtn:
                moreApp();
                break;

            case R.id.helpBtn:
                startActivityes(new Intent(MainActivity.this, HelpActivity.class));
                break;

            case R.id.modeBtn:
                Log.e( "onClick: ", "click");
                int mode = SharedPrefs.getAppNightDayMode(this);
                if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SharedPrefs.setInt(this, SharedPrefs.PREF_NIGHT_MODE,AppCompatDelegate.MODE_NIGHT_YES);
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    SharedPrefs.setInt(this, SharedPrefs.PREF_NIGHT_MODE,AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;

        }
    }

    void startActivityes(Intent intent) {
        if (!AdUtils.isLoadIronSourceAd) {
            AdUtils.adCounter++;
            AdUtils.showInterAd(MainActivity.this, intent,0);
        } else {
            AdUtils.adCounter++;
            AdUtils.ironShowInterstitial(MainActivity.this, intent,0);
        }
    }


    public void shareApp() {
        String shareBody = "https://play.google.com/store/apps/details?id="
                + getApplicationContext().getPackageName();

        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent
                .putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "(InstaGallery for download video from photos from Instagram. Open it in Google Play Store to Download the Application)");

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void rateUs() {
        Uri uri = Uri.parse("market://details?id="
                + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + getApplicationContext().getPackageName())));
        }
    }

    public void moreApp() {
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.slide.photo")));
    }
}
