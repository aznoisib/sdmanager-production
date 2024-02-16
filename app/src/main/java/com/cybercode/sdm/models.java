package com.cybercode.sdm;


import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

public class models implements Runnable {
    private dcallback cbk;
    private String paths;
    private int Progress;
    private int threads;
    private  boolean running;
    private int Id;
    private String status;
    private String url;
    private String speed;
 private long bytes,stamp;
    private models task;
    private Object mTag;
    public dutils loader;
    public Thread ths;

    public models(models task,String speed, String url,int Progress,long stamp, String status, String paths,int threads, int Id,dcallback cbk) {

        this.Progress = Progress;
        this.stamp = stamp;
        this.status = status;
        this.paths = paths;
        this.speed = speed;
        this.Id = Id;
        this.url = url;
        this.task = task;
        this.threads = threads;
        this.cbk =  cbk;
    }


    public long getStamp()  {
        return stamp;
    }
    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    public void setThs(Thread ths) {
        this.ths = ths;
    }

    public Thread getThs() {
        return ths;
    }

    public models getTask() {
        return task;
    }

    public void setCbk(dcallback cbk) {
        this.cbk = cbk;
    }

    public void setTag(final Object tag) {
        this.mTag = tag;
    }

    public void setLoader(dutils loader) {
        this.loader = loader;
    }

    public dutils getLoader() {
        return loader;
    }
    public boolean getRunninng() {
        return  running = !running;
    }
    public Object getTag() {
        return this.mTag;
    }
    public void setSpeed(String speed) {
        this.speed = speed;
    }
    public String getSpeed() {
        return speed;
    }
    public String getUrl() {
       return url;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setTask(models task) {
        this.task = task;
    }
    public String getStatus() {
        return status;
    }
    public String getPaths() {
        return paths;
    }
    public void setPaths(String paths) {
        this.paths = paths;
    }
    public void setProgress(int Progress) {
        this.Progress = Progress;
    }
    public int getId() {
        return Id;
    }
    public int getProgress() {
        return Progress;
    }
    public void setId(int Id) {
        this.Id = Id;
    }
    public String toString()
    {
        return Id + " " + paths + " ";
    }

    @Override
    public void run() {
        try{

            Log.v("runss","run");
            this.loader = new dutils(getTask(),Id,url,threads,cbk);

        }catch(Exception e){
            Log.v("runss", String.valueOf(e));
            //this.downloadCallbacks.onDownloadException(taskId, e);

        }
     // this.loader = new dutils(task,Id,url,threads,cbk);
    }
}
