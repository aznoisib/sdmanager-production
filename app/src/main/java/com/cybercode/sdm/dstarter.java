package com.cybercode.sdm;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

class dstarter {
    static class startnew extends AsyncTask<String , Integer ,String> {
        private WeakReference<MainActivity> activityReference;
        AsyncResponse delegate;
        Matcher m;
        Exception error;
        String contentType;
        int responseCode;
        long fileLength;
        public interface AsyncResponse {
            void processDONE(long i);
            void processERROR();
            void processERROR_EXCEPTION(Exception e);

        }



        // only retain a weak reference to the activity
        startnew(MainActivity context,AsyncResponse asyncResponse) {
            delegate = asyncResponse;
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;

            try {
// Create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
                };

                // Install the all-trusting trust manager
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());


                // Create all-trusting host name verifier
                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                //Install the all-trusting host verifier
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                long conLength = -1;
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setInstanceFollowRedirects(true);
                urlConnection.setRequestMethod("HEAD");
                urlConnection.setRequestProperty("User-Agent", "Wget/5.0");
                urlConnection.setRequestProperty("Accept-Encoding", "identity");
                urlConnection.setRequestProperty("Accept-Encoding", "");
                urlConnection.setRequestProperty("Range", "bytes=0-1");
                String contentRange = urlConnection.getHeaderField("Content-Range");
                if (contentRange != null) {
                    conLength = Integer.parseInt(contentRange.split("/")[1]);
                }

                if (conLength<0) {
                    conLength = urlConnection.getContentLength();
                    if (conLength<0) {
                        conLength=-1;
                    }
                }

                if (!(conLength ==-1)) {
                    fileLength = conLength;
                }else{
                    // throw new Exception("Unknown file Length");
                }


                contentType = urlConnection.getContentType();
                if (contentType.isEmpty()) {
                    contentType = "application/*";
                    // contentType = urlConnection.getContent();
                    //if (contentType == null) {
                    //    contentType = "application/*";
                    //}
                }
                Log.v("contentType", contentType + "/" + conLength);


                responseCode = urlConnection.getResponseCode();



            } catch (Exception e) {
                error=e;
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
//urlConnection.disconnect();
            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {

            // get a reference to the activity if it is still there
            MainActivity activity = activityReference.get();
            if (activity == null) return;
            if(error!=null){
                delegate.processERROR_EXCEPTION(error);
            }else{

                Pattern p = Pattern.compile("text/html");   // the pattern to search for
                m = p.matcher(contentType);
                if (m.find()) {
                    delegate.processERROR();

                }else{
                    if (200 <= responseCode && responseCode <= 399) {
                        delegate.processDONE(fileLength);
                        Log.v("CatalogClient", String.valueOf(responseCode));
                    } else {
                        Log.v("CatalogClient", String.valueOf(responseCode));
                        delegate.processERROR();
                    }
                }







                delegate=null;

            }
        }
    }

}