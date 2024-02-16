package com.cybercode.sdm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


public class browser extends Fragment {
    private LinearLayout lparent,sparent;
    private WebView webView;
    private Handler mHandler;
    private NestedScrollView nestEd;
    private View popupInputDialogView = null;
    // Contains user name data.
    private EditText userNameEditText = null;
    // Contains password data.
    private EditText passwordEditText = null;
    // Contains email data.
    private EditText emailEditText = null;
    // Click this button in popup dialog to save user input data in above three edittext.
    private Button upBtn;

    long cLength = 0L;

    public  void goWeb(String ur){
        MainActivity ac = (MainActivity) getActivity();
        if (ac!=null){
            String url = ac.sendUri();
            webView.loadUrl((url!=null) ? url : ur);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        lparent.removeView(webView);
        //webView.removeAllViews();
       //webView.destroy();
        webView = null;
        //sparent.removeAllViews();
        lparent.removeAllViews();
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.browser,container,false);
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
// custom dialog

        lparent = (LinearLayout) view.findViewById(R.id.controlLinearLayout);
        sparent = (LinearLayout) view.findViewById(R.id.fullscreen_content_controls);
        webView = (WebView) view.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl("http://z80.info/zip/");
        goWeb("https://www.google.com/");
        //webView.loadUrl("https://drive.google.com/uc?id=0B5lMcat54A3oal9mX0ozYjJEdUk&export=download");

         /*webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                callback.onTouch();
            }
        });*/



        webView.setDownloadListener(new DownloadListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadStart(final String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        final long contentLength) {

            }

        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lparent.removeView(webView);
        webView.removeAllViews();
        webView.destroy();
        webView = null;
        sparent.removeAllViews();
        lparent.removeAllViews();
        sparent = null;
    }

}