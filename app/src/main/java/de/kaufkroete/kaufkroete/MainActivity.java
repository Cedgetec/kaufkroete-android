package de.kaufkroete.kaufkroete;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
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
        mToolbar.setLogo(R.mipmap.ic_kaufkroete);
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
