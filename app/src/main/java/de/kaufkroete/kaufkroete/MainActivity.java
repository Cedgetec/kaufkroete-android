package de.kaufkroete.kaufkroete;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    ViewPager mViewPager;
    ViewPagerAdapter adapter;
    TabLayout mTabLayout;

    ArrayList<KKData> shops;
    ArrayList<KKData> societies;

    public KKAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api = new KKAPI(this);

        shops = api.getShops(true);
        societies = api.getSocieties(true);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("  " + getTitle());
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setLogo(R.mipmap.ic_launcher);
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        //TODO Menu
        //mToolbar.inflateMenu(R.menu.menu);

        adapter =  new ViewPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

    }


}
