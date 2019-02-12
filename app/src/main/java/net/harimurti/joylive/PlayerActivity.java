package net.harimurti.joylive;

import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import net.harimurti.joylive.Classes.Converter;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.Api.JoyUser;
import net.harimurti.joylive.Classes.Preferences;
import net.harimurti.joylive.Classes.Share;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends AppCompatActivity {
    private Preferences pref;
    private SimpleExoPlayer player;
    private RelativeLayout layoutMenu;
    private ImageButton favorite;
    private JoyUser user;
    private boolean openFromExternal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        pref = new Preferences();
        layoutMenu = findViewById(R.id.layoutmenu);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            openFromExternal = true;
            Uri uri = intent.getData();
            String playlist = uri.toString();
            String linkStream = Converter.HttpToRtmp(playlist);

            user = new JoyUser("", "", "", "", linkStream);
        }
        else {
            Bundle bundle = getIntent().getExtras();
            String id = bundle.getString(JoyUser.ID);
            String profilePic = bundle.getString(JoyUser.PROFILEPIC);
            String nickname = bundle.getString(JoyUser.NICKNAME);
            String announcement = bundle.getString(JoyUser.ANNOUNCEMENT);
            String linkStream = bundle.getString(JoyUser.LINKSTREAM);

            user = new JoyUser(id, nickname, profilePic, announcement, linkStream);
            user.setPlayStartTimeNow();
        }

        layoutMenu.setVisibility(openFromExternal ? View.INVISIBLE : View.VISIBLE);

        if (!openFromExternal) {
            Notification.Toast("Opening Stream : " + user.getNickname());

            TextView tvNickname = findViewById(R.id.tv_nickname);
            tvNickname.setText(user.getNickname());

            CircleImageView image = findViewById(R.id.iv_picture);
            Picasso.get()
                    .load(user.getProfilePic())
                    .error(R.drawable.ic_no_image)
                    .into(image);

            favorite = findViewById(R.id.ib_favorite);
            favorite.setImageResource(pref.isFavorite(user) ? R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);
        }

        player = ExoPlayerFactory.newSimpleInstance(this);

        PlayerView playerView = findViewById(R.id.pv_player);
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "ExoPlayer2"));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(user.getLinkStream()));

        player.prepare(videoSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && (playbackState == 3)) {
                    if (pref.isFavorite(user)) {
                        user.setPlayStartTimeNow();
                        pref.addOrUpdateFavorite(user);
                    }
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                String errorMessage = "%s went wrong!";

                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        errorMessage = String.format(errorMessage, "Source");
                        Log.e("Player", "TYPE_SOURCE: " + error.getSourceException().getMessage());
                        break;

                    case ExoPlaybackException.TYPE_RENDERER:
                        errorMessage = String.format(errorMessage, "Renderer");
                        Log.e("Player", "TYPE_RENDERER: " + error.getRendererException().getMessage());
                        break;

                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        errorMessage = String.format(errorMessage, "Something");
                        Log.e("Player", "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                        break;
                }

                Notification.Toast(errorMessage);
            }
        });
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

    public void onFavoriteClick(View v) {
        if (!pref.isFavorite(user)) {
            if(pref.addFavorite(user))
                favorite.setImageResource(R.drawable.ic_action_favorite);
        }
        else {
            if(pref.remFavorite(user))
                favorite.setImageResource(R.drawable.ic_action_unfavorite);
        }
    }

    public void onShareClick(View v) {
        new Share(this).Link(user);
    }

    public void onLayoutMenuClick(View v) {
        if (openFromExternal) return;

        layoutMenu.setVisibility(
                layoutMenu.getVisibility() == View.VISIBLE ?  View.INVISIBLE : View.VISIBLE);
    }
}
