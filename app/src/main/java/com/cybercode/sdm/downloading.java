package com.cybercode.sdm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class downloading extends Fragment implements dadapter.Listener,dservices.Listener{
    public dservices mDownloadService;
    public boolean mBoundToDownloadService = false;
    Context mcontext;
    RelativeLayout rparent;
    EditText ledit,aedit,pedit;
    ImageView browBtn;
    Button saveBtn,go;
   public  dadapter itemArrayAdapter;
    RecyclerView  recyclerView;
    ArrayList<models> prog = new ArrayList<models>();
    dmodel downloadModel;
    final ArrayList <models> itemList = new ArrayList<>();
    private final Handler handler = new Handler();



    public static downloading getInstance()
    {

        return new downloading();
    }


public void perfomClick(final int id){
    Log.i("onFinish", String.valueOf(id));
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
            //getIndexFinish()
            try {
                View v = recyclerView.getChildAt(id);
                if (v != null) {
                    RelativeLayout tx = (RelativeLayout) v.findViewById(R.id.singleRow);
                    tx.performClick();
                    Log.i("onFinish","clk");
                }
            } catch (Exception | Error error) {
                Log.i("onFinish","err");
                // Output expected IndexOutOfBoundsExceptions.
                Log.v("errorsss", String.valueOf(error));
                // perfomClick(getIndexId(ind));
                System.out.println(error);

            }// Output unexpected Exceptions/Errors.


        }
    }, 500);
}



    public int getIndexFinish() {

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getStatus().equals("finish")) {
                return i;
            }
        }
        return  -1;
    }

    public int getIndex(int value) {

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getId()==value) {
                return i;
            }
        }
        return  -1;
    }


    public long getTime() {
      return getItemList().get(0).getStamp();
    }

    public void postNotifyDataChanged() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (itemArrayAdapter != null) {
                        itemArrayAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final models[] myListData =  new models[] {
              //  new models("Email", DownLoadUtils.getInstance().getUniqueId())

        };

        itemArrayAdapter = new dadapter(R.layout.list_item, itemList);
        itemArrayAdapter.setListener(downloading.this);


        /*ft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] URLS = {
                        "http://www.papytane.com/mp4/accrobra.mp4",

                };
                final  models task = null;
                final String Tag = "FileDownloadUtilsTag";
                final String loadUrl = "http://ftp.gunadarma.ac.id/bselama/pdf/Kelas12_Program%20Bahasa_Aktif%20dan%20Kreatif%20Berbahasa%20Indonesia_Adi.zip"; // 下载文件的服务器地址
                final String loadUrl1 = "http://ftp.gunadarma.ac.id/bselama/pdf/Matematika%201%20SD.zip"; // 下载文件的服务器地址
                final String filePath = "/storage/emulated/0/Alarms/S" + System.currentTimeMillis()+".zip"; // 本地存储下载文件的绝对地址
                final int id = utils.UniqueID(loadUrl);
                final int id2 = utils.UniqueID(loadUrl1);
                Log.i(Tag, "DownloadTask Begin"+id);
                //  Log.i(Tag, "DownloadTasks"+ FileDownloader.getImpl().getTotal(id));
                //dutils.UniqueIDTest()


                itemList.add(new models(task,0,loadUrl,0,System.currentTimeMillis(),"paused",filePath,3,id,null));
              //  itemList.add(new models(task,0,loadUrl1,0,System.currentTimeMillis(),"paused",filePath,3,id2,null));
                itemArrayAdapter.notifyDataSetChanged();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            View v = recyclerView.getChildAt(getIndex(id));
                            if (v != null) {
                                RelativeLayout tx = (RelativeLayout) v.findViewById(R.id.singleRow);
                                tx.performClick();
                                // Log.i(Tag, "DownloadTask Started"+index);
                            }
                        } catch (Exception | Error error) {
                            // Output expected IndexOutOfBoundsExceptions.
                            Log.v("errorsss", String.valueOf(error));
                            // perfomClick(getIndexId(ind));
                            System.out.println(error);

                        }// Output unexpected Exceptions/Errors.


                    }
                }, 500);

        }
        });*/






        rparent = (RelativeLayout) view.findViewById(R.id.rparent);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclViews);
        recyclerView.setLayoutManager(new LinearLayoutManager(mcontext));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.downloading,container,false);

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// start services

        Intent intent = new Intent(getActivity(), dservices.class);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();


        // bind service

        Intent intent = new Intent(getActivity(), dservices.class);
        getActivity().bindService(intent, mDownloadServiceConnection, Context.BIND_AUTO_CREATE);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(itemArrayAdapter!=null) {
            itemArrayAdapter.removeListener(this);
            recyclerView.setAdapter(null);
        }
        recyclerView = null;
        rparent = null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBoundToDownloadService) {
            mDownloadService.removeListener(this);
        }
     itemArrayAdapter.removeListener(this);
    }
    @Override
    public void onPause() {

        // unbind service

        if (mBoundToDownloadService) {
            getActivity().unbindService(mDownloadServiceConnection);
        }

        super.onPause();
    }

    private ServiceConnection mDownloadServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            /*
             * Get service instance
             */

           dservices.DownloadServiceBinder binder = (dservices.DownloadServiceBinder) service;
            mDownloadService = binder.getService();
            mDownloadService.setListener(downloading.this);
            mBoundToDownloadService = true;
            if(mDownloadService.getList().isEmpty()) {
                Log.v("onServiceConnected", mDownloadService.getList().toString());
            }
            Log.v("onServiceConnecteds", mDownloadService.getList().toString());
                Log.v("onServiceConnected", String.valueOf(mDownloadService.getList().isEmpty()));
            mDownloadService.remList();
            itemList.clear();
            itemList.addAll(mDownloadService.getList());
            postNotifyDataChanged();
            /*
             * Start download for NOT_STARTED saved show
             */



        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundToDownloadService = false;
        }
    };





    public ArrayList<models> getItemList() {
        return itemList;
    }

    protected void postAndNotifyAdapter(final int id,Context ctx) {
        Toast.makeText(ctx,"click on item: "+id,Toast.LENGTH_LONG).show();
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                if (recyclerView!=null) {
                    Toast.makeText(getContext(),"click on item: "+id,Toast.LENGTH_LONG).show();
                    // This will call first item by calling "performClick()" of view.
                   Objects.requireNonNull(recyclerView.findViewHolderForLayoutPosition(id)).itemView.performClick();
                }  //
                //postAndNotifyAdapter(handler);

            }
        });
    }



    @Override
    public void onTask(final int i) {

        Log.i("onTask","task");
    }

    @Override
    public void onStart(final models m) {
        Log.i("onStart","task");
        if (mBoundToDownloadService) {
            mDownloadService.downLoadFile(m);
        }



    }

    @Override
    public void onFinish(final int i) {
        itemList.remove(0);
        itemArrayAdapter.notifyDataSetChanged();
        Log.i("onFinish", String.valueOf(itemList));
        perfomClick(i);

    }


    @Override
    public void onStartCommand(models m) {

        itemList.add(m);
        itemArrayAdapter.notifyDataSetChanged();
        perfomClick(getIndex(m.getId()));
        Log.i("onStartCommand", String.valueOf(m));
    }
}
