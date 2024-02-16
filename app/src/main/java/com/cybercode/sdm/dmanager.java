package com.cybercode.sdm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;

public class dmanager {
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static dmanager instance;

    private dmanager(Context context) {
        this.context = context;
    }

    public synchronized static dmanager getInstance(Context context) {
        if (instance == null) instance = new dmanager(context);
        return instance;
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
    public void onDestroy() {
        if(context != null) {
            context = null;
        }
    }
}