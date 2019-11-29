package com.appsecco.vyapi.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.appsecco.vyapi.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewWebsiteFragment extends Fragment {

    public static WebView wv_personal_website;
    public static WebSettings webSettings;

    public ViewWebsiteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_website_fragment,container,false);
        wv_personal_website = (WebView) view.findViewById(R.id.wv_personal_website);

        Bundle bundle = getArguments();
        String URL = bundle.getString("URL");

        // Check if URL begins with http
        if (!URL.matches("^(http|https)://.*$")){
            URL = "http://" + URL;
        }

        wv_personal_website.setWebViewClient(new WebViewClient());
        webSettings = wv_personal_website.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_personal_website.setWebChromeClient(new WebChromeClient());

//        wv_personal_website.getSettings().setJavaScriptEnabled(true);
//        wv_personal_website.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        wv_personal_website.canGoBack();
//        wv_personal_website.goBack();

        // If URL is valid, open it
        if (URLUtil.isValidUrl(URL)){
            wv_personal_website.loadUrl(URL);
        }
        // If URL is invalid
        else {
            // Show a custom display_data_from_db webpage
            String htmlData ="<html><body><div align=\"center\" ><h2> Error loading URL: </h2><p>The link you are trying to access seems to be invalid. Please correct the URL and try again. </p><span style=\"color:red\"><h3><br/>" + URL + "</h3></span></div></body>";

            wv_personal_website.loadUrl("about:blank");
            wv_personal_website.loadDataWithBaseURL(null,htmlData, "text/html", "UTF-8",null);
            wv_personal_website.invalidate();
        }

        wv_personal_website.setWebViewClient(new WebViewClient());

        return view;
    }

}
