package com.cybercode.sdm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;


public class MainActivity  extends AppCompatActivity {
    Handler mHandler;
    String m_chosenDir = "/storage/emulated/0/Download";
    String mUrl = null;
    int tryCount=0;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    Timer timer;
    TextView fileSize;
    ProgressBar redit;
    EditText ledit,aedit,pedit;
    ImageView browBtn;
    Button saveBtn,cancelBtn;
    FloatingActionButton faBad;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    pager pagerAdapter;
    private dservices boundService;
    private boolean isBound;

    private static final int READ_TIMEOUT = 5 * 60;
    private static final int WRITE_TIMEOUT = 5 * 60;
    private static final int CONNECT_TIMEOUT = 30;


    public static boolean isNetworkConnected(Context ctx) {
        final ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }

    public String sendUri(){
        return mUrl;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        doBindService();
        Log.v("ctx",String.valueOf(isNetworkConnected(getApplicationContext())));


             //   .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
               // .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
               // .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        // 添加 SSL 认证


      //  FileDownloader.setup(this);

        //link views
        getViews();

        //setting toolbar
        //initializeToolBar();

        //adapter setup
        pagerAdapter = new pager(getSupportFragmentManager());

        //attaching fragments to adapter
        pagerAdapter.addFragment(new downloading(),"Downloading");
        pagerAdapter.addFragment(new downloaded(),"Downloaded");
        pagerAdapter.addFragment(new browser(),"Browser");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);




faBad.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View view) {

        int[] primes = {0,1};

        for (int prime : primes) {
            Log.v("TAG", String.valueOf(prime));

        }

        return false;
    }
});

        faBad.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View view) {
                //new NonLeakyThread(MainActivity.this).start();

                dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                View layoutView = getLayoutInflater().inflate(R.layout.dialogue, null);
                dialogBuilder.setView(layoutView);
                alertDialog = dialogBuilder.create();


                ledit = layoutView.findViewById(R.id.filePath);
                pedit = layoutView.findViewById(R.id.fileLink);
                aedit = layoutView.findViewById(R.id.fileName);
                redit = layoutView.findViewById(R.id.pbLoading);
                browBtn = layoutView.findViewById(R.id.browse);
                saveBtn = layoutView.findViewById(R.id.button_save_user_data);
                cancelBtn = layoutView.findViewById(R.id.button_cancel_user_data);
                fileSize = layoutView.findViewById(R.id.sizes);
                final RelativeLayout vg = layoutView.findViewById(R.id.groupdial);
                final TableLayout vy = layoutView.findViewById(R.id.tableLayouts);

                saveBtn.setEnabled(false);
                ledit.setText(m_chosenDir.trim());
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onShow(DialogInterface dialog) {
                        saveBtn.setText("Save");


                        pedit.addTextChangedListener(new TextWatcher() {

                            private void swithButton(boolean arg){
                                if(arg){
                                    saveBtn.setEnabled(true);
                                    saveBtn.setText("Save");
                                    saveBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final int id = utils.UniqueID(pedit.getText().toString().trim());
                                            //if (isBound) {
                                            boundService.downLoadStart(new models(null, "0B/s", pedit.getText().toString().trim(), 0, System.currentTimeMillis(), "pauseds", ledit.getText().toString().trim(), 3, id, new dcallback() {
                                                @Override
                                                public void onDownloadProgress(models tsk, int taskId, long size) {

                                                }

                                                @Override
                                                public void onDownloadException(String taskId, Exception e) {

                                                }

                                                @Override
                                                public void onDownloadPrepare(models tsk) {

                                                }

                                                @Override
                                                public void onDownloadFinish(int taskId, models tsk) {

                                                }

                                                @Override
                                                public void onDownloadStart(dutils fdl, models tsk) {

                                                }
                                            }));

                                                //}
                                        }
                                    });
                                }else{
                                    saveBtn.setEnabled(true);
                                    saveBtn.setText("Browser");
                                    saveBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            viewPager.setCurrentItem(2);
                                            mHandler.postDelayed(new Runnable() {
                                                public void run() {
                                                    alertDialog.dismiss();
                                                }
                                            }, 500);

                                        }
                                    });
                                }
                            }


                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileSize.setVisibility(View.INVISIBLE);
                                        redit.setVisibility(View.VISIBLE);

                                    }
                                });
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if(timer != null) timer.cancel();
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                                if(tryCount==3) {

                                }
                                if (editable.length() > 0) {
                                    mUrl = editable.toString();
                                    timer = new Timer();
                                    TimerTask tt = new TimerTask() {
                                        @Override
                                        public void run() {

                                         new dstarter.startnew(MainActivity.this, new dstarter.startnew.AsyncResponse() {
                                                @Override
                                                public void processDONE(long i) {
                                                    redit.setVisibility(View.INVISIBLE);
                                                    fileSize.setVisibility(View.VISIBLE);
                                                    fileSize.setText(String.valueOf(i));
                                                    swithButton(true);
                                                }

                                                @Override
                                                public void processERROR() {
                                                    ++tryCount;
                                                    redit.setVisibility(View.INVISIBLE);
                                                    Toast toast = Toast.makeText(getApplicationContext(), "SDM: "+  "open it in browser", Toast.LENGTH_LONG);
                                                    View toastView = toast.getView(); // This'll return the default View of the Toast.

                                                    /* And now you can get the TextView of the default View of the Toast. */
                                                    TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                                                    // toastMessage.setTextSize(25);
                                                    //toastMessage.setTextColor(Color.RED);
                                                    toastMessage.setPadding(16,0,16,0);
                                                    toastMessage.setGravity(Gravity.CENTER);
                                                    toastView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                                                    toast.show();
                                                }
                                                @Override
                                                public void processERROR_EXCEPTION(Exception e) {
                                                    ++tryCount;
                                                    redit.setVisibility(View.INVISIBLE);
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Error: "+  e.getLocalizedMessage(), Toast.LENGTH_LONG);
                                                    View toastView = toast.getView(); // This'll return the default View of the Toast.

                                                    /* And now you can get the TextView of the default View of the Toast. */
                                                    TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                                                    // toastMessage.setTextSize(25);
                                                    //toastMessage.setTextColor(Color.RED);
                                                    toastMessage.setPadding(16,0,16,0);
                                                    toastMessage.setGravity(Gravity.CENTER);
                                                    toastView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                                                    toast.show();
                                                }
                                            }).execute(mUrl);


                                        }
                                    };
                                    timer.schedule(tt, 1000);
                                }
                            }
                        });




                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                redit.setVisibility(ProgressBar.INVISIBLE);
                            }
                        });

                    }
                });
                //ok





                //alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();




            }
        });


















        //setting icons
        ////tabLayout.getTabAt(0).setIcon(R.drawable.ic_launcher_background);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_launcher_background);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_launcher_background);
    }

    private void getViews() {
        toolbar = findViewById(R.id.appbarlayout_tool_bar);
        tabLayout = findViewById(R.id.mTabLayout);
        viewPager = findViewById(R.id.viewPager);
        faBad = findViewById(R.id.fab_add);
    }

    private void initializeToolBar() {
        setSupportActionBar(toolbar);
        setTitle("Awesome Hollywood Movies");

    }



    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            boundService = ((dservices.DownloadServiceBinder) service).getService();


        }

        public void onServiceDisconnected(ComponentName className) {
            boundService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(MainActivity.this,
               dservices.class), mConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    void doUnbindService() {
        if (isBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //DownLoadUtils.getImpl().onDestroy();
        doUnbindService();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }





}