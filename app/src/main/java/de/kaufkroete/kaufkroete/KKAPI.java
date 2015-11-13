package de.kaufkroete.kaufkroete;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

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
import java.net.URL;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class KKAPI {

    private Context context;

    public KKAPI(Context c) {
        context = c;
    }

    public HttpURLConnection openBlankConnection(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestProperty("User-Agent", "KaufkroeteAPP/" + BuildConfig.VERSION_NAME + " (" +
                BuildConfig.VERSION_CODE + " " + BuildConfig.BUILD_TYPE + " Android " + Build.VERSION.RELEASE + " " + Build.PRODUCT + ")");
        con.setConnectTimeout(3000);
        Log.w("kaufkroete", "connection to " + con.getURL().getHost() + " established: " + url);
        return con;
    }

    public String readStream(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String content = "";
        String line;

        while ((line = br.readLine()) != null) {
            content += line + "\n";
        }

        return content;
    }

    public ArrayList<KKData> getShops(boolean use_cache) {
        try {
            String content = "";
            if(use_cache) {
                content = cacheGetText("shops");
            }

            // no network on main thread
            if(content.isEmpty() && Looper.myLooper() != Looper.getMainLooper()) {
                HttpURLConnection huc = openBlankConnection("http://kaufkroete.de/api/api_shops.php");
                if (huc.getResponseCode() == 200) {
                    content = readStream(huc.getInputStream());
                    cacheSaveText("shops", content);
                } else {
                    content = "";
                }
            } else if(content.isEmpty()) {
                return new ArrayList<>();
            }

            return parseShops(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<KKData> getSocieties(boolean use_cache) {
        try {
            String content = "";
            if(use_cache) {
                content = cacheGetText("societies");
            }

            if(content.isEmpty() && Looper.myLooper() != Looper.getMainLooper()) {
                HttpURLConnection huc = openBlankConnection("http://kaufkroete.de/api/api_vereine.php");
                if (huc.getResponseCode() == 200) {
                    content = readStream(huc.getInputStream());
                    cacheSaveText("societies", content);
                } else {
                    content = "";
                }
            } else if(content.isEmpty()) {
                return new ArrayList<>();
            }

            return parseSocieties(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<KKData> parseSocieties(String js) throws Exception {
        ArrayList<KKData> kks_al = new ArrayList<>();
        JSONArray pages = new JSONArray(js);

        for (int i = 0; i < pages.length(); ++i) {
            JSONObject rec = pages.getJSONObject(i);
            KKData kks = new KKData();
            kks.sid = rec.getInt("vid");
            kks.name = rec.getString("name");
            kks.imageUrl = new URL(rec.getString("image_url"));
            kks.detail = context.getString(R.string.donations, rec.getInt("donations"));
            kks.info = context.getString(R.string.donation_amount, rec.getDouble("donation_amount"));
            kks_al.add(kks);
        }
        return kks_al;
    }

    public ArrayList<KKData> parseShops(String js) throws Exception {
        ArrayList<KKData> data = new ArrayList<>();
        JSONArray pages = new JSONArray(js);

        for (int i = 0; i < pages.length(); ++i) {
            JSONObject rec = pages.getJSONObject(i);
            KKData kks = new KKData();
            kks.sid = rec.getInt("sid");
            kks.name = rec.getString("name");
            kks.detail = rec.getString("mode");
            if(kks.detail.equals("percent")) {
                kks.detail = context.getString(R.string.percent);
                kks.info = new DecimalFormat("#.#").format(rec.getDouble("amount"));
            } else if(kks.detail.equals("euro")) {
                kks.detail = context.getString(R.string.euro);
                kks.info = new DecimalFormat("#.##").format(rec.getDouble("amount"));
            }


            kks.imageUrl = new URL(rec.getString("image_url"));
            data.add(kks);
        }

        return data;
    }

    public Bitmap cacheGetBitmap(String filename) {
        try {
            return BitmapFactory.decodeStream(context.openFileInput(hashString("cache_" + filename)));
        } catch(Exception e) {
            return null;
        }
    }

    public void cacheSaveBitmap(String filename, Bitmap image) {
        try {
            OutputStream fos = context.openFileOutput(hashString("cache_" + filename), Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String hashString(String in) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(in.getBytes());

        String hexString = "";
        for (byte b : md.digest()) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1)
                hexString += '0';
            hexString += hex;
        }

        return hexString;
    }

    public Bitmap getSocietyImage(String filename) throws IOException {

        HttpURLConnection con = openBlankConnection(filename);
        con.setRequestMethod("GET");

        if(con.getResponseCode() == 200) {
            Bitmap b =  BitmapFactory.decodeStream(con.getInputStream());
            cacheSaveBitmap(filename, b);
            b = cacheGetBitmap(filename);
            return b;
        } else {
            throw new IOException();
        }
    }

    public String cacheGetText(String filename) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(hashString("cache_text_" + filename));

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
            OutputStream fos = context.openFileOutput(hashString("cache_text_" + filename), Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getStats() {
        String content = "";

        try {
            HttpURLConnection huc = openBlankConnection("http://kaufkroete.de/api/api_summen.php");
            if (huc.getResponseCode() == 200) {
                content = readStream(huc.getInputStream());
                cacheSaveText("stats", content);

            }
        } catch (Exception e) {
            e.printStackTrace();
            content = cacheGetText("stats");
        }

        if (!content.isEmpty()) {
            try {
                JSONObject j_obj = new JSONArray(content).getJSONObject(0);
                return new String[]{ j_obj.getString("shopanzahl"), j_obj.getString("vereinsanzahl"),
                        j_obj.getString("spendensumme"), String.valueOf(j_obj.getInt("spendenstand(unix)")) };

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}
