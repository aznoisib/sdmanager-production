package com.cybercode.sdm;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.mobile.util.WeakHandler;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class dadapter extends RecyclerView.Adapter<dholder> {
    public Listener mCallback;
    private int listItemLayout;
    private final Object flagLock = new Object();
    private boolean flag,isRunning,canAquireActualLock  = false;
    private  boolean canResume;
    private final ArrayList<models> itemList;
    private int ifl = 0;
    private long lastSec = 0;
    //private final Handler handler = new Handler();
    private WeakHandler handler = new WeakHandler();





    public void removeAt(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
        downloading.getInstance().postNotifyDataChanged();
    }
    public void setListener(Listener mCallback) {
        this.mCallback = mCallback;
    }
    public void removeListener(Listener mCallback) {
        this.mCallback = null;
    }

    public interface Listener {
        void onTask(int i);
        void onStart(models m);
        void onFinish(int i);
    }

    public int getIndex(int value) {

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getId()==value) {
                return i;
            }
        }
        return  -1;
    }




    public dcallback callback = new dcallback() {
    private dholder checkCurrentHolder(final models task) {
        final dholder tag = (dholder) task.getTag();
        if (tag.id != task.getId()) {
            return null;
        }

        return tag;
    }
    @Override
    public void onDownloadProgress(final models tsk, final int taskId, final long size) {
        final dholder tag = checkCurrentHolder(tsk);
        if (tag == null) {
            return;

        }
        final WeakReference<dholder> weakref = new WeakReference<>(tag);


        handler.post(new Runnable() {
            @Override
            public void run() {

                    Log.v("boskp", String.valueOf(size));
                    //lastSec = sec;

               // Log.v("boskp", String.valueOf(tsk.getLoader().getSpeedCalc(tsk)));
                weakref.get().taskSpeedTv.setText(String.valueOf(tsk.getSpeed()));
                weakref.get().updateDownloaded(size);

            }
        });

        Log.i("TAG", "DownloadTag tag"+ tag);
    }

    @Override
    public void onDownloadException(String taskId, Exception e) {

    }

        @Override
        public void onDownloadPrepare(models tsk) {
            final dholder tag = checkCurrentHolder(tsk);
            if (tag == null) {
                return;
                //Log.i(TAG, "DownloadTag null"+ tag);
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
//tag.relativeLayout.performClick();

                }
            });

        }

        @Override
    public void onDownloadFinish(final int taskId,models tsk) {
        final dholder tag = checkCurrentHolder(tsk);
        if (tag == null) {
            return;
            //Log.i(TAG, "DownloadTag null"+ tag);
        }

        tsk.setStatus("finish");

      //  mCallback.onFinish(taskId);
       handler.post(new Runnable() {
           @Override

            public void run() {
               //tag.taskNameTv.setText("fin");
               //if(tag.relativeLayout!=null)
                   tag.relativeLayout.performClick();
            //  mCallback.onFinish(taskId);
                //downloading.getInstance().perfomClick(taskId);
               // DownLoadUtils.getImpl().removeTaskForViewHolder(getIndex(taskId));
             //  removeAt(0);

               // itemList.remove(getIndex(taskId));
               // notifyItemRemoved(getIndex(taskId));
                //notifyItemRangeChanged(getIndex(taskId),getItemCount());
                //downloading.getInstance().postNotifyDataChanged();
            }
       });
       // mCallback.onFinish(taskId);
    }

    @Override
    public void onDownloadStart(dutils fdl,models tsk) {
        final dholder tag = checkCurrentHolder(tsk);
        if (tag == null) {
            return;
            //Log.i(TAG, "DownloadTag null"+ tag);
        }
        tsk.setLoader(fdl);
        Log.i("TAG", " onDownloadStart"+ "start");
        tsk.setStatus("started");

    }
};




    private View.OnClickListener taskActionOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
       //h
            if (v.getTag() == null) {
                Log.e("mName", "Null value returns by above getTag");
            }

            isRunning = !isRunning;


            final dholder holder = (dholder) v.getTag();

            final models model = itemList.get(holder.position);
            Log.v("Onclick", model.getStatus());




          if(model.getStatus().equals("finish")) {
                DownLoadUtils.getImpl().removeTaskForViewHolder(holder.position);
                itemList.remove(holder.position);
                notifyItemRemoved(holder.position);
                notifyItemRangeChanged(holder.position,getItemCount());
                //removeAt(holder.position);
                Log.v("Onplay", String.valueOf(holder.position));


            }else {
              if (model.getRunninng()) {


                  if (model.getStatus().equals("interrupt")) {

                      Log.v("Onplay", "interupt" + Thread.currentThread().isAlive());
                      if (!model.getThs().isAlive()) {
                          DownLoadUtils.getImpl().addTaskForViewHolder(model);
                          DownLoadUtils.getImpl().updateViewHolder(holder.id, holder);
                          mCallback.onStart(model);
                      } else {
                          model.setStatus("error");
                          itemList.get(holder.position).getLoader().suspend();
                          DownLoadUtils.getImpl().removeTaskForViewHolder(holder.position);
                          itemList.remove(holder.position);
                          notifyItemRemoved(holder.position);
                          notifyItemRangeChanged(holder.position, getItemCount());
                          isRunning = false;
//model.getThs().interrupt();

                          Log.v("Onplay", "kena");

                      }
                  } else if (model.getStatus().equals("pauseds")) {


                      model.setTask(model);
                      model.setCbk(callback);
                      DownLoadUtils.getImpl().addTaskForViewHolder(model);
                      DownLoadUtils.getImpl().updateViewHolder(holder.id, holder);
                      mCallback.onStart(model);

                  } else if (model.getStatus().equals("paused")) {
                      model.setStatus("error");
                      DownLoadUtils.getImpl().removeTaskForViewHolder(holder.position);
                      itemList.remove(holder.position);
                      notifyItemRemoved(holder.position);
                      notifyItemRangeChanged(holder.position, getItemCount());
                      isRunning = false;
                  }
              } else {
                  itemList.get(holder.position).getLoader().suspend();
                  //itemList.get(holder.position).getLoader().pause();
                  model.setStatus("paused");

              }
          }
            //  itemList
//ppp
/*if(model.getStatus().equals("paused")){
   // Log.v("Onplay", "paused"+ model.getLoader().getTerminate());

    //Log.v("onsynchronized", String.valueOf(Thread.holdsLock(model.getTask())));
if(model.getLoader()!=null){

  //  Log.v("Onplay", "paused"+ model.getLoader().getPaused());

    //model.getLoader().resumes(model.getTask());

}else{
    //model.setTask(model);
    //model.setCbk(callback);
   // DownLoadUtils.getImpl().addTaskForViewHolder(model);
    //DownLoadUtils.getImpl().updateViewHolder(holder.id, holder);
    //mCallback.onStart(model);
}
    if(!model.getStatus().equals("interrupt")) {
      //  Log.v("Onplay", "pausedsc"+ model.getLoader().getPaused());

        model.setTask(model);
        model.setCbk(callback);
        DownLoadUtils.getImpl().addTaskForViewHolder(model);
        DownLoadUtils.getImpl().updateViewHolder(holder.id, holder);
        mCallback.onStart(model);
    }

    //    model.setAct(isRunning);
    //  downloading.getInstance().postNotifyDataChanged();
    // Log.v("OnClickViewHolder", String.valueOf(itemList.get(holder.position).getAct()));
    //itemList.get(holder.position).getTask().pause();
    // holder.updatePlay(holder, itemList, model);

    //testetr
  //model.setTask(model);
   //model.setCbk(callback);
  //DownLoadUtils.getImpl().addTaskForViewHolder(model);
 //DownLoadUtils.getImpl().updateViewHolder(holder.id, holder);
 //mCallback.onStart(model);

   // synchronized (model.getTask()) {
       // model.getTask().notifyAll(); // Unblocks thread

    //}

    //new Thread(model).start();
}else if(model.getStatus().equals("started")){
    Log.v("Onplay", "started"+ holder.position);


    //removeAt(holder.position);
   itemList.get(holder.position).getLoader().suspend();
    //itemList.get(holder.position).getLoader().pause();
   model.setStatus("paused");

                //itemList.get(holder.position).getTask().pause();
}else if(model.getStatus().equals("interrupt")) {

    Log.v("Onplay", "interupt"+ Thread.currentThread().isAlive());
   if(!model.getThs().isAlive()){
       mCallback.onStart(model);
   }else{
       model.setStatus("error");
       itemList.get(holder.position).getLoader().suspend();
       DownLoadUtils.getImpl().removeTaskForViewHolder(holder.position);
       itemList.remove(holder.position);
       notifyItemRemoved(holder.position);
       notifyItemRangeChanged(holder.position,getItemCount());



     //model.getThs().interrupt();

       Log.v("Onplay", "kena");

   }


}else if(model.getStatus().equals("finish")) {
    DownLoadUtils.getImpl().removeTaskForViewHolder(holder.position);
    itemList.remove(holder.position);
  notifyItemRemoved(holder.position);
    notifyItemRangeChanged(holder.position,getItemCount());
   //removeAt(holder.position);
    Log.v("Onplay", String.valueOf(holder.position));

}*/

        }
    };

    public ArrayList getList() {
        return itemList;
    }

    // RecyclerView recyclerView;
    public dadapter(int layoutId, ArrayList<models> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    @NotNull
    @Override
    public dholder onCreateViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        //return new dholder(listItem);
        dholder holder = new dholder(
                LayoutInflater.from(
                        parent.getContext())
                        .inflate(R.layout.list_item, parent, false));

        holder.relativeLayout.setOnClickListener(taskActionOnClickListener);
        return holder;
    }


    @Override
    public void onBindViewHolder(final dholder holder, @SuppressLint("RecyclerView") final int position) {
        final models model = itemList.get(position);
        Log.v("onBindViewHolder", String.valueOf(model.getId()));
        //itemList.get(position).getId()
       // if(model.getLoader()!=null) {
            //Log.v("onBindViewHolder", String.valueOf(model.getLoader().getDownloadSize()));
            //holder.updateDownloaded(model.getLoader().getDownloadSize());
        //}
        holder.update(model.getId(),position);
        holder.relativeLayout.setTag(holder);
        DownLoadUtils.getImpl().addTaskForViewHolder(model);
        DownLoadUtils.getImpl().updateViewHolder(holder.id, holder);
        holder.taskNameTv.setText(String.valueOf(model.getId()));
        if(model.getStatus().equals("finish")){
            holder.taskNameTv.setText(String.valueOf(model.getId()));
        }
       /// holder.taskNameTv.setText("kkkk");






    }


    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }












}