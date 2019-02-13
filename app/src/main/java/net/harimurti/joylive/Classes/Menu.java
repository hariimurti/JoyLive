package net.harimurti.joylive.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import net.harimurti.joylive.R;

public class Menu {
    public static void Exit(Activity activity) {
        activity.finish();
        System.exit(0);
    }

    public static void About(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("Haii, Haloooooo\n\n" +
                "Aplikasi ini aku tujukan kepada om-om yg suka nonton joylive!\n" +
                "Selamat menikmati \uD83D\uDE18\n\n" +
                "Telegram : @paijemdev")
                .setTitle(R.string.app_name)
                .create();
        dialog.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://t.me/paijemdev"));
                context.startActivity(intent);
            }
        });
        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
