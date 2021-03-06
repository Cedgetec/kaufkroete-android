package de.kaufkroete.kaufkroete;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.NumberFormat;
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
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        View v = inflater.inflate(R.layout.tab, container, false);
        sharedPreferences = this.getActivity().getSharedPreferences("metadata", Context.MODE_PRIVATE);
        mView = v;
        tv_my_shop = (TextView) mView.findViewById(R.id.my_shop);
        iv_my_shop = (ImageView) mView.findViewById(R.id.my_shop_imageview);
        iv_my_shop.setAlpha(0.7f);
        tv_my_society = (TextView) mView.findViewById(R.id.my_society);
        iv_my_society = (ImageView) mView.findViewById(R.id.my_society_imageview);
        iv_my_society.setAlpha(0.7f);

        View.OnClickListener listen1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).mViewPager.setCurrentItem(2);
            }
        };

        tv_my_shop.setOnClickListener(listen1);
        iv_my_shop.setOnClickListener(listen1);

        View.OnClickListener listen2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).mViewPager.setCurrentItem(1);
            }
        };
        tv_my_society.setOnClickListener(listen2);
        iv_my_society.setOnClickListener(listen2);
        ((TextView) mView.findViewById(R.id.my_shop)).setText(String.valueOf(sharedPreferences.getString("shop_name", "N/A")));
        CardViewViewHolder cvh = new CardViewViewHolder();
        cvh.filename = String.valueOf(sharedPreferences.getString("shop_image_url", ""));
        cvh.imgview = iv_my_shop;
        if(!cvh.filename.isEmpty()) {
            new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

                @Override
                protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                    Bitmap bitmap = mainActivity.api.cacheGetBitmap(params[0].filename);
                    if(bitmap!=null) {
                        params[0].bitmap = bitmap;
                    } else {
                        try {
                            bitmap = mainActivity.api.getSocietyImage(params[0].filename);
                            params[0].bitmap = bitmap;
                        } catch(IOException e) {
                            e.printStackTrace();
                            params[0].bitmap = null;
                        }
                    }

                    return params[0];
                }

                @Override
                protected void onPostExecute(CardViewViewHolder result) {
                    try {
                        ImageView imgView = result.imgview;
                        if (result.bitmap != null) {
                            imgView.setImageBitmap(result.bitmap);
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
        cvh2.filename = String.valueOf(sharedPreferences.getString("society_image_url", ""));
        cvh2.imgview = iv_my_society;
        if(!cvh2.filename.isEmpty()) {
            new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

                @Override
                protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                    //params[0].bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block_house_steak);
                    try {
                        Bitmap bitmap = mainActivity.api.cacheGetBitmap(params[0].filename);
                        if(bitmap!=null) {
                            params[0].bitmap = bitmap;
                        } else {
                            bitmap = mainActivity.api.getSocietyImage(params[0].filename);
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
                        if (result.bitmap != null) {
                            imgView.setImageBitmap(result.bitmap);
                        } else {
                            imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute(cvh2);
        }
        ((TextView) mView.findViewById(R.id.my_society)).setText(String.valueOf(sharedPreferences.getString("society_name", "N/A")));
        Button btn_go_shopping = (Button) mView.findViewById(R.id.go_shopping);
        btn_go_shopping.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShop();
            }
        });
        tv_donations = (TextView) mView.findViewById(R.id.tv_donations);
        tv_date = (TextView) mView.findViewById(R.id.tv_date);
        getSums(tv_donations, tv_date);
        return v;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if(mView!=null) {
                ((TextView) mView.findViewById(R.id.my_shop)).setText(String.valueOf(sharedPreferences.getString("shop_name", "N/A")));
                ((TextView) mView.findViewById(R.id.my_society)).setText(String.valueOf(sharedPreferences.getString("society_name", "N/A")));
            }
        }
    }

    private void openShop() {
        // ???
        //((TextView) mView.findViewById(R.id.my_shop)).setText(String.valueOf(sharedPreferences.getString("shop_name", "N/A")));
        //((TextView) mView.findViewById(R.id.my_society)).setText(String.valueOf(sharedPreferences.getString("society_name", "N/A")));

        long sid = sharedPreferences.getLong("shop",-1);
        long vid = sharedPreferences.getLong("society", -1);
        if(sid == -1 || vid == -1) {
            if(getView() != null)
                Snackbar.make(getView(), R.string.please_select_first, Snackbar.LENGTH_SHORT).show();
            return;
        }

        try {

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
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kaufkroete.de/api/api_referrer.php?sid=" + url[0] + "&vid=" + url[1]));
                            startActivity(browserIntent);
                            ((MainActivity) getActivity()).mViewPager.setCurrentItem(0);

                        }
                    });
                }
            }.execute(Long.toString(sid), Long.toString(vid));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void getSums(TextView tv, TextView tv2) {
        StatsViewHolder svh = new StatsViewHolder();
        svh.textview = tv;
        svh.textview2 = tv2;
        new AsyncTask<StatsViewHolder, Void, StatsViewHolder>() {
            @Override
            protected StatsViewHolder doInBackground(StatsViewHolder... svhs) {
                svhs[0].value = mainActivity.api.getStats();
                return svhs[0];
            }

            @Override
            protected void onPostExecute(final StatsViewHolder svh) {
                //onPostExecute runs on UI thread
                if (!svh.value[0].isEmpty() && !svh.value[1].isEmpty() && !svh.value[2].isEmpty() && !svh.value[3].isEmpty()) {
                    String donations = NumberFormat.getInstance().format(Double.parseDouble(svh.value[2])) + " EUR";
                    svh.textview.setText(donations);
                    Log.e("Date", svh.value[3]);
                    java.util.Date time=new java.util.Date((long)Long.parseLong(svh.value[3])*1000);
                    String date = "(Stand: " + new SimpleDateFormat("dd. MMM yyyy", Locale.GERMAN).format(time) + ")";
                    svh.textview2.setText(date);
                }
            }
        }.execute(svh);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && sharedPreferences != null) {
            CardViewViewHolder cvh = new CardViewViewHolder();
            cvh.filename = String.valueOf(sharedPreferences.getString("shop_image_url", ""));
            cvh.imgview = iv_my_shop;
            if(!cvh.filename.isEmpty()) {
                new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

                    @Override
                    protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                        try {
                            Bitmap bitmap = mainActivity.api.cacheGetBitmap(params[0].filename);
                            if(bitmap != null) {
                                params[0].bitmap = bitmap;
                            } else {
                                bitmap = mainActivity.api.getSocietyImage(params[0].filename);
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
                            if (result.bitmap != null) {
                                imgView.setImageBitmap(result.bitmap);
                            } else {
                                imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.choose));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute(cvh);
            }
            CardViewViewHolder cvh2 = new CardViewViewHolder();
            cvh2.filename = String.valueOf(sharedPreferences.getString("society_image_url", ""));
            cvh2.imgview = iv_my_society;
            if(!cvh2.filename.isEmpty()) {
                new AsyncTask<CardViewViewHolder, Void, CardViewViewHolder>() {

                    @Override
                    protected CardViewViewHolder doInBackground(CardViewViewHolder... params) {
                        try {
                            Bitmap bitmap = mainActivity.api.cacheGetBitmap(params[0].filename);
                            if(bitmap!=null) {
                                params[0].bitmap = bitmap;
                            } else {
                                bitmap = mainActivity.api.getSocietyImage(params[0].filename);
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
                            if (result.bitmap != null) {
                                imgView.setImageBitmap(result.bitmap);
                            } else {
                                imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.choose));
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