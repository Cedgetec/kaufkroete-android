package de.kaufkroete.kaufkroete;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class KKDataAdapter extends BaseAdapter {

    private ArrayList<KKData> data;
    private ListTab tab;

    public KKDataAdapter(ArrayList<KKData> d, ListTab lt) {
        this.data = d;
        this.tab = lt;
    }

	public View getView(int position, View convertView, ViewGroup parent) {
        return tab.createCardItem(data.get(position), tab.getActivity().getLayoutInflater());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

}