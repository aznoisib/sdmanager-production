package com.cybercode.sdm;


public interface  dcallback {
    public void onDownloadProgress(models tsk,int taskId, long size);
    public void onDownloadException(String taskId, Exception e);
    public void onDownloadPrepare(models tsk);
    public void onDownloadFinish(int taskId,models tsk);
    public void onDownloadStart(dutils fdl,models tsk);
}