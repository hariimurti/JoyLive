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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class PlayerActivity extends AppCompatActivity {
    private SimpleExoPlayer player;
    private RelativeLayout layoutMenu;
    private ImageButton favorite;
    private boolean isFavorite;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Bundle bundle = getIntent().getExtras();
        String image = bundle.getString("image");
        String nickname = bundle.getString("nickname");
        String rtmp = bundle.getString("rtmp");

        userInfo = new UserInfo(image, nickname, "", rtmp);

        TextView tvNickname = findViewById(R.id.tv_nickname);
        tvNickname.setText(nickname);

        layoutMenu = findViewById(R.id.layoutmenu);
        favorite = findViewById(R.id.ib_favorite);

        Notification.Toast("Opening Stream : " + nickname);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        PlayerView playerView = findViewById(R.id.pv_player);
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "ExoPlayer2"));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(rtmp));

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
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
    }

    public void buttonFavoriteClick(View v) {
        if (isFavorite) {
            Notification.Toast("Ra Favorit!");
            favorite.setImageResource(R.drawable.ic_action_unfavorite);
        } else {
            Notification.Toast("Favorit!");
            favorite.setImageResource(R.drawable.ic_action_favorite);
        }

        isFavorite = !isFavorite;
    }

    public void buttonShareClick(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "JoyLive.tv Streaming");
        intent.putExtra(Intent.EXTRA_TEXT, userInfo.getNickname() + " sedang live disini:\n" + userInfo.getLinkShare());
        startActivity(Intent.createChooser(intent, "Share URL"));
    }

    public void onLayoutMenu(View v) {
        layoutMenu.setVisibility(
                layoutMenu.getVisibility() == View.VISIBLE ?  View.INVISIBLE : View.VISIBLE);
    }
}
