package com.clevertap.android.sdk;

import com.clevertap.android.sdk.logs.CustomLogger;
import com.elvishew.xlog.XLog;

@SuppressWarnings({"unused"})
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        CustomLogger.initXLog();
        ActivityLifecycleCallback.register(this);
        super.onCreate();
    }
}
