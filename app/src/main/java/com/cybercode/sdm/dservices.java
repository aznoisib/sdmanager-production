package com.cybercode.sdm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class dservices extends Service {
    public Listener mCallback;
    private final static int NOTIFICATION_ID = 95;
    public static final String CHANNEL_ID = "SDM";
    private final IBinder mBinder = new DownloadServiceBinder();
    final ArrayList<models> itemList = new ArrayList<>();
    private int inc = 0;
    public interface Listener {

        void onStartCommand(models m);
    }

    public void setListener(Listener mCallback) {
        this.mCallback = mCallback;
    }
    public void removeListener(Listener mCallback) {
        this.mCallback = null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("onTaskRemoved", "stop remove"+itemList);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("onStartCommand", "onStartCommand");
        // startForegroundService();
        createNotificationChannel();
        startForegroundService();
        /**intent = new Intent(this, downloading.class);
        // Use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // Building notification here
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("MyForegroundService");
        mBuilder.setContentText("The Service is currently running");
        mBuilder.setAutoCancel(false);
        mBuilder.setContentIntent(pIntent);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        // Creating Intent and Pending Intent for actions
        //Intent bdIntent = new Intent(this, ForegroundService.class);
        //bdIntent.setAction(ACTION_STOP);
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, bdIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Adding action to notification builder
        //mBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.about), pendingIntent);
        // Launch notification
        startForeground(NOTIFICATION_ID, mBuilder.build());**/

        return START_NOT_STICKY;
    }

    /*
     * Binding-related methods
     */

    public class DownloadServiceBinder extends Binder {
       dservices getService() {
            return dservices.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void downLoadFile(models model) {

        Map<Thread,StackTraceElement[]> map = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> threadEntry : map.entrySet()) {
            Log.v("Thread:",threadEntry.getKey().getName()+":"+threadEntry.getKey().getState());
            for (StackTraceElement element : threadEntry.getValue()) {
                Log.v("--> ", String.valueOf(element));
            }
        }



        Log.v("ondownLoadFile",String.valueOf(model.getTask()));
        if(!itemList.contains(model)){
            addTask(model);
        }

            Thread th1 = new Thread(model);
       model.setThs(th1);

       if(!model.getThs().isAlive()){
           Log.v("ondownLoadFile","inc "+String.valueOf(inc++));
           th1.start();
        }

        if(model.getStatus().equals("interrupt")){

        }else{

        }

        //new Thread(model).start();
        Log.v("ondownLoadFile", String.valueOf(th1.getState()));


//Thread.currentThread()
        if (th1.isInterrupted())
        {
            //Log.v("ondownLoadFile", String.valueOf(Thread.currentThread().getState()));
          // th1.start();
        }



    }

    public void downLoadStart(models model){
     mCallback.onStartCommand(model);

    }

    /* Used to build and start foreground service. */
    private void startForegroundService()
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
       notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
               // .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    public void remList() {

        for (int i = itemList.size() - 1; i >= 0 ; i--) {
            if (itemList.get(i).getStatus().equals("finish") ||
                    itemList.get(i).getStatus().equals("error")) {
                itemList.remove(i);
                Log.v("toh", String.valueOf(i));
            }

        }}
    public ArrayList<models> getList() {
        return itemList;
    }

    public  void rTask() {

        itemList.remove(0);
        downloading.getInstance().postNotifyDataChanged();

    }
    public  void addTask(models model) {

       itemList.add(model);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.enableVibration(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }



}