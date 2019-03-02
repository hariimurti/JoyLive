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

import net.harimurti.joylive.Api.JoyLive;
import net.harimurti.joylive.Classes.Converter;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.Api.JoyUser;
import net.harimurti.joylive.Classes.Preferences;
import net.harimurti.joylive.Classes.Share;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayerActivity extends AppCompatActivity {
    private Activity activity;
    private Preferences pref;
    private SimpleExoPlayer player;
    private RelativeLayout layoutMenu;
    private LinearLayout layoutOffline;
    private TextView bicNickname;
    private ImageView bicPicture;
    private ImageButton favorite;
    private JoyUser user;
    private boolean openFromExternal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        this.activity = this;

        pref = new Preferences();
        layoutMenu = findViewById(R.id.layout_menu);
        layoutOffline = findViewById(R.id.layout_offline);
        bicNickname = findViewById(R.id.tv_big_nickname);
        bicPicture = findViewById(R.id.iv_big_picture);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            openFromExternal = true;
            Uri uri = intent.getData();
            String playlist = uri.toString();

            user = Converter.LinkToUser(playlist);
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
            bicNickname.setVisibility(View.GONE);

            CircleImageView image = findViewById(R.id.iv_picture);
            Picasso.get()
                    .load(user.getProfilePic())
                    .error(R.drawable.ic_no_image)
                    .into(image);

            Picasso.get()
                    .load(user.getProfilePic())
                    .error(R.drawable.ic_no_image)
                    .into(bicPicture);

            favorite = findViewById(R.id.ib_favorite);
            favorite.setImageResource(pref.isFavorite(user) ? R.drawable.ic_action_favorite : R.drawable.ic_action_unfavorite);
        }
        else {
            Notification.Toast("Opening Stream from External Link");
            GetUserInfo(user.getLinkStream());
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
                if (playWhenReady) {
                    if (playbackState == Player.STATE_READY) {
                        if (pref.isFavorite(user)) {
                            user.setPlayStartTimeNow();
                            pref.addOrUpdateFavorite(user);
                        }
                    }
                }

                if (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING) {
                    bicPicture.setVisibility(View.INVISIBLE);
                    layoutOffline.setVisibility(View.INVISIBLE);
                }

                if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED) {
                    bicPicture.setVisibility(View.VISIBLE);
                    layoutOffline.setVisibility(View.VISIBLE);
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
                    bicPicture.setVisibility(View.VISIBLE);
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

    public void onRetryClick(View v) {
        Log.d("Player", "Retry...");

        player.retry();
        layoutOffline.setVisibility(View.INVISIBLE);
    }

    private void GetUserInfo (String link) {
        String website = Converter.LinkToWebpage(link);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(website)
                .addHeader("User-Agent", JoyLive.UserAgent)
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
                    String pattern = "<img src=\"(.*)\" class=\"avatar fl\">[\\s\\S]+class=\"nickname\">(.*)</p>";

                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(body);

                    if (m.find()) {
                        user.setProfilepic(m.group(1));
                        user.setNickname(m.group(2));

                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                bicNickname.setText(user.getNickname());
                                Picasso.get()
                                        .load(user.getProfilePic())
                                        .error(R.drawable.ic_no_image)
                                        .into(bicPicture);
                            }
                        });
                    }
                }
                catch (Exception ex) {
                    Log.e("GetUserInfo",ex.getMessage());
                }
            }
        });
    }
}
