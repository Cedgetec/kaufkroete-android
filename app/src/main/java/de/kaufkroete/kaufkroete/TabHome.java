package de.kaufkroete.kaufkroete;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TabHome extends KaufkroeteFragment {

    View mView;
    public TextView tv_my_shop;
    public ImageView iv_my_shop;
    public TextView tv_my_society;
    public ImageView iv_my_society;
    public TextView tv_donations;
    public TextView tv_date;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab, container, false);
        sharedPreferences = this.getActivity().getSharedPreferences("metadata", Context.MODE_PRIVATE);
        mView = v;
        tv_my_shop = (TextView) mView.findViewById(R.id.my_shop);
        iv_my_shop = (ImageView) mView.findViewById(R.id.my_shop_imageview);
        tv_my_society = (TextView) mView.findViewById(R.id.my_society);
        iv_my_society = (ImageView) mView.findViewById(R.id.my_society_imageview);
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
        CardViewViewHolder cvh = new CardViewViewHolder();
        cvh.filename = String.valueOf(sharedPreferences.getString("shop_image_url", ""));
        cvh.imgview = iv_my_shop;
        if(!cvh.filename.isEmpty()) {
            new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

                @Override
                protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                    //params[0].bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block_house_steak);
                    try {
                        Bitmap bitmap = cacheGetBitmap(params[0].filename);
                        if (bitmap != null) {
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
                        ImageView imgView = (ImageView) result.imgview;
                        if (result.bitmap != null) {
                            imgView.setImageBitmap((Bitmap) result.bitmap);
                        } else {
                            imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute(cvh);
        }
        CardViewViewHolder cvh2 = new CardViewViewHolder();
        cvh2.filename = String.valueOf(sharedPreferences.getString("societie_image_url", ""));
        cvh2.imgview = iv_my_society;
        if(!cvh2.filename.isEmpty()) {
            new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

                @Override
                protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                    //params[0].bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block_house_steak);
                    try {
                        Bitmap bitmap = cacheGetBitmap(params[0].filename);
                        if (bitmap != null) {
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
                        ImageView imgView = (ImageView) result.imgview;
                        if (result.bitmap != null) {
                            imgView.setImageBitmap((Bitmap) result.bitmap);
                        } else {
                            imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute(cvh2);
        }
        ((TextView) mView.findViewById(R.id.my_society)).setText(String.valueOf(sharedPreferences.getString("societie_name", "N/A")));
        Button btn_go_shopping = (Button) mView.findViewById(R.id.go_shopping);
        btn_go_shopping.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShop();
            }
        });
        tv_donations = (TextView) mView.findViewById(R.id.tv_donations);
        tv_date = (TextView) mView.findViewById(R.id.tv_date);
        getSumms(tv_donations, tv_date);
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

    public void getSumms(TextView tv, TextView tv2) {
        StatsViewHolder svh = new StatsViewHolder();
        svh.textview = tv;
        svh.textview2 = tv2;
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
                            String donations = svh.value[2] + " EUR";
                            svh.textview.setText(donations);
                            String date = "(Stand: " + new SimpleDateFormat("dd. MMM yyyy", Locale.GERMAN).format(new Date()) + ")";
                            svh.textview2.setText(date);
                        }
                    }
                });
            }
        }.execute(svh);
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

    public Bitmap getSocietyImage(String filename) throws IOException {

        HttpURLConnection con = openBlankConnection(filename);
        con.setRequestMethod("GET");

        if(con.getResponseCode() == 200) {
            return BitmapFactory.decodeStream(con.getInputStream());
        } else {
            throw new IOException();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&(sharedPreferences!=null)) {
            CardViewViewHolder cvh = new CardViewViewHolder();
            cvh.filename = String.valueOf(sharedPreferences.getString("shop_image_url", ""));
            cvh.imgview = iv_my_shop;
            if(!cvh.filename.isEmpty()) {
                new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

                    @Override
                    protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                        //params[0].bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block_house_steak);
                        try {
                            Bitmap bitmap = cacheGetBitmap(params[0].filename);
                            if (bitmap != null) {
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
                            ImageView imgView = (ImageView) result.imgview;
                            if (result.bitmap != null) {
                                imgView.setImageBitmap((Bitmap) result.bitmap);
                            } else {
                                imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute(cvh);
            }
            CardViewViewHolder cvh2 = new CardViewViewHolder();
            cvh2.filename = String.valueOf(sharedPreferences.getString("societie_image_url", ""));
            cvh2.imgview = iv_my_society;
            if(!cvh2.filename.isEmpty()) {
                new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

                    @Override
                    protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                        //params[0].bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block_house_steak);
                        try {
                            Bitmap bitmap = cacheGetBitmap(params[0].filename);
                            if (bitmap != null) {
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
                            ImageView imgView = (ImageView) result.imgview;
                            if (result.bitmap != null) {
                                imgView.setImageBitmap((Bitmap) result.bitmap);
                            } else {
                                imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute(cvh2);
            }
        }
    }

    private class StatsViewHolder {
        TextView textview;
        TextView textview2;
        String[] value;
    }

    private class CardViewViewHolder {
        ImageView imgview;
        Bitmap bitmap;
        String filename;
    }
}