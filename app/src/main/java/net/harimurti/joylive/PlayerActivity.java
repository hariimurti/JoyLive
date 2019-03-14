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
    private MediaSource videoSource;
    private boolean openFromExternal;
    private boolean isUserLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        this.activity = this;

        pref = new Preferences();
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
            String id = bundle.getString(User.MID);
            String profilePic = bundle.getString(User.PROFILEPIC);
            String nickname = bundle.getString(User.NICKNAME);
            String announcement = bundle.getString(User.ANNOUNCEMENT);
            String linkStream = bundle.getString(User.LINKSTREAM);

            user = new User(id, nickname, profilePic, announcement, linkStream);
            isUserLoaded = true;
        }

        if (!openFromExternal) {
            txtNickname.setText(user.nickname);
            txtBigNickname.setText(user.nickname);

            Picasso.get()
                    .load(user.headPic)
                    .error(R.drawable.user_default)
                    .into(imgProfile);

            boolean isFavorite = pref.isFavorite(user);
            btnFavorite.setImageResource(isFavorite ?
                    R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);
        }
        else {
            layoutShowHide.setVisibility(View.INVISIBLE);
            Notification.Toast("Opening Stream from External Link");
        }

        GetUserInfo(user.getId());

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
                if (playWhenReady) {
                    if (playbackState == Player.STATE_READY) {
                        user.setPlayStartTimeNow();
                        if (pref.isFavorite(user)) {
                            pref.addOrUpdateFavorite(user);
                        }
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
                if (error.type != ExoPlaybackException.TYPE_SOURCE) {
                    SetMessage(false);
                    layoutOffline.setVisibility(View.INVISIBLE);
                }
                else {
                    SetMessage(true);
                    imgBackground.setVisibility(View.VISIBLE);
                    layoutOffline.setVisibility(View.VISIBLE);
                }

                spinKit.setVisibility(View.INVISIBLE);
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

    public void onShowHideClick(View v) {
        if (!isUserLoaded) return;

        layoutShowHide.setVisibility(
                layoutShowHide.getVisibility() == View.VISIBLE ?  View.INVISIBLE : View.VISIBLE);
    }

    public void RetryPlayback() {
        try {
            Thread.sleep(5000);
            Log.d("Player", "Retrying...");
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
        }
        catch (Exception e) {
            Log.e("Player", e.getMessage());
        }
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
                SetBackgroundWithProfilePic();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    Log.e("GetUserInfo","Unexpected code " + response);
                    SetBackgroundWithProfilePic();
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

                    if (openFromExternal)
                        user.announcement = data.signature;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtNickname.setText(data.nickname);
                            txtBigNickname.setText(data.nickname);

                            Picasso.get()
                                    .load(data.headPic)
                                    .error(R.drawable.user_default)
                                    .into(imgProfile);

                            Picasso.get()
                                    .load(data.bgImg)
                                    .error(R.drawable.user_default)
                                    .into(imgBackground);

                            boolean isFavorite = pref.isFavorite(user);
                            btnFavorite.setImageResource(isFavorite ?
                                    R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);

                            layoutShowHide.setVisibility(View.VISIBLE);
                            isUserLoaded = true;
                        }
                    });
                }
                catch (Exception ex) {
                    Log.e("GetUserInfo",ex.getMessage());
                    SetBackgroundWithProfilePic();
                }
            }
        });
    }

    private void SetBackgroundWithProfilePic() {
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

    private void SetMessage(boolean isShowOver) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtMessage.setText(isShowOver ? R.string.show_is_over : R.string.network_problem);
                txtSubMessage.setText(isShowOver ? R.string.waiting_broadcaster : R.string.try_to_connect);
            }
        });
    }
}
