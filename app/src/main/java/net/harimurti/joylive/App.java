package net.harimurti.joylive;

import android.app.Application;
import android.content.Context;
import android.os.Build;

public class App extends Application {
    private static Context context;
    public static final String AndroidVersion = Build.VERSION.RELEASE;
    public static final String DeviceName = String.format("%s %s", Build.BRAND, Build.MODEL).trim();
    public static final String GogoLiveVersion = "2.7.6";
    public static final String GogoLiveAgent = "Gogo.Live " + GogoLiveVersion;
    public static final String MobileAgent = "Mozilla/5.0 (Linux; Android 8.0.0; Pixel) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.80 Mobile Safari/537.36";

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getContext() {
        return App.context;
    }
}
