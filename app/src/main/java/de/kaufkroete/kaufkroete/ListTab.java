package de.kaufkroete.kaufkroete;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ListTab extends KaufkroeteFragment {

    private MainActivity mainActivity;
    private KKDataAdapter sfla;
    private ViewHolder vh;
    private SwipeRefreshLayout srl;
    private ListView lv;
    private CheckBox cb_show_all;
    private String filter = "";
    private boolean refreshing_listview = false;
    private SharedPreferences sharedPreferences;

    public ArrayList<KKData> getData() {
        return getType() == TabType.TAB_SHOPS ? mainActivity.shops : mainActivity.societies;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        sharedPreferences = this.getActivity().getSharedPreferences("metadata", Context.MODE_PRIVATE);
        View v = inflater.inflate(R.layout.tab_shops, container, false);
        lv = (ListView) v.findViewById(R.id.list_view);

        cb_show_all = (CheckBox) v.findViewById(R.id.cb_show_all);
        cb_show_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                createView(true);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPreferences.edit().putLong(getType() == TabType.TAB_SHOPS ? "shop" : "society", sfla.getItemId(i)).apply();
                sharedPreferences.edit().putString(getType() == TabType.TAB_SHOPS ? "shop_name" : "society_name", ((KKData) sfla.getItem(i)).name).apply();
                sharedPreferences.edit().putString(getType() == TabType.TAB_SHOPS ? "shop_image_url" : "society_image_url", ((KKData) sfla.getItem(i)).imageUrl.toString()).apply();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).pager.setCurrentItem(0);
                    }
                });

            }
        });

        sfla = new KKDataAdapter(getData(), this);
        lv.setAdapter(sfla);
        vh = new ViewHolder();
        vh.view.add(lv);
        vh.layout_inflater = inflater;
        srl = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createView(false);
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (lv == null || lv.getChildCount() == 0) ?
                                0 : lv.getChildAt(0).getTop();
                srl.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        EditText search_edit = (EditText) v.findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter = String.valueOf(charSequence);
                createView(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        createView(true);
        return v;
    }

    public TabType getType() {
        return TabType.valueOf(getArguments().getString("type"));
    }

    private void createView(boolean use_cache) {
        if(refreshing_listview) {
            return;
        }
        refreshing_listview = true;

        vh.use_cache = use_cache;

        new AsyncTask<ViewHolder,Void,ViewHolder>() {

            @Override
            protected ViewHolder doInBackground(ViewHolder... params) {
                params[0].content =  getType() == TabType.TAB_SHOPS ? mainActivity.api.getShops(params[0].use_cache)
                        : mainActivity.api.getSocieties(params[0].use_cache);
                return params[0];
            }

            @Override
            protected void onPostExecute(final ViewHolder vh) {

                getData().clear();

                for(KKData shop : vh.content) {
                    if(cb_show_all.isChecked() || (!filter.isEmpty() && shop.name.toLowerCase().contains(filter.toLowerCase()))) {
                        getData().add(shop);
                    }
                }

                refreshing_listview = false;
                srl.setRefreshing(false);
                sfla.notifyDataSetChanged();
            }
        }.execute(vh);
    }

    public CardView createCardItem(KKData kks_item, LayoutInflater i) {
        CardView scv = createCardView();
        scv.setCardBackgroundColor(Color.WHITE);
        scv.setContentPadding(0, 0, 0, 0);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        scv.setLayoutParams(params);
        i.inflate(R.layout.shops_cardview_entry, scv, true);
        ((TextView) scv.findViewById(R.id.shop_name)).setText(kks_item.name);
        ((TextView) scv.findViewById(R.id.shop_donation_amount)).setText(kks_item.info);
        ((TextView) scv.findViewById(R.id.shop_donations_text)).setText(kks_item.detail);
        scv.findViewById(R.id.shop_header_outer).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        CardViewViewHolder vh = new CardViewViewHolder();
        vh.imgview = (ImageView) scv.findViewById(R.id.shop_image);
        vh.imgview.setMaxWidth(toPixels(120));
        vh.imgview.setAlpha(0.7f);
        vh.filename = kks_item.imageUrl;

        new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

            @Override
            protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                //params[0].bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block_house_steak);
                try {
                    Bitmap bitmap = mainActivity.api.cacheGetBitmap(params[0].filename.getFile());
                    if(bitmap!=null) {
                        params[0].bitmap = bitmap;
                    } else {
                        bitmap = mainActivity.api.getSocietyImage(params[0].filename.toString());
                        mainActivity.api.cacheSaveBitmap(params[0].filename.getFile(), bitmap);
                        params[0].bitmap = bitmap;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    params[0].bitmap = null;
                }

                return params[0];
            }

            @Override
            protected void onPostExecute(CardViewViewHolder result) {
                try {
                    ImageView imgView = result.imgview;
                    if(result.bitmap != null) {
                        imgView.setImageBitmap(result.bitmap);
                    } else {
                        imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame));
                    }
                    //imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(vh);
        return scv;
    }

    private class CardViewViewHolder {
        ImageView imgview;
        Bitmap bitmap;
        URL filename;
    }

    public enum TabType {
        TAB_SHOPS, TAB_SOCIETIES
    }
}