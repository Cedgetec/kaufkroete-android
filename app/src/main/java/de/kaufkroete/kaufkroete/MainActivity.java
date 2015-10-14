package de.kaufkroete.kaufkroete;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


public class MainActivity extends FragmentActivity {

    Toolbar mToolbar;
    public ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getTitle());
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setLogo(R.mipmap.ic_kaufkroete);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //TODO Menu
        // mToolbar.set

        adapter =  new ViewPagerAdapter(getSupportFragmentManager(), this);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.slidingtabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        tabs.setViewPager(pager);

    }
}

/*import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentActivity;


public class MainActivity extends FragmentActivity {

    public Toolbar mToolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Tab1", "Tab2"};
    int Numboftabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getTitle());
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        mToolbar.setLogo(R.mipmap.ic_kaufkroete);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        //tabs.setDistributeEvenly(true);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        SlidingTabLayout.TabColorizer tc = new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(android.R.color.white);
            }

        };
        tabs.setCustomTabColorizer(tc);

        tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

}*/