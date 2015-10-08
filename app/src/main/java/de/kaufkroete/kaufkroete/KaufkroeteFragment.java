package de.kaufkroete.kaufkroete;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class KaufkroeteFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_default, container, false);
    }

    public HttpsURLConnection openHttpsConnection(String url) throws IOException {
        HttpsURLConnection con = (HttpsURLConnection) new URL("https://ggdevelopers.de/sites/kaufkroete-de" + url).openConnection();
        con.setRequestProperty("User-Agent", "KaufkroeteAPP/" + BuildConfig.VERSION_NAME + " (" +
                BuildConfig.VERSION_CODE + " " + BuildConfig.BUILD_TYPE + " Android " + Build.VERSION.RELEASE + " " + Build.PRODUCT + ")");
        con.setConnectTimeout(3000);
        Log.w("kaufkroete", "connection to " + con.getURL().getHost() + " established: " + url);
        return con;
    }

    public HttpURLConnection openBlankConnection(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestProperty("User-Agent", "KaufkroeteAPP/" + BuildConfig.VERSION_NAME + " (" +
                BuildConfig.VERSION_CODE + " " + BuildConfig.BUILD_TYPE + " Android " + Build.VERSION.RELEASE + " " + Build.PRODUCT + ")");
        con.setConnectTimeout(3000);
        Log.w("kaufkroete", "connection to " + con.getURL().getHost() + " established: " + url);
        return con;
    }

    public String httpURLConnectionToString(HttpURLConnection huc) throws Exception {
        InputStream is = huc.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public String httpsURLConnectionToString(HttpsURLConnection huc) throws Exception {
        InputStream is = huc.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public CardView createCardView() {
        CardView c2 = new CardView(getActivity());
        AbsListView.LayoutParams c2params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT
        );
        c2.setLayoutParams(c2params);
        c2.setUseCompatPadding(true);
        c2.setContentPadding(toPixels(16), toPixels(16), toPixels(16), toPixels(16));
        return c2;
    }

    public int toPixels(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }

    class ViewHolder {
        ArrayList<View> view = new ArrayList<>();
        ArrayList<Object> content = new ArrayList<>();
        LayoutInflater layout_inflater;
        boolean use_cache;
    }
}