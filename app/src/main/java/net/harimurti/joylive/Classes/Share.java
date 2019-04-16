package net.harimurti.joylive.Classes;

import android.content.Context;
import android.content.Intent;

import net.harimurti.joylive.JsonData.User;

import java.util.Locale;

public class Share {
    private Context context;

    public Share(Context context) {
        this.context = context;
    }

    public void Link(User user) {
        String text = String.format(Locale.getDefault(),
                "%s — %s\n\n▶ LiveShow » %s",
                user.nickname, user.getAnnouncement(), user.getPlaylistUrl());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "JoyLive.tv Streaming");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "Share URL"));
    }
}
