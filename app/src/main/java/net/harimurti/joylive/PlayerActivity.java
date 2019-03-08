package net.harimurti.joylive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.harimurti.joylive.JsonClass.JsonUser;
import net.harimurti.joylive.Classes.Converter;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.JsonClass.User;
import net.harimurti.joylive.Classes.Preferences;
import net.harimurti.joylive.Classes.Share;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlayerActivity extends AppCompatActivity {
    private Activity activity;
    private Preferences pref;
    private SimpleExoPlayer player;
    private RelativeLayout layoutMenu;
    private LinearLayout layoutOffline;
    private TextView txtNickname;
    private TextView txtBigNickname;
    private CircleImageView imgProfile;
    private ImageView imgBackground;
    private ImageButton btnFavorite;
    private SpinKitView spinKit;
    private User user;
    private boolean openFromExternal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        this.activity = this;

        pref = new Preferences();
        layoutMenu = findViewById(R.id.layout_menu);
        layoutOffline = findViewById(R.id.layout_offline);
        btnFavorite = findViewById(R.id.ib_favorite);
        txtNickname = findViewById(R.id.tv_nickname);
        imgProfile = findViewById(R.id.iv_picture);
        txtBigNickname = findViewById(R.id.tv_big_nickname);
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
            String id = bundle.getString(User.MID);
            String profilePic = bundle.getString(User.PROFILEPIC);
            String nickname = bundle.getString(User.NICKNAME);
            String announcement = bundle.getString(User.ANNOUNCEMENT);
            String linkStream = bundle.getString(User.LINKSTREAM);

            user = new User(id, nickname, profilePic, announcement, linkStream);
        }

        if (!openFromExternal) {
            txtNickname.setText(user.nickname);
            txtBigNickname.setText(user.nickname);

            Picasso.get()
                    .load(user.headPic)
                    .error(R.drawable.ic_no_image)
                    .into(imgProfile);

            Picasso.get()
                    .load(user.headPic)
                    .error(R.drawable.ic_no_image)
                    .into(imgBackground);

            boolean isFavorite = pref.isFavorite(user);
            btnFavorite.setImageResource(isFavorite ?
                    R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);
        }
        else {
            layoutMenu.setVisibility(View.GONE);
            Notification.Toast("Opening Stream from External Link");
        }

        GetUserInfo(user.getId());

        player = ExoPlayerFactory.newSimpleInstance(this);

        PlayerView playerView = findViewById(R.id.pv_player);
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "ExoPlayer2"));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(user.videoPlayUrl));

        player.prepare(videoSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    if (playbackState == Player.STATE_READY) {
                        if (pref.isFavorite(user)) {
                            user.setPlayStartTimeNow();
                            pref.addOrUpdateFavorite(user);
                        }
                    }
                }

                if (playbackState == Player.STATE_BUFFERING) {
                    spinKit.setVisibility(View.VISIBLE);
                }

                if (playbackState == Player.STATE_READY) {
                    user.setPlayStartTimeNow();
                    spinKit.setVisibility(View.GONE);
                }

                if (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING) {
                    imgBackground.setVisibility(View.INVISIBLE);
                    layoutOffline.setVisibility(View.INVISIBLE);
                }

                if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED) {
                    imgBackground.setVisibility(View.VISIBLE);
                    layoutOffline.setVisibility(View.VISIBLE);
                    spinKit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e("Player", "Player error or Stream cannot be accessed...");
                if (error.type != ExoPlaybackException.TYPE_SOURCE) {
                    Log.d("Player", "AutoRetry...");
                    player.retry();
                }
                else {
                    imgBackground.setVisibility(View.VISIBLE);
                    layoutOffline.setVisibility(View.VISIBLE);
                }
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
                btnFavorite.setImageResource(R.drawable.ic_action_favorite);
        }
        else {
            if(pref.remFavorite(user))
                btnFavorite.setImageResource(R.drawable.ic_action_unfavorite);
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

    public void onRetryClick(View v) {
        Log.d("Player", "Retry...");

        player.retry();
        layoutOffline.setVisibility(View.INVISIBLE);
    }

    private void GetUserInfo (String id) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("androidVersion", App.AndroidVersion)
                .addFormDataPart("packageId", "3")
                .addFormDataPart("channel", "developer-default")
                .addFormDataPart("version", App.GogoLiveVersion)
                .addFormDataPart("deviceName", App.DeviceName)
                .addFormDataPart("platform", "android")
                .build();
        Request request = new Request.Builder()
                .url("http://app.joylive.tv/user/GetUserInfo?uid=" + id)
                .post(body)
                .addHeader("Host", "app.joylive.tv")
                .addHeader("User-Agent", App.GogoLiveAgent)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Connection","keep-alive")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("GetUserInfo", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    Log.e("GetUserInfo","Unexpected code " + response);
                    return;
                }

                try {
                    String body = response.body().string();
                    Gson gson = new Gson();
                    JsonUser jsonObject = gson.fromJson(body, JsonUser.class);
                    User data = jsonObject.data;

                    user.mid = data.id;
                    user.nickname = data.nickname;
                    user.headPic = data.headPic;
                    user.announcement = data.signature;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtNickname.setText(data.nickname);
                            txtBigNickname.setText(data.nickname);

                            Picasso.get()
                                    .load(data.headPic)
                                    .error(R.drawable.ic_no_image)
                                    .into(imgProfile);

                            Picasso.get()
                                    .load(data.bgImg)
                                    .error(R.drawable.ic_no_image)
                                    .into(imgBackground);

                            boolean isFavorite = pref.isFavorite(user);
                            btnFavorite.setImageResource(isFavorite ?
                                    R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);
                        }
                    });

                }
                catch (Exception ex) {
                    Log.e("GetUserInfo",ex.getMessage());
                }
            }
        });
    }
}
