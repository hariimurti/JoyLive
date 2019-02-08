package net.harimurti.joylive.Classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.harimurti.joylive.JsonData.JoyUser;
import net.harimurti.joylive.MainActivity;
import net.harimurti.joylive.PlayerActivity;

public class Plugins {

    public static void OpenPlayer(JoyUser user) {
        Context context = MainActivity.getContext();
        Preferences pref = new Preferences(context);

        try {
            if (pref.getBoolean(Preferences.KEY_3RD_PLAYER)) {
                Uri uriStream = Uri.parse(user.getLinkStream());
                Intent intent = new Intent(Intent.ACTION_VIEW, uriStream);
                context.startActivity(intent);
                return;
            }
        }
        catch (Exception e) {
            Notification.Toast("Tidak bisa membuka Player luar, pakai bawaan saja!");
        }

        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("image", user.getProfilePic());
        intent.putExtra("nickname", user.getNickname());
        intent.putExtra("rtmp", user.getLinkStream());
        context.startActivity(intent);
    }

    public static void ShareLink(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "JoyJson.tv Streaming");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        MainActivity.getContext().startActivity(Intent.createChooser(intent, "Share URL"));
    }
}
