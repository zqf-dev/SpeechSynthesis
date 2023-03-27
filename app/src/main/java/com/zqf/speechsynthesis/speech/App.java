package com.zqf.speechsynthesis.speech;

import android.app.Application;

import com.hjq.toast.Toaster;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Toaster.init(this);
    }
}
