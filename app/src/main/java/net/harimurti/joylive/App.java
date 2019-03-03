package net.harimurti.joylive;

import android.app.Application;
import android.content.Context;
import android.os.Build;

public class App extends Application {
    private static Context context;
    public static final String GogoLiveVersion = "2.7.6";
    public static final String GogoLiveAgent = "Gogo.Live " + GogoLiveVersion;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getContext() {
        return App.context;
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceName() {
        return String.format("%s %s", Build.BRAND, Build.MODEL).trim();
    }
}
