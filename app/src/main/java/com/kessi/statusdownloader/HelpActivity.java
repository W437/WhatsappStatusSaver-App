package com.kessi.statusdownloader;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ironsource.mediationsdk.IronSource;
import com.kessi.statusdownloader.util.AdUtils;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;


public class HelpActivity extends AppCompatActivity {

    RelativeLayout header;
    ImageView back;
    TextView topTV;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.help_1,R.drawable.help_2, R.drawable.help_3, R.drawable.help_4};

    LinearLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        header = findViewById(R.id.header);
        RelativeLayout.LayoutParams paramTop = LayManager.relParams(HelpActivity.this, 1080, 150);
        header.setLayoutParams(paramTop);

        topTV = findViewById(R.id.topTV);

        back = findViewById(R.id.back);
        LinearLayout.LayoutParams backParam = LayManager.linParams(HelpActivity.this, 60, 60);
        back.setLayoutParams(backParam);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);

        adContainer = findViewById(R.id.banner_container);

        if (!AdUtils.isLoadIronSourceAd) {
            //admob
            AdUtils.initAd(HelpActivity.this);
            AdUtils.loadBannerAd(HelpActivity.this, adContainer);
            AdUtils.loadInterAd(HelpActivity.this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AdUtils.isLoadIronSourceAd) {
            AdUtils.destroyIron();
            AdUtils.ironBanner(HelpActivity.this, adContainer);
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

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(sampleImages[position]);
        }
    };

    @Override
    public void onBackPressed() {
        AdUtils.adCounter++;
        if (AdUtils.adCounter == AdUtils.adDisplayCounter) {
            if (!AdUtils.isLoadIronSourceAd) {
                AdUtils.showInterAd(HelpActivity.this, null, 0);
            } else {
                AdUtils.ironShowInterstitial(HelpActivity.this, null, 0);
            }
        } else {
            super.onBackPressed();
        }
    }
}
