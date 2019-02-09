package net.harimurti.joylive;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import net.harimurti.joylive.Classes.Link;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.JsonData.JoyUser;

import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {
    private SimpleExoPlayer player;
    private RelativeLayout layoutMenu;
    private ImageButton favorite;
    private boolean isFavorite;
    private JoyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString(JoyUser.ID);
        String profilePic = bundle.getString(JoyUser.PROFILEPIC);
        String nickname = bundle.getString(JoyUser.NICKNAME);
        String announcement = bundle.getString(JoyUser.ANNOUNCEMENT);
        String linkStream = bundle.getString(JoyUser.LINKSTREAM);

        user = new JoyUser(id, nickname, profilePic, announcement, linkStream);

        layoutMenu = findViewById(R.id.layoutmenu);
        favorite = findViewById(R.id.ib_favorite);
        TextView tvNickname = findViewById(R.id.tv_nickname);
        tvNickname.setText(nickname);

        Notification.Toast("Opening Stream : " + nickname);

        player = ExoPlayerFactory.newSimpleInstance(this);

        PlayerView playerView = findViewById(R.id.pv_player);
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "ExoPlayer2"));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(linkStream));

        Player.EventListener eventListener = new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                String errorMessage = "Something went wrong! Try again later.";

                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        errorMessage = error.getSourceException().getMessage();
                        Log.e("Player", "TYPE_SOURCE: " + errorMessage);
                        break;

                    case ExoPlaybackException.TYPE_RENDERER:
                        errorMessage = error.getRendererException().getMessage();
                        Log.e("Player", "TYPE_RENDERER: " + errorMessage);
                        break;

                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        errorMessage = error.getUnexpectedException().getMessage();
                        Log.e("Player", "TYPE_UNEXPECTED: " + errorMessage);
                        break;
                }

                Notification.Toast(errorMessage);
            }
        };

        player.addListener(eventListener);
        player.prepare(videoSource);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.release();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.setPlayWhenReady(false);
    }

    public void buttonFavoriteClick(View v) {
        if (isFavorite) {
            Notification.Toast(user.getNickname() + " — unFavorite!");
            favorite.setImageResource(R.drawable.ic_action_unfavorite);
        } else {
            Notification.Toast(user.getNickname() + " — Favorite!");
            favorite.setImageResource(R.drawable.ic_action_favorite);
        }

        isFavorite = !isFavorite;
    }

    public void buttonShareClick(View v) {
        String text = String.format(Locale.getDefault(),
                "%s — %s\n▶LiveShow : %s",
                user.getNickname(), user.getAnnouncement(), user.getLinkPlaylist());
        Link.Share(text);
    }

    public void onLayoutMenu(View v) {
        layoutMenu.setVisibility(
                layoutMenu.getVisibility() == View.VISIBLE ?  View.INVISIBLE : View.VISIBLE);
    }
}
