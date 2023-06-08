package com.hk.ogrencievi.Public;

import com.onesignal.OneSignal;

public class Application extends android.app.Application {
    private static final String ONESIGNAL_APP_ID = "819cbe6b-16da-432c-b257-abc6d26f2391";

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.promptForPushNotifications();
    }
}
