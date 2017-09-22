package com.codepath.android.booksearch;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by mars_williams on 9/22/17.
 */

public class BookSearchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}