package com.cybercode.sdm;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class dutils{
    private static final String TAG = "FileDownloader";
    private Context context;
    private DownLoadThread[] threads;
    private boolean exit = false;
    private models task;
    private long downloadSize,speedCalc = 0;
    private int fileSize = 0;
    private long lastSec = 0;
    private int runningThread;
    private boolean terminate = false;
    private int taskId;
    public dcalculator calculator;
    public Map<Integer, Integer> data = new ConcurrentHashMap<>();


    public volatile boolean notFinish = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();


    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

    // Get the name of the file
    public  String getFilename(String path){

        int start =  path.lastIndexOf("/")+1;
        String substring = path.substring(start);

        String fileName = Environment.getExternalStorageDirectory().getPath()+"/"+substring;

        File file = new File(fileName);
        // Log.v("ppopo", String.valueOf(file.length()));
        return fileName;
    }


    public void terminate(){
        this.terminate = true;
        //fileService.delete(this.downloadUrl);
    }
    public boolean getPaused()
    {
        return this.paused;
    }
    public boolean getTerminate()
    {
        return this.terminate;
    }

    public void suspend()
    {
        this.exit = true;
        Log.v("suspend", String.valueOf(this.exit));
    }
    public void stop() {
        notFinish = false;
        // you might also want to interrupt() the Thread that is
        // running this Runnable, too, or perhaps call:
        resume();
        // to unblock
    }

    public void pause() {
        // you may want to throw an IllegalStateException if !running
        paused = true;
    }

    public void resumes(models tsk) {
        synchronized (pauseLock) {
            int blockSize = 4229066 / 3;


            for (int i = 0; i < 3; i++) {
                int startIndex = i * blockSize; // start position of each thread download
                int endIndex = (i + 1) * blockSize - 1;
                // special case is the last thread
                if (i == 3 - 1) {

                    endIndex = 4229066 - 1;

                }
                this.threads[i] = new DownLoadThread(this, startIndex, endIndex, i, "http://ftp.gunadarma.ac.id/bselama/pdf/Kelas08_Bahasa%20Indonesia%20Bahasa%20Kebanggaanku_Sarwiji.zip", 3);
                this.threads[i].setPriority(Thread.NORM_PRIORITY);
                this.threads[i].start();

            }



            paused = false;
            pauseLock.notifyAll(); // Unblocks thread
            tsk.setStatus("started");
        }
    }




    public void resume()
    {
        //   this.notFinish = false;
        Log.v("suspend", String.valueOf(this.exit));
    }

    private models getTask(){if(task!=null) return  this.task;
        return null;
    }
    public boolean getExit()
    {
        return this.exit;
    }

    public int getFileSize()
    {
        return fileSize;
    }
    public int getSpeedCalc(models mtask) {
        long startTimes = System.currentTimeMillis();
        long start = System.nanoTime();
        long totalRead = 0;
        final double NANOS_PER_SECOND = 1000000000.0;
        final double BYTES_PER_MIB = 1024 * 1024;


        int speed = 0;
        long finishedSize = 0;
        long startSize = 0;

        long startTime = System.currentTimeMillis();
        if(mtask!=null){
            //finishedSize = mtask.getSpeed();
        }
        startSize = finishedSize;
        long sec = System.currentTimeMillis() / 1000;
        if (sec != lastSec) {
           // if(mtask!=null) mtask.setSpeed(this.downloadSize);
            long duration = (System.currentTimeMillis() - startTimes) / 1000;
            speed = (int) ((this.downloadSize - startSize));
            //  speed= (int) ((1024/(double) (startTime-end)));
            double speedInMBps = NANOS_PER_SECOND / BYTES_PER_MIB * this.downloadSize / (System.nanoTime() - start + 1);
            double speedInMbps = speed * 8;

            System.out.println("skd"+duration);
            lastSec = sec;
        }

        return speed;
       /* int speed = 0;
        long finishedSize = 0;
        long startSize = 0;
        if(mtask!=null) mtask.setSpeed(this.downloadSize);

        long startTime = System.currentTimeMillis();
        if(mtask!=null){
            finishedSize = mtask.getSpeed();
        }

        startSize = finishedSize;
        Thread.sleep(900);
        speed = (int) ((this.downloadSize - startSize) / (int) (System.currentTimeMillis() + 1 - startTime));
     return speed;*/
    }
    public long getDownloadSize(){return downloadSize;}


    public synchronized void append(long size)
    {
        downloadSize += size;
        // Log.v("bos", String.valueOf(downloadSize));

    }

    public synchronized void update(int threadId, int pos)
    {
        //  this.data.put(threadId, pos);
        //   this.fileService.update(this.downloadUrl, threadId, pos);
    }
    private  class DownLoadThread extends Thread{
        //Through the constructor to pass in the start and end of each thread download
        private boolean finish = false;
        private dutils downloader;
        private boolean isErsror=false;
        private int startIndex;
        private int endIndex;
        private int threadId;
        private String path;
        private int threadCount;
        private int PbMaxSize; // represents the maximum value of the current thread download
        //If interrupted, get the location of the last download
        private int pblastPostion;
        public boolean isError(){
            return isErsror;
        }

        public boolean isFinish() {
            return finish;
        }


        public DownLoadThread(dutils downloader,int startIndex,int endIndex,int threadId,String path,int threadCount){
            this.startIndex = startIndex;
            this.endIndex  = endIndex;
            this.downloader = downloader;
            this.threadId = threadId;
            this.path = path;
            this.threadCount = threadCount;
        }

        @Override
        public void run() {
            //four logic to get the server to download files

            try {
                //(0) calculate the maximum value of the current progress bar
                PbMaxSize = endIndex - startIndex;
                //(1) Create a url object The parameter is the URL
                URL url = new URL(path);
                //(2) Get the HttpURLConnection link object
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                //(3) Set parameters Send get request
                conn.setRequestMethod("GET"); //default request is get to capitalize
                //(4) Set the timeout period of the link network
                conn.setConnectTimeout(5000);


                //[4.0] If the middle breaks Continue to the last position Continue downloading Read the last downloaded location from the file

                File file =new File(getFilename(path)+threadId+".txt");
                if (file.exists() && file.length()>0) {
                    FileInputStream fis = new  FileInputStream(file);
                    BufferedReader bufr = new  BufferedReader(new InputStreamReader(fis));
                    String lastPositionn = bufr.readLine(); //The content read is the location of the last download.
                    int lastPosition = Integer.parseInt(lastPositionn);

                    //[4.0] gives us the defined progress bar position assignment
                    pblastPostion = lastPosition - startIndex;

                    //[4.0.1] want to change the startIndex position
                    startIndex = lastPosition;

                    System.out.println("thread id::"+threadId + "true download location"+":"+startIndex+"-----"+endIndex);

                    fis.close(); //Close the stream
                }

                //[4.1]Set a request header Range (acting to tell the server where each thread starts and ends)
                conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);

                //(5) Get the status code returned by the server
                int code = conn.getResponseCode(); //200 means to get all the server resources successfully. 206 Request some resources successfully
                if (code == 206) {
                    //[6]Create random read and write file objects
                    RandomAccessFile raf = new RandomAccessFile(getFilename(path), "rw");
                    //[6] Each thread should start writing from its own location
                    raf.seek(startIndex);

                    InputStream in = conn.getInputStream(); //Saved is feiq.exe

                    //[7] write the data to the file
                    int len = -1;
                    byte[] buffer = new byte[1024*1024];//1Mb

                    int total = 0; // represents the size of the current thread download
//|| !this.downloader.getTask().getStatus().equals("error")
                    while(!this.downloader.getExit()  && (len = in.read(buffer))!=-1){


                        raf.write(buffer, 0, len);
                        total +=len;
                        //[8] Implementing a breakpoint resume is to save the location of the current thread download. The next time you download again, it will continue to download according to the location of the last download.
                        int currentThreadPosition = startIndex + total; // For example, save it in a normal .txt text
//mDownloadTask.setTotalSize(total);
                        //System.out.println("thread bool:"+threadId + this.downloader.getExit());

                        //[9] is used to save the current thread download location
                        RandomAccessFile raff = new RandomAccessFile(getFilename(path)+threadId+".txt", "rwd");
                        raff.write(String.valueOf(currentThreadPosition).getBytes());
                        raff.close();
                        downloader.append(len);
                        //[10] set the maximum value of the current progress bar and the current progress
                        //pbLists.get(threadId).setMax(PbMaxSize);//Set the maximum value of the progress bar
                        // pbLists.get(threadId).setProgress(pblastPostion+total);//Set the current progress of the current progress bar
                    }
                    raf.close();//Close the stream Release the resource
                    this.finish = true;
                    System.out.println("thread id:"+threadId + "---download completed");
                    runningThread--;
                    System.out.println("thread id:"+runningThread + "---running thread"+finish);
                    //File  delteFile = new File(getFilename(path)+threadId+".txt");
                    //delteFile.delete();
                    //[10]Delete the .txt file. When each thread is downloaded, we don’t know.
                    synchronized (DownLoadThread.class) {
                        //runningThread--;
                        for (int i = 0; i < threadCount; i++) {
                           // File  delteFile = new File(getFilename(path)+threadId+".txt");
                            //delteFile.delete();
                        }
                        if (runningThread == 0) {

                        }


                    }
                }
//Exception e ||
            } catch (IOException e) {
                Log.v(TAG, String.valueOf(e));
                e.printStackTrace();
                Log.v(TAG,"Thread-" + this.threadId + " exception:" + e);
            }
        }
    }
    public dutils(final models mtask, final int taskId, final String path, final int threadCount, final dcallback callbacks)
    {

        callbacks.onDownloadPrepare(mtask);
        this.taskId = taskId;
        mtask.setStatus("start");
        Log.d(TAG, "file path : " + this+"o/o"+mtask.getUrl());
        //Log.d(TAG, "file name : " + mDownloadTask.getFileName());
        // Log.d(TAG, "download url : " + mDownloadTask.getUrl());
        // new Thread(){
        // public void run() {

        //[1 ☆☆☆☆] Get the size of the server file To calculate the start and end positions of each thread download
        long finishedSize = 0;
        long totalSize = 0;
        long startSize = 0;
        try {
            //Log.d(TAG, "file d : " + mtask.getMtask());
            calculator = new dcalculator(mtask);

            callbacks.onDownloadStart(this,mtask);
            //(1) Create a url object The parameter is the URL
            URL url = new URL(path);
            this.threads = new DownLoadThread[threadCount];
            //(2) Get the HttpURLConnection link object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //(3) Set parameters Send get request
            conn.setRequestMethod("GET"); //default request is get to capitalize
            //(4) Set the timeout period of the link network
            conn.setConnectTimeout(5000);
            //(5) Get the status code returned by the server
            int code = conn.getResponseCode(); //200 means to get all the server resources successfully 206 requests some resources
            if (code == 200) {

                if (calculator != null) {
                    mtask.setStamp(System.currentTimeMillis());
                    calculator.startCalculateDown();
                }
                //(6) Get the size of the server file
                int length = conn.getContentLength();
                this.fileSize = length;
                //[6.1] assign the number of threads to the running thread
                runningThread = threadCount;


                System.out.println("length:" + length);

                //[2☆☆☆☆] Create a file with the same size and size as the server.
                RandomAccessFile rafAccessFile = new RandomAccessFile(getFilename(path), "rw");
                rafAccessFile.setLength(length);


                int blockSize = length / threadCount;


                for (int i = 0; i < this.threads.length; i++) {
                    int startIndex = i * blockSize;
                    File file = new File(getFilename(path) + i + ".txt");
                    if (file.exists() && file.length() > 0) {
                        FileInputStream fis = new FileInputStream(file);
                        BufferedReader bufr = new BufferedReader(new InputStreamReader(fis));
                        String lastPositionn = bufr.readLine(); //The content read is the location of the last download.
                        int lastPosition = Integer.parseInt(lastPositionn);
                        int pblastPostion = lastPosition - startIndex;
                        data.put(i, pblastPostion);
                        //[4.0.1] want to change the startIndex position
                        //    startIndex = lastPosition;

                        Log.v("bosgr", String.valueOf(pblastPostion));
                        // this.downloadSize += lastPosition;
                        //data.put(i,lastPosition);
                    }

                    // Integer xVal = this.data.get(i);

                    if (this.data.size() == this.threads.length) {

                        for (i = 0; i < this.threads.length; i++) {
                            Integer xVal = this.data.get(i);
                            if (xVal == null) {
                                Log.e("Error!", "xValue is null!");
                                return;
                            }
                            this.downloadSize += xVal;
                            //this.speedCalc +=xVal;

                        }
                        // this.downloadSize += xVal;
                    }

                    Log.v("bosg", String.valueOf(this.speedCalc));
                }


                for (int i = 0; i < this.threads.length; i++) {
                    int startIndex = i * blockSize; // start position of each thread download
                    int endIndex = (i + 1) * blockSize - 1;
                    // special case is the last thread
                    if (i == threadCount - 1) {

                        endIndex = length - 1;

                    }
                    this.threads[i] = new DownLoadThread(this, startIndex, endIndex, i, path, threadCount);
                    this.threads[i].setPriority(Thread.NORM_PRIORITY);
                    //this.threads[i].start();
                    if(this.threads[i]!=null){
                        //Log.v(TAG, "ths"+ this.threads[i].getState());
                        if(this.threads[i].getState()==Thread.State.NEW) {
                            this.threads[i].start();
                        }
                    }

//Thread.NORM_PRIORITY



                }
                //[Three ☆☆☆☆ Calculate the start and end positions of each thread download]

                // boolean notFinish = true;
                while (notFinish) {
                    if (calculator != null) {
                        calculator.endCalculateDown();
                        calculator.startCalculateDown();
                    }

                    //Activity refedActivity = mtask.getAct().get();
                    //       System.out.println("gspedder" + refedActivity);


                    //mtask.setSpeed(this.downloadSize);

                    long startTime = System.currentTimeMillis();
                    if(mtask!=null){
                        //finishedSize = mtask.getSpeed();
                    }

                    startSize = finishedSize;
                    int speed;
                    try {
                        Thread.sleep(100);

                        //Thread.currentThread().interrupt();
                    } catch (InterruptedException e) {

                        // Thread.currentThread().interrupt(); // preserve interruption status
                        //  mtask.getTask().setStatus("interrupt");
                        System.out.println("Interrupted.");
                        break;

                    }
                    //Thread.sleep(100);
                    if (Thread.interrupted()) {
                        Log.v(TAG, "interuppttegggg");
                        break; }

                    if (getTerminate()) {
                        throw new Exception("Terminated by user.");
                    }
                    notFinish = false;

                    for (int i = 0; i < threadCount; i++){

                        if (this.threads[i] != null && !this.threads[i].isFinish()) {
                            // Log.v(TAG, "thsd"+ this.threads[i].getState());
                            if(mtask.getStatus().equals("error")){
                                this.threads[i].interrupt();
                                if(notFinish) mtask.getThs().interrupt();

                            }
//if(this.threads[i].getState()==Thread.State.TERMINATED){
                            // notFinish = false;
                            // suspend();
//}
                            notFinish = true;
                            //speed = (int) ((this.downloadSize - startSize) / (int) (System.currentTimeMillis() + 1 - startTime));
                            //System.out.println("spedder" + this.downloadSize +"/"+startSize);
                            //System.out.println("spedder" + speed);

                        }else{


                            //Thread.sleep(2000);
                            //Thread.currentThread().interrupt();
                        }
                        int startIndex = i * blockSize; // start position of each thread download
                        int endIndex = (i + 1) * blockSize - 1;
                        // special case is the last thread
                        if (i == threadCount - 1) {

                            endIndex = length - 1;

                        }

                        //System.out.println("thread id:::" + i + "The theoretical download location " + ": " + startIndex + "-----" + endIndex);

                        //four open the thread to the server to download the file
                        //DownLoadThread downLoadThread = new DownLoadThread(startIndex, endIndex, i, path, threadCount);
                        //downLoadThread.start();
                        //  task.setSize(this.downloadSize);
                        //if (mtask!=null) mtask.suspend();
                        // if (this.downloadSize > 1048576) suspend();

                        callbacks.onDownloadProgress(mtask, taskId, this.downloadSize);

                    }

                    // Log.v(TAG, downloadSize +"==" +this.fileSize);
                    if (downloadSize == this.fileSize) {
                        notFinish = false;

                        callbacks.onDownloadFinish(taskId, mtask);
                        Log.v(TAG, String.valueOf(notFinish));
                    }

                }
                Log.v("end", String.valueOf(Thread.currentThread().isInterrupted()));
                Thread.sleep(500);
                Thread.currentThread().interrupt();
                Log.v("end", String.valueOf(Thread.currentThread().isInterrupted()));

                if(!mtask.getStatus().equals("finish") &&
                        !mtask.getStatus().equals("error")){
                    Log.v("endState", mtask.getStatus());

                    mtask.getTask().setStatus("interrupt");
                }else if (mtask.getStatus().equals("error")){
                    // mtask.getThs().interrupt();
                    //notFinish=false;
                }else{
                    //finish
                    for (int i = 0; i < threadCount; i++) {
                        File  delteFile = new File(getFilename(path)+i+".txt");
                        delteFile.delete();
                        Log.v("end", mtask.getStatus()+"/"+getFilename(path)+i);
                    }




                }
                // mtask.getTask().setStatus("interrupt");
            }

        } catch (Exception e) {
            Log.v(TAG, String.valueOf(e));
            e.printStackTrace();
        }

    };
    //}.start();
    // }

    /**
     * 获取文件名
     */





}