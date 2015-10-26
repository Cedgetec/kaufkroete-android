package de.kaufkroete.kaufkroete;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    ArrayList<TabElement> al_tabs;

    public ViewPagerAdapter(FragmentManager fm, Context ctx) {
        super(fm);

        context = ctx;
        al_tabs = new ArrayList<>();

        TabElement te0 = new TabElement();
        te0.title = context.getResources().getString(R.string.home);
        te0.fragment = new TabHome();
        al_tabs.add(te0);

        TabElement te1 = new TabElement();
        te1.title = context.getResources().getString(R.string.societies);
        te1.fragment = new ListTab();
        Bundle bn1 = new Bundle();
        bn1.putString("type", ListTab.TabType.TAB_SOCIETIES.toString());
        te1.fragment.setArguments(bn1);
        al_tabs.add(te1);

        TabElement te2 = new TabElement();
        te2.title = context.getResources().getString(R.string.shops);
        te2.fragment = new ListTab();
        Bundle bn2 = new Bundle();
        bn2.putString("type", ListTab.TabType.TAB_SHOPS.toString());
        te2.fragment.setArguments(bn2);
        al_tabs.add(te2);
    }

    // This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(al_tabs.get(position).fragment!=null) {
           return al_tabs.get(position).fragment;
        }

        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return al_tabs.get(position).title;
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return al_tabs.size();
    }

}

class TabElement {
    String title;
    Fragment fragment;
}