package com.cybercode.sdm;
import android.annotation.SuppressLint;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
public class utils {
    public static long getNormalSize(String urls) throws MalformedURLException {
        URL url = new URL(urls);
        HttpURLConnection conn = null;
        try {
            System.out.println("Printing Response Header...\n");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            return -1;
        } finally {
            conn.disconnect();
        }


    }

    @SuppressLint("DefaultLocale")
    public static String readableFileSize(long size) {

        if (size / (1024 * 1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "GB";
        } else if (size / (1024 * 1024) > 0) {
            DecimalFormat df = null;
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            if(size<=10485760){
                //  Log.v("no", "size is less than 10MB");
                df = new DecimalFormat("#.00");
            }else{
                //  Log.v("yes", "size is less than 10MB");
                df = new DecimalFormat("#.0");
            }

            return "" + df.format(tmpSize) + "mB";
        } else if (size / 1024 > 0) {
            return "" + (size / (1024)) + "kB";
        } else
            return "" + size + "B";
    }



    public static  String getHttpProtocol( Map<String, List<String>> map){
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey()
                    + " ,Value : " + entry.getValue());

            List<String> contentProtocol = map.get(null);

            if (contentProtocol == null) {
                System.out.println("'contentProtocol' doesn't present in Header!"+ contentProtocol);
            } else {
                for (final String header : contentProtocol) {
                    return  header;
                }

            }

        }
        return  null;
    }

    public static int getIconState(String sts) {


        switch (sts) {
            case "Finished":
                //DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white));
                return R.drawable.ic_done;
            case "Begining":
                //DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white));
                return R.drawable.ic_pause;
            case "Resumed":
                //DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white));
                return R.drawable.ic_play_arrow;
            default:
                // code block
        }
        return -1;
    }
    public static String getDownSpeed(long task) {
        String speedText;
        if (task < 1024) { //you can also convert to double
            speedText = task + " B/s";
        } else if (task < 1024 * 1024) {
            speedText = (task / 1024) + " KB/S";
        } else {
            speedText = (task / (1024 * 1024)) + " MB/S";
        }
return speedText;
    }
    public static int UniqueID(String id){
        // creating UUID
        String result = UUID.nameUUIDFromBytes(id.getBytes()).toString();
        UUID uid = UUID.fromString(result);
        return uid.hashCode();
    }



}