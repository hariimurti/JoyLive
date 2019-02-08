package net.harimurti.joylive;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class Notification {

    public static void Toast(String text) {
        Toast(text, false);
    }

    public static void Toast(String text, boolean isLong) {
        Context context = MainActivity.getContext();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }
}
