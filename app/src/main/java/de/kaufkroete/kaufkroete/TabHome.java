package de.kaufkroete.kaufkroete;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.MessageDigest;

public class TabHome extends KaufkroeteFragment {

    View mView;
    public TextView tv_my_shop;
    public TextView tv_my_society;
    public TextView tv_facts;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab, container, false);
        sharedPreferences = this.getActivity().getSharedPreferences("metadata", Context.MODE_PRIVATE);
        mView = v;
        tv_my_shop = (TextView) mView.findViewById(R.id.my_shop);
        tv_my_society = (TextView) mView.findViewById(R.id.my_society);
        tv_my_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).pager.setCurrentItem(2);
            }
        });
        tv_my_society.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).pager.setCurrentItem(1);
            }
        });
        ((TextView) mView.findViewById(R.id.my_shop)).setText(String.valueOf(sharedPreferences.getString("shop_name", "N/A")));
        ((TextView) mView.findViewById(R.id.my_society)).setText(String.valueOf(sharedPreferences.getString("societie_name", "N/A")));
        Button btn_go_shopping = (Button) mView.findViewById(R.id.go_shopping);
        btn_go_shopping.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShop();
            }
        });
        tv_facts = (TextView) mView.findViewById(R.id.tv_facts);
        getSumms(tv_facts);
        return v;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if(mView!=null) {
                ((TextView) mView.findViewById(R.id.my_shop)).setText(String.valueOf(sharedPreferences.getString("shop_name", "N/A")));
                ((TextView) mView.findViewById(R.id.my_society)).setText(String.valueOf(sharedPreferences.getString("societie_name", "N/A")));
            }
        }
    }

    private void openShop() {
        ((TextView) mView.findViewById(R.id.my_shop)).setText(String.valueOf(sharedPreferences.getString("shop_name", "N/A")));
        ((TextView) mView.findViewById(R.id.my_society)).setText(String.valueOf(sharedPreferences.getString("societie_name", "N/A")));
        try {
            String sid = String.valueOf(sharedPreferences.getLong("shop",-1));
            String vid = String.valueOf(sharedPreferences.getLong("societie", -1));
            new AsyncTask<String ,Void,String[]>() {
                @Override
                protected String[] doInBackground(String... strings) {
                    String[] ret;
                    ret = strings;
                    return ret;
                }

                @Override
                protected void onPostExecute(final String[] url) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
                            if(!url[0].isEmpty()&&!url[1].isEmpty()) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kaufkroete.de/api/api_referrer.php?sid=" + (String) url[0] + "&vid=" + (String) url[1]));
                                startActivity(browserIntent);
                            } else {
                                Toast.makeText(getActivity(),getString(R.string.please_select_first),Toast.LENGTH_SHORT).show();
                            }
                            ((MainActivity) getActivity()).pager.setCurrentItem(0);
                            /*try {
                                ((MainActivity) getActivity()).selected_shop = -1;
                                ((MainActivity) getActivity()).selected_societie = -1;
                            } catch(Exception e) {
                                e.printStackTrace();
                            }*/
                        }
                    });
                }
            }.execute(sid, vid);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void getSumms(TextView tv) {
        StatsViewHolder svh = new StatsViewHolder();
        svh.textview = tv;
        new AsyncTask<StatsViewHolder ,Void, StatsViewHolder>() {
            @Override
            protected StatsViewHolder doInBackground(StatsViewHolder... svhs) {
                try {
                    String content;
                    HttpURLConnection huc = openBlankConnection("http://kaufkroete.de/api/api_summen.php");
                    if (huc.getResponseCode() == 200) {
                        content = httpURLConnectionToString(huc);
                        cacheSaveText("summs", content);
                    } else {
                        String cache_content = cacheGetText("summs");
                        if (!cache_content.isEmpty()) {
                            content = cache_content;
                        } else {
                            content = "";
                        }
                    }
                    Log.e("kaufkroete", content);
                    JSONArray j_arr = new JSONArray(content);
                    JSONObject j_obj = j_arr.getJSONObject(0);
                    svhs[0].value = new String[]{j_obj.getString("shopanzahl"),j_obj.getString("vereinsanzahl"),j_obj.getString("spendensumme"),String.valueOf(j_obj.getInt("spendenstand(unix)"))};
                    return svhs[0];
                } catch (Exception e) {
                    e.printStackTrace();
                    return svhs[0];
                }
            }

            @Override
            protected void onPostExecute(final StatsViewHolder svh) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!svh.value[0].isEmpty()&&!svh.value[1].isEmpty()&&!svh.value[2].isEmpty()&&!svh.value[3].isEmpty()) {
                            svh.textview.setText(Html.fromHtml("Die Kaufkröte sammelt aktuell Spenden für <b>" + svh.value[0] + "</b> Vereine und hat hierfür bereits <b>" + svh.value[1] + "</b> Partner-Shops, ingesammt sind so schon <b>" + svh.value[2] + "€</b> an Spenden zusammegekommen. (Stand: " + new java.util.Date(Long.parseLong(svh.value[3])).toString() + ")"));
                        }
                    }
                });
            }
        }.execute(svh);
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

    public String cacheGetText(String filename) {
        String ret = "";
        try {
            InputStream inputStream = getActivity().openFileInput(hashString("cache_text_" + filename));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
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

    private class StatsViewHolder {
        TextView textview;
        String[] value;
    }
}