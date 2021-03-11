package com.clevertap.android.sdk;

import com.clevertap.android.sdk.logs.CustomLogger;

@SuppressWarnings({"unused"})
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        CustomLogger.initXLog();
        super.onCreate();
    }
}
