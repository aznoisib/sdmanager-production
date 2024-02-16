package com.cybercode.sdm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class downloaded extends Fragment {
    ListView listView;
    ItemArrayAdapter adapter;
    public int position = 0;
    public boolean initfab = true;
    FloatingActionButton fab;
String TAG = "SDM |";
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    String m_chosenDir = "";
    boolean m_newFolderEnabled = false;
    static class ItemArrayAdapter extends ArrayAdapter<dmodel> {
        private Handler mHandler = new Handler();
        private int listItemLayout;

        final ArrayList<ViewHolder> lstHolders= new ArrayList<>();





        public ItemArrayAdapter(Context context, int layoutId, ArrayList<dmodel> itemList) {
            super(context, layoutId, itemList);
            listItemLayout = layoutId;
            //startUpdateTimer();

        }



        @NotNull
        @Override
        public View getView (final int position, View convertView, final ViewGroup parent) {
            //Log.v("cccc",)
            // Get the data item for this position
            final dmodel item = getItem(position);


            // Check if an existing view is being reused, otherwise inflate the view
            final ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(listItemLayout, parent, false);
               // viewHolder.item = convertView.findViewById(R.id.tvName);
                //viewHolder.prog = convertView.findViewById(R.id.progressBar);
                //viewHolder.state = convertView.findViewById(R.id.imgstate);
                convertView.setTag(viewHolder); // view lookup cache stored in tag

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            //jjj


            //viewHolder.item.setText(String.valueOf(System.currentTimeMillis()));
            viewHolder.prog.setVisibility(View.GONE);
            viewHolder.state.setImageResource(R.drawable.ic_done);
            // Return the completed view to render on screen
            viewHolder.setData(getItem(position));

            return convertView;
        }

        // The ViewHolder, only one item for simplicity and demonstration purposes, you can put all the views inside a row of the list into this ViewHolder
        private static class ViewHolder {
            TextView item;
            ProgressBar prog;
            ImageView state;
            dmodel mmodel;




            public void setData(dmodel items) {
                mmodel = items;
                //Log.v("sgkk", String.valueOf(mmodel));
                //updateTimeRemaining(System.currentTimeMillis());


            }

            public void updateTimeRemaining(long i) {

                //item.setText(String.valueOf(i));
            }
        }
    }



    String[] comedyMovies = {"TAG","The Grinch","Damsel","Early Man","'Blockers","Overboard"};
    public downloaded(){
        //constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.downloaded,container,false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        //FloatingActionButton floatingActionButton = view.findViewById(R.id.fabs);
      //if (floatingActionButton!=null) floatingActionButton.hide();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Downloaded", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        listView = view.findViewById(R.id.listView);
       ViewCompat.setNestedScrollingEnabled(listView, true);
        String userInfoListJsonString = sharedPreferences.getString("files", "");

        Type userListType = new TypeToken<ArrayList<dmodel>>(){}.getType();
        ArrayList<dmodel> userArray = gson.fromJson(userInfoListJsonString, userListType);

        //adapter =   new ItemArrayAdapter(getActivity(), R.layout.listing, userArray);
        //setting ArrayAdapter on listView
        if(userArray!=null) listView.setAdapter(adapter);

        //listView.


    }
        }