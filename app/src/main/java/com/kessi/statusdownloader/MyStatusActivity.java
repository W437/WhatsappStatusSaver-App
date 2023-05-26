package com.kessi.statusdownloader;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ironsource.mediationsdk.IronSource;
import com.kessi.statusdownloader.fragments.StaPhotos;
import com.kessi.statusdownloader.fragments.StaVideos;
import com.kessi.statusdownloader.util.AdUtils;

import java.util.ArrayList;
import java.util.List;

public class MyStatusActivity extends AppCompatActivity {

    RelativeLayout topTab, header, tabLay;
    ImageView backIV;
    TextView topTV;

    TabLayout tabLayout;
    ViewPager viewPager;
    LinearLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_status);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        topTab = findViewById(R.id.topTab);
        RelativeLayout.LayoutParams tapTop = LayManager.relParams(MyStatusActivity.this,1080,250);
        topTab.setLayoutParams(tapTop);

        header = findViewById(R.id.header);
        RelativeLayout.LayoutParams top = LayManager.relParams(MyStatusActivity.this,1080,150);
        header.setLayoutParams(top);

        backIV = findViewById(R.id.backIV);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        LinearLayout.LayoutParams bParam = LayManager.linParams(MyStatusActivity.this, 60,60);
        backIV.setLayoutParams(bParam);

        topTV = findViewById(R.id.topTV);

        tabLay = findViewById(R.id.tabLay);
        RelativeLayout.LayoutParams tab = LayManager.relParams(MyStatusActivity.this,1080,100);
        tab.addRule(RelativeLayout.BELOW,R.id.header);
        tabLay.setLayoutParams(tab);


        viewPager = (ViewPager) findViewById(R.id.pagerdiet);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layoutdiet);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!AdUtils.isLoadIronSourceAd) {
                    AdUtils.adCounter++;
                    AdUtils.showInterAd(MyStatusActivity.this, null,0);
                } else {
                    AdUtils.adCounter++;
                    AdUtils.ironShowInterstitial(MyStatusActivity.this, null,0);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        adContainer = findViewById(R.id.banner_container);

        if (!AdUtils.isLoadIronSourceAd) {
            //admob
            AdUtils.initAd(MyStatusActivity.this);
            AdUtils.loadBannerAd(MyStatusActivity.this, adContainer);
            AdUtils.loadInterAd(MyStatusActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AdUtils.isLoadIronSourceAd) {
            AdUtils.destroyIron();
            AdUtils.ironBanner(MyStatusActivity.this, adContainer);
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

    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Photos");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabtwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabtwo.setText("Videos");
        tabLayout.getTabAt(1).setCustomView(tabtwo);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(
                getSupportFragmentManager());

        adapter.addFragment(new StaPhotos(), "Photos");
        adapter.addFragment(new StaVideos(), "Videos");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return (Fragment) this.mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return (CharSequence) this.mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        AdUtils.adCounter++;
        if (AdUtils.adCounter == AdUtils.adDisplayCounter) {
            if (!AdUtils.isLoadIronSourceAd) {
                AdUtils.showInterAd(MyStatusActivity.this, null, 0);
            } else {
                AdUtils.ironShowInterstitial(MyStatusActivity.this, null, 0);
            }
        } else {
            super.onBackPressed();
        }
    }
}
