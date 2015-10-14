package de.kaufkroete.kaufkroete;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class SocietiesFragmentListAdapter extends BaseAdapter {
    private ArrayList[] listview_al;

    /*public NewsFragmentListAdapter(Context pContext, String[] pTitle, String[] pContent, int[] pIcon) {*/
    public SocietiesFragmentListAdapter(ArrayList cw_al, ArrayList vid_al, ArrayList name_al, ArrayList image_url_al) {
        listview_al = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
        listview_al[0] = cw_al;
        listview_al[1] = vid_al;
        listview_al[2] = name_al;
        listview_al[3] = image_url_al;
    }

	public View getView(int position, View convertView, ViewGroup parent) {
        return (CardView) listview_al[0].get(position);
    }

    @Override
    public int getCount() {
        return listview_al[0].size();
    }

    @Override
    public Object getItem(int position) {
        return listview_al[0].get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) listview_al[1].get(position);
    }

    public String getName(int position) {
        return (String) listview_al[2].get(position);
    }

    public String getImageUrl(int position) {
        return (String) listview_al[3].get(position);
    }
}