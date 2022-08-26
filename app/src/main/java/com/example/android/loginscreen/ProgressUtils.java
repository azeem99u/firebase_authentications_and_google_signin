package com.example.android.loginscreen;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

public class ProgressUtils {

    private static ProgressBar mProgressBar;
    public static void initAddProgress(Activity activity){
       mProgressBar = activity.findViewById(R.id.progressbar);
    }




    public static void showProgressBar() {
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }


}
