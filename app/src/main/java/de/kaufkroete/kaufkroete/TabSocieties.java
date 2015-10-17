package de.kaufkroete.kaufkroete;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class TabSocieties extends KaufkroeteFragment {

    private ArrayList[] listview_al;
    private SocietiesFragmentListAdapter sfla;
    private ViewHolder vh;
    private SwipeRefreshLayout srl;
    private ListView lv;
    private CheckBox cb_show_all;
    private String filter = "";
    private boolean refreshing_listview = false;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = this.getActivity().getSharedPreferences("metadata", Context.MODE_PRIVATE);
        listview_al = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
        View v = inflater.inflate(R.layout.tab_societies, container, false);
        lv = (ListView) v.findViewById(R.id.list_view);
        cb_show_all = (CheckBox) v.findViewById(R.id.cb_show_all);
        cb_show_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    createView(true);
                } else {
                    createView(true);
                }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPreferences.edit().putLong("societie", sfla.getItemId(i)).apply();
                int index = listview_al[1].indexOf(sharedPreferences.getLong("societie",-1));
                sharedPreferences.edit().putString("societie_name", (String) listview_al[2].get(index)).apply();
                sharedPreferences.edit().putString("societie_image_url", (String) listview_al[3].get(index)).apply();
                if(sharedPreferences.getLong("societie",-1) != -1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainActivity) getActivity()).pager.setCurrentItem(0);
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainActivity) getActivity()).pager.setCurrentItem(2);
                            Toast.makeText(getActivity(), getString(R.string.please_select_a_shop), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        sfla = new SocietiesFragmentListAdapter(listview_al[0], listview_al[1], listview_al[2], listview_al[3]);
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

    private void createView(boolean use_cache) {
        if(refreshing_listview) {
            return;
        }
        refreshing_listview = true;
        listview_al[0].clear();
        listview_al[1].clear();
        listview_al[2].clear();
        listview_al[3].clear();
        vh.use_cache = use_cache;
        sfla.notifyDataSetChanged();
        new AsyncTask<ViewHolder,Void,ViewHolder>() {
            @Override
            protected ViewHolder doInBackground(ViewHolder... params) {
                params[0].content.clear();
                params[0].content.add(getContent(params[0].use_cache));
                return params[0];
            }
            @Override
            protected void onPostExecute(final ViewHolder vh) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //((TextView) vh.view.get(0)).setText(vh.content.get(0));
                        try {
                            int length;
                            if(cb_show_all.isChecked() || !filter.equals("")) {
                                length = ((ArrayList) vh.content.get(0)).size();
                            } else {
                                length = 10;
                            }
                            for (int i = 0; i < length; i++) {
                                KKSociety tempSociety = (KKSociety) ((ArrayList) vh.content.get(0)).get(i);
                                if (!filter.equals("")) {
                                    if (tempSociety.name.toLowerCase().contains(filter.toLowerCase())) {
                                        CardView cw = createCardItem((KKSociety) ((ArrayList) vh.content.get(0)).get(i), vh.layout_inflater);
                                        listview_al[0].add(cw);
                                        listview_al[1].add((long) ((KKSociety) ((ArrayList) vh.content.get(0)).get(i)).vid);
                                        listview_al[2].add(((KKSociety) ((ArrayList) vh.content.get(0)).get(i)).name);
                                        listview_al[3].add(((KKSociety) ((ArrayList) vh.content.get(0)).get(i)).image_url);
                                        sfla.notifyDataSetChanged();
                                    }
                                } else {
                                    CardView cw = createCardItem((KKSociety) ((ArrayList) vh.content.get(0)).get(i), vh.layout_inflater);
                                    listview_al[0].add(cw);
                                    listview_al[1].add((long) ((KKSociety) ((ArrayList) vh.content.get(0)).get(i)).vid);
                                    listview_al[2].add(((KKSociety) ((ArrayList) vh.content.get(0)).get(i)).name);
                                    listview_al[3].add(((KKSociety) ((ArrayList) vh.content.get(0)).get(i)).image_url);
                                    sfla.notifyDataSetChanged();
                                }
                            }
                            if (srl.isRefreshing()) {
                                srl.setRefreshing(false);
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        refreshing_listview = false;
                    }
                });
            }
        }.execute(vh);
    }

    private CardView createCardItem(KKSociety kks_item, LayoutInflater i) {
        CardView scv = createCardView();
        scv.setCardBackgroundColor(Color.WHITE);
        scv.setContentPadding(0, 0, 0, 0);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        scv.setLayoutParams(params);
        i.inflate(R.layout.societies_cardview_entry, scv, true);
        String donations = String.valueOf(kks_item.donations) + " " + getResources().getString(R.string.donations);
        ((TextView) scv.findViewById(R.id.society_donations)).setText(donations);
        ((TextView) scv.findViewById(R.id.society_name)).setText(kks_item.name);
        String donation_amount = String.valueOf(kks_item.donations_amount) + " " + getResources().getString(R.string.euros);
        ((TextView) scv.findViewById(R.id.society_donation_amount)).setText(donation_amount);
        scv.findViewById(R.id.society_header_outer).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        CardViewViewHolder vh = new CardViewViewHolder();
        vh.imgview = (ImageView) scv.findViewById(R.id.society_image);
        vh.imgview.setMaxWidth(toPixels(120));
        //vh.imgview.setScaleType(ImageView.ScaleType.FIT_XY);
        vh.imgview.setAdjustViewBounds(true);
        vh.imgview.setAlpha(0.7f);
        vh.filename = kks_item.image_url;
        new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

            @Override
            protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                //params[0].bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block_house_steak);
                try {
                    Bitmap bitmap = cacheGetBitmap(params[0].filename);
                    if(bitmap!=null) {
                        params[0].bitmap = bitmap;
                    } else {
                        bitmap = getSocietyImage(params[0].filename);
                        cacheSaveBitmap(params[0].filename, bitmap);
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
                    imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(vh);
        return scv;
    }

    private ArrayList<KKSociety> getContent(boolean use_cache) {
        try {
            String content;
            //HttpsURLConnection huc = openHttpsConnection("/test.json");
            if(use_cache) {
                content = cacheGetText("societies");
                if(content.isEmpty()) {
                    HttpURLConnection huc = openBlankConnection("http://kaufkroete.de/api/api_vereine.php");
                    if (huc.getResponseCode() == 200) {
                        content = httpURLConnectionToString(huc);
                        cacheSaveText("societies", content);
                    } else {
                        content = "";
                    }
                }
            } else {
                HttpURLConnection huc = openBlankConnection("http://kaufkroete.de/api/api_vereine.php");
                if (huc.getResponseCode() == 200) {
                    content = httpURLConnectionToString(huc);
                    cacheSaveText("societies", content);
                } else {
                    content = "";
                }
            }
            return parseJSON(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<KKSociety> parseJSON(String js) throws JSONException {
        ArrayList<KKSociety> kks_al = new ArrayList<>();
        JSONArray pages = new JSONArray(js);
        Log.e("Kaufkroete", js);
        for (int i = 0; i < pages.length(); ++i) {
            JSONObject rec = pages.getJSONObject(i);
            KKSociety kks = new KKSociety();
            kks.vid = rec.getInt("vid");
            kks.name = rec.getString("name");
            kks.city = rec.getString("city");
            kks.image_url = rec.getString("image_url");
            kks.donations = rec.getInt("donations");
            kks.donations_amount = rec.getDouble("donation_amount");
            kks_al.add(kks);
        }
        return kks_al;
    }

    public Bitmap cacheGetBitmap(String filename) {
        try {
            return BitmapFactory.decodeStream(getActivity().openFileInput(hashString("cache_" + filename)));
        } catch(Exception e) {
            return null;
        }
    }

    private void cacheSaveBitmap(String filename, Bitmap image) {
        try {
            OutputStream fos = getActivity().openFileOutput(hashString("cache_" + filename), Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String cacheGetText(String filename) {
        String ret = "";
        try {
            InputStream inputStream = getActivity().openFileInput(hashString("cache_text_" + filename));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch(FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch(IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    private void cacheSaveText(String filename, String content) {
        try {
            OutputStream fos = getActivity().openFileOutput(hashString("cache_text_" + filename), Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hashString(String in) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(in.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println("Hex format : " + sb.toString());

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<byteData.length;i++) {
            String hex=Integer.toHexString(0xff & byteData[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public Bitmap getSocietyImage(String filename) throws IOException {

        HttpURLConnection con = openBlankConnection(filename);
        con.setRequestMethod("GET");

        if(con.getResponseCode() == 200) {
            return BitmapFactory.decodeStream(con.getInputStream());
        } else {
            throw new IOException();
        }
    }

    class KKSociety {
        int vid;
        String name;
        String city;
        String image_url;
        int donations;
        double donations_amount;
    }

    private class CardViewViewHolder {
        ImageView imgview;
        Bitmap bitmap;
        String filename;
    }
}