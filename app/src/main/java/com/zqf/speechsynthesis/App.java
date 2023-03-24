package com.zqf.speechsynthesis;

import android.app.Application;

import com.hjq.toast.Toaster;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Toaster.init(this);
    }
}
