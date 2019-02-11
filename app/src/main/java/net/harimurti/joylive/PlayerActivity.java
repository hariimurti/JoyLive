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

import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.Api.JoyUser;
import net.harimurti.joylive.Classes.Preferences;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends AppCompatActivity {
    private Preferences pref;
    private SimpleExoPlayer player;
    private RelativeLayout layoutMenu;
    private ImageButton favorite;
    private JoyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        pref = new Preferences();

        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString(JoyUser.ID);
        String profilePic = bundle.getString(JoyUser.PROFILEPIC);
        String nickname = bundle.getString(JoyUser.NICKNAME);
        String announcement = bundle.getString(JoyUser.ANNOUNCEMENT);
        String linkStream = bundle.getString(JoyUser.LINKSTREAM);

        user = new JoyUser(id, nickname, profilePic, announcement, linkStream);

        layoutMenu = findViewById(R.id.layoutmenu);

        TextView tvNickname = findViewById(R.id.tv_nickname);
        tvNickname.setText(nickname);

        CircleImageView image = findViewById(R.id.iv_picture);
        Picasso.get()
                .load(user.getProfilePic())
                .error(R.drawable.ic_no_image)
                .into(image);

        favorite = findViewById(R.id.ib_favorite);
        favorite.setImageResource(pref.isFavorite(user) ? R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);

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
                String errorMessage = "%s went wrong! Reloading...";

                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        errorMessage = String.format(errorMessage, "Source");
                        Log.e("Player", "TYPE_SOURCE: " + error.getSourceException());
                        break;

                    case ExoPlaybackException.TYPE_RENDERER:
                        errorMessage = String.format(errorMessage, "Renderer");
                        Log.e("Player", "TYPE_RENDERER: " + error.getRendererException());
                        break;

                    default:
                        errorMessage = String.format(errorMessage, "Something");
                        Log.e("Player", "TYPE_UNEXPECTED: " + error.getUnexpectedException());
                        break;
                }

                Notification.Toast(errorMessage);
                player.setPlayWhenReady(true);
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
        String text = String.format(Locale.getDefault(),
                "%s — %s\n\n▶ LiveShow » %s",
                user.getNickname(), user.getAnnouncement(), user.getLinkPlaylist());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "JoyLive.tv Streaming");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        this.startActivity(Intent.createChooser(intent, "Share URL"));
    }

    public void onLayoutMenuClick(View v) {
        layoutMenu.setVisibility(
                layoutMenu.getVisibility() == View.VISIBLE ?  View.INVISIBLE : View.VISIBLE);
    }
}
