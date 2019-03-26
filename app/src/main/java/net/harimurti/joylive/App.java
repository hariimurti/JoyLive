package net.harimurti.joylive;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.Method;

public class App extends Application {
    private static Context context;
    public static final String AndroidVersion = Build.VERSION.RELEASE;
    public static final String DeviceName = String.format("%s %s", Build.BRAND, Build.MODEL).trim();
    public static final String GogoLiveVersion = "2.7.6";
    public static final String GogoLiveAgent = String.format("Gogo.Live %s", GogoLiveVersion);
    public static final String MobileAgent = "Mozilla/5.0 (Linux; Android 8.0.0; Pixel) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.80 Mobile Safari/537.36";

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getContext() {
        return App.context;
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }
}
