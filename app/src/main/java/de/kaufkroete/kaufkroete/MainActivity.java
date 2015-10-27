package de.kaufkroete.kaufkroete;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    public ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;

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

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.slidingtabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        tabs.setViewPager(pager);

    }


}
