package com.cybercode.sdm;

import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownLoadUtils {


    private final static class HolderClass {
        private static DownLoadUtils INSTANCE
                = new DownLoadUtils();
    }

    public static DownLoadUtils getImpl() {

        if (HolderClass.INSTANCE == null) HolderClass.INSTANCE = new DownLoadUtils();
        return HolderClass.INSTANCE;
    }

    public void onDestroy(){
        HolderClass.INSTANCE = null;
    }
    private SparseArray<models> taskSparseArray = new SparseArray<>();

    public void addTaskForViewHolder(final models task) {
        taskSparseArray.put(task.getId(), task);
    }

    public void removeTaskForViewHolder(final int id) {
        taskSparseArray.remove(id);
    }

    public void updateViewHolder(final int id, final dholder holder) {
        System.out.println("updateViewHolder: "+holder);
        final models task = taskSparseArray.get(id);
        if (task == null) {
            return;
        }

        task.setTag(holder);
    }

}