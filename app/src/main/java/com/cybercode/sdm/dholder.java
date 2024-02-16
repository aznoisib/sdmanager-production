package com.cybercode.sdm;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.util.ArrayList;

public class dholder extends RecyclerView.ViewHolder {
    public RelativeLayout relativeLayout;
    private static final long MIN_DELAY_MS = 500;
    private long interval = 0L;
    private long mLastClickTime;
    static public boolean toggle(boolean aBoolean) {
        return !aBoolean;
    }
    public dholder(View itemView) {
        super(itemView);
        assignViews();
    }

    private View findViewById(final int id) {
        return itemView.findViewById(id);
    }

    /**
     * viewHolder position
     */
    public int position;
    /**
     * download id
     */
    public int id;


    public void update(final int id, final int position) {
        this.id = id;
        this.position = position;
    }
    public void updatePlay(final dholder holder, final ArrayList<models> sd, final models md) {

    }


       // models.get(holder.position).setStatus(sds);
       // downloading.getInstance().postNotifyDataChanged();
    //    Log.v("toogleplay",  models.get(holder.position).getStatus());
   // }
    @SuppressLint("SetTextI18n")
    public void updateDownloading(final int status, final long sofar, final long total, long stamp) {

        final float percent = sofar
                / (float) total;
        taskPb.setMax(100);
        taskPb.setProgress((int) (percent * 100));
        //  taskNameTv.setText(String.valueOf(percent));
        switch (status) {
            case FileDownloadStatus.pending:
                taskNameTv.setText("pending");
                break;
            case FileDownloadStatus.started:
                taskNameTv.setText("STARTED");
                break;
            case FileDownloadStatus.connected:

                taskNameTv.setText("connecteds");
                break;
            case FileDownloadStatus.progress:
               long stamps = System.currentTimeMillis() - stamp;
                long allTimeForDownloading = stamps * total / sofar;
                long remainingTime = allTimeForDownloading - stamps;
                int remainS =(int) (remainingTime / 1000) % 60;
                int remainM = (int) ((remainingTime / (1000 * 60)) % 60);
                int timerS = (int) (stamps / 1000) % 60;
                int timerM = (int) ((stamps / (1000 * 60)) % 60);
                Log.v("rem",   ((timerM < 10) ? "0" + timerM : timerM) +":"+((timerS < 10) ? "0" + timerS : timerS)
                        +"/"+
                        ((remainM < 10) ? "0" + remainM : remainM) +":"+((remainS < 10) ? "0" + remainS : remainS));
                long sec =  System.currentTimeMillis()/1000;
                if (sec != interval) {
               taskElapsedTv.setText(((timerM < 10) ? "0" + timerM : timerM) +":"+((timerS < 10) ? "0" + timerS : timerS)
                       +"/"+
                       ((remainM < 10) ? "0" + remainM : remainM) +":"+((remainS < 10) ? "0" + remainS : remainS));
                    interval = sec;
                }
                // taskNameTv.setText(String.valueOf(downloading.getInstance().getItemList()));
               // taskNameTv.setText(String.valueOf(percent));
                break;
            default:
                taskNameTv.setText("defaut");
                break;
        }

        //   taskActionBtn.setText(R.string.pause);
    }
    public void updateProgress() {
        taskNameTv.setText("status");
    }
    public void updateNotDownloaded(final int status, final long sofar, final long total) {
        if (sofar > 0 && total > 0) {
            final float percent = sofar
                    / (float) total;
            taskPb.setMax(100);
            taskPb.setProgress((int) (percent * 100));
        } else {
            taskPb.setMax(1);
            taskPb.setProgress(0);
        }

        switch (status) {
            case FileDownloadStatus.error:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_error);
                break;
            case FileDownloadStatus.paused:
                //taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
                break;
            default:
              //  taskStatusTv.setText(R.string.tasks_manager_demo_status_not_downloaded);
                break;
        }
       // taskActionBtn.setText(R.string.start);
    }
    public void updateDownloaded(long sp) {
        taskNameTv.setText(String.valueOf(sp));
    }

    public TextView taskNameTv,taskSpeedTv,taskElapsedTv;
    public ImageView taskStateTv;
    public ProgressBar taskPb;
    private void assignViews() {
        taskElapsedTv = itemView.findViewById(R.id.tvElapsed);
        taskSpeedTv = itemView.findViewById(R.id.tvspeed);
        taskNameTv = itemView.findViewById(R.id.row_item);
        taskStateTv = itemView.findViewById(R.id.playImg);
        relativeLayout = itemView.findViewById(R.id.singleRow);
        taskPb = itemView.findViewById(R.id.progressBar);

    }

}