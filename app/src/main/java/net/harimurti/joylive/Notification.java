package net.harimurti.joylive;

import android.widget.Toast;

public class Notification {

    public static void Toast(String text) {
        Toast.makeText(
                MainActivity.getContext(),
                text,
                Toast.LENGTH_SHORT).show();
    }

    public static void Toast(String text, boolean isLong) {
        Toast.makeText(
                MainActivity.getContext(),
                text,
                isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }
}
