package net.harimurti.joylive;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import net.harimurti.joylive.Classes.Preferences;

import java.lang.reflect.Method;

public class App extends Application {
    private static Context context;
    public static final String AndroidVersion = Build.VERSION.RELEASE;
    public static final String DeviceName = String.format("%s %s", Build.BRAND, Build.MODEL).trim();
    public static final String GogoLiveVersion = "2.7.6";
    public static final String GogoLiveAgent = String.format("Gogo.Live %s", GogoLiveVersion);
    public static final String GogoUsername = "62895377348858";
    public static final String GogoPassword = "d1jJ8nMd50wzneu3MA2q6JpQuJj5UrbA2RSwCsbtRrEZY1ER2oe/ZckJMLtwLCKs7YyzY/IEOO4+Xa1NORn8HLZTtQfgHxK4I5ZNOGJsU6aWxuW7Zqr57/aNZ9epluxpkUu+o3rFtYJWkABUB8rGz70Xzs3J4LB3SnCT2zqKccc=";

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
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = "";
        }

        // If none of the methods above worked
        if (serialNumber.equals("") || serialNumber.equals("unknown"))
            serialNumber = new Preferences().getDeviceSerial();

        return serialNumber;
    }
}
