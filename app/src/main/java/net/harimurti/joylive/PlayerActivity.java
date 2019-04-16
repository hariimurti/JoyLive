package net.harimurti.joylive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
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

import net.harimurti.joylive.Classes.AsyncSleep;
import net.harimurti.joylive.Classes.Converter;
import net.harimurti.joylive.Classes.Favorite;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.Classes.Share;
import net.harimurti.joylive.JsonData.User;
import net.harimurti.joylive.JsonData.UserFav;
import net.harimurti.joylive.Rest.RestClient;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends AppCompatActivity {
    private Favorite favorite;
    private SimpleExoPlayer player;
    private RelativeLayout layoutShowHide;
    private LinearLayout layoutOffline;
    private TextView txtNickname;
    private TextView txtBigNickname;
    private TextView txtMessage;
    private TextView txtSubMessage;
    private CircleImageView imgProfile;
    private ImageView imgBackground;
    private ImageButton btnFavorite;
    private SpinKitView spinKit;
    private User user;
    private UserFav userFav;
    private MediaSource videoSource;
    private boolean openFromExternal;
    private boolean isUserLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Activity activity = this;

        favorite = new Favorite();
        layoutShowHide = findViewById(R.id.layout_show_hide);
        layoutOffline = findViewById(R.id.layout_offline);
        btnFavorite = findViewById(R.id.ib_favorite);
        txtNickname = findViewById(R.id.tv_nickname);
        imgProfile = findViewById(R.id.iv_picture);
        txtBigNickname = findViewById(R.id.tv_big_nickname);
        txtMessage = findViewById(R.id.tv_message);
        txtSubMessage = findViewById(R.id.tv_sub_message);
        imgBackground = findViewById(R.id.iv_big_picture);
        spinKit = findViewById(R.id.spin_kit);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            openFromExternal = true;
            Uri uri = intent.getData();
            String playlist = uri.toString();

            user = Converter.LinkToUser(playlist);
        }
        else {
            Bundle bundle = intent.getExtras();
            String id = bundle.getString(User.ID);
            String profilePic = bundle.getString(User.HEADPIC);
            String nickname = bundle.getString(User.NICKNAME);
            String announcement = bundle.getString(User.ANNOUNCEMENT);
            String linkStream = bundle.getString(User.VIDEOPLAYURL);

            user = new User(id, nickname, profilePic, announcement, linkStream);
            isUserLoaded = true;
        }

        userFav = new UserFav().convertFrom(user);
        if (!openFromExternal) {
            txtNickname.setText(user.nickname);
            txtBigNickname.setText(user.nickname);

            Picasso.get()
                    .load(user.headPic)
                    .error(R.drawable.user_default)
                    .into(imgProfile);

            boolean isFavorite = favorite.exist(userFav);
            btnFavorite.setImageResource(isFavorite ?
                    R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);
        }
        else {
            layoutShowHide.setVisibility(View.INVISIBLE);
            Notification.Toast(getString(R.string.play_from_external));
        }

        RestClient client = new RestClient();
        client.setOnUserInfoListener(new RestClient.OnUserInfoListener() {
            @Override
            public void onError() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get()
                                .load(user.headPic)
                                .error(R.drawable.user_default)
                                .into(imgBackground);
                    }
                });
            }

            @Override
            public void onSuccess(User result) {
                user.setId(result.getId());
                user.nickname = result.nickname;
                user.headPic = result.headPic;
                user.signature = result.signature;
                userFav = new UserFav().convertFrom(user);

                txtNickname.setText(result.nickname);
                txtBigNickname.setText(result.nickname);
                btnFavorite.setImageResource(favorite.exist(userFav) ?
                        R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);
                layoutShowHide.setVisibility(View.VISIBLE);
                isUserLoaded = true;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get()
                                .load(result.headPic)
                                .error(R.drawable.user_default)
                                .into(imgProfile);

                        Picasso.get()
                                .load(result.bgImg)
                                .error(R.drawable.user_default)
                                .into(imgBackground);
                    }
                });
            }
        });
        client.getUserInfo(user.getId());

        player = ExoPlayerFactory.newSimpleInstance(this);

        PlayerView playerView = findViewById(R.id.pv_player);
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, App.GogoLiveAgent));
        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(user.videoPlayUrl));

        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.prepare(videoSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && (playbackState == Player.STATE_READY)) {
                    if (favorite.exist(userFav)) {
                        userFav.setLastSeenNow();
                        favorite.addOrUpdate(userFav);
                    }
                }

                if (playbackState == Player.STATE_BUFFERING) {
                    spinKit.setVisibility(View.VISIBLE);
                    layoutOffline.setVisibility(View.INVISIBLE);
                }

                if (playbackState == Player.STATE_READY) {
                    spinKit.setVisibility(View.INVISIBLE);
                    imgBackground.setVisibility(View.INVISIBLE);
                    layoutOffline.setVisibility(View.INVISIBLE);
                }

                if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED) {
                    spinKit.setVisibility(View.INVISIBLE);
                    imgBackground.setVisibility(View.VISIBLE);
                    layoutOffline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e("Player", "Player error or Source can't be accessed...");
                spinKit.setVisibility(View.INVISIBLE);

                if (error.type != ExoPlaybackException.TYPE_SOURCE) {
                    SetMessage(false);
                    layoutOffline.setVisibility(View.INVISIBLE);
                }
                else {
                    SetMessage(true);
                    imgBackground.setVisibility(View.VISIBLE);
                    layoutOffline.setVisibility(View.VISIBLE);
                }

                RetryPlayback();
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
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
    }

    public void onFavoriteClick(View v) {
        if (!favorite.exist(userFav)) {
            if(favorite.add(userFav))
                btnFavorite.setImageResource(R.drawable.ic_action_favorite);
        }
        else {
            if(favorite.remove(userFav))
                btnFavorite.setImageResource(R.drawable.ic_action_unfavorite);
        }
    }

    public void onShareClick(View v) {
        new Share(this).Link(user);
    }

    public void onShowHideClick(View v) {
        if (!isUserLoaded) return;

        layoutShowHide.setVisibility(
                layoutShowHide.getVisibility() == View.VISIBLE ?  View.INVISIBLE : View.VISIBLE);
    }

    public void RetryPlayback() {
        new AsyncSleep().setListener(new AsyncSleep.Listener() {
            @Override
            public void onFinish() {
                Log.d("Player", "Retrying...");
                player.prepare(videoSource);
                player.setPlayWhenReady(true);
            }
        }).start(5);
    }

    private void SetMessage(boolean isShowOver) {
        txtMessage.setText(isShowOver ? R.string.show_is_over : R.string.network_problem);
        txtSubMessage.setText(isShowOver ? R.string.waiting_broadcaster : R.string.try_to_connect);
    }
}
