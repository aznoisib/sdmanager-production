package com.cybercode.sdm;

import android.util.Log;

public class dcalculator {
        private long startRecord = -1;//开始记录当前下载位置
        private long startTimeStamp = -1;
        private models mTask;
        public dcalculator(models task) {
            mTask = task;
        }

        /**
         * 开始计算下载速度
         */
        void startCalculateDown() {
            if (startRecord != -1 || startTimeStamp != -1 || mTask == null) {//前一次采集未结束
                return;
            }
            startRecord = mTask.getLoader().getDownloadSize();

            startTimeStamp = System.currentTimeMillis();

        }

        /**
         * 停止计算下载速度并记录
         */
        void endCalculateDown() {
            long currentTimeMillis = System.currentTimeMillis();
            long duration = (currentTimeMillis - startTimeStamp) / 1000;
            long stamps = currentTimeMillis - mTask.getStamp();
            if (startRecord == -1 || startTimeStamp == -1 || duration < 1 || mTask == null) { //采集事件至少大于1s
                return;
            }
            long allTimeForDownloading = stamps * mTask.getLoader().getFileSize() / mTask.getLoader().getDownloadSize();
            long remainingTime = allTimeForDownloading - stamps;
            int remainS =(int) (remainingTime / 1000) % 60;
            int remainM = (int) ((remainingTime / (1000 * 60)) % 60);
            int timerS = (int) (stamps / 1000) % 60;
            int timerM = (int) ((stamps / (1000 * 60)) % 60);
            Log.v("rem",   ((timerM < 10) ? "0" + timerM : timerM) +":"+((timerS < 10) ? "0" + timerS : timerS)
                    +"/"+
                    ((remainM < 10) ? "0" + remainM : remainM) +":"+((remainS < 10) ? "0" + remainS : remainS));
            long downBytes = mTask.getLoader().getDownloadSize() - startRecord;
            System.out.println("pkd"+ utils.getDownSpeed(downBytes/duration));

            mTask.setSpeed(utils.getDownSpeed(downBytes/duration));
            startRecord = -1;
            startTimeStamp = -1;
        }
}