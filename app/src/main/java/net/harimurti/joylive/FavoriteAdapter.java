package net.harimurti.joylive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.harimurti.joylive.Classes.Favorite;
import net.harimurti.joylive.JsonData.Room;
import net.harimurti.joylive.JsonData.UserFav;
import net.harimurti.joylive.JsonData.User;
import net.harimurti.joylive.Rest.RestClient;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteAdapter extends ArrayAdapter<UserFav> {
    private Handler handler;
    private Context context;
    private Favorite favorite = new Favorite();
    private ArrayList<UserFav> dataSet;
    private User user;

    private class ViewHolder {
        CircleImageView picture;
        TextView nickname;
        TextView status;
        TextView lastSeen;
        ImageButton play;
        LinearLayout layout;

        public ViewHolder(View view) {
            picture = view.findViewById(R.id.iv_picture);
            nickname = view.findViewById(R.id.tv_nickname);
            status = view.findViewById(R.id.tv_status);
            lastSeen = view.findViewById(R.id.tv_lastseen);
            play = view.findViewById(R.id.ib_play);
            layout = view.findViewById(R.id.layout_user);
        }
    }

    public FavoriteAdapter(Context context, ArrayList<UserFav> dataSet) {
        super(context, R.layout.main_content, dataSet);
        this.handler = new Handler(Looper.getMainLooper());
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final UserFav item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.favorite_content, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.picture.setImageResource(R.drawable.user_default);
        viewHolder.nickname.setText(item.nickname);
        viewHolder.status.setText(R.string.status_checking);
        viewHolder.lastSeen.setText(item.getLastSeen());
        viewHolder.play.setEnabled(false);
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playThis();
            }
        });
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog(parent);
            }
        });

        RestClient client = new RestClient();
        client.setOnRoomListener(new RestClient.OnRoomListener() {
            @Override
            public void onError() {
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        viewHolder.status.setText(R.string.status_error);
                    }
                });
            }

            @Override
            public void onLoginRequired() {
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        viewHolder.status.setText(R.string.status_login);
                    }
                });
            }

            @Override
            public void onSuccess(Room result) {
                user.videoPlayUrl = result.videoPlayUrl;
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        viewHolder.status.setText(result.isPlaying ? R.string.status_live : R.string.status_offline);
                        viewHolder.play.setEnabled(result.isPlaying);
                        viewHolder.play.setImageResource(
                                result.isPlaying ? R.drawable.ic_action_play : R.drawable.ic_action_play_grey);
                    }
                });
            }
        });
        client.setOnUserInfoListener(new RestClient.OnUserInfoListener() {
            @Override
            public void onError() {
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        Picasso.get()
                                .load(item.picture)
                                .error(R.drawable.user_default)
                                .into(viewHolder.picture);
                    }
                });
            }

            @Override
            public void onSuccess(User result) {
                user = result;
                favorite.addOrUpdate(new UserFav().convertFrom(user));
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        Picasso.get()
                                .load(result.headPic)
                                .error(R.drawable.user_default)
                                .into(viewHolder.picture);
                    }
                });
                client.getRoomInfo(result.rid);
            }
        });
        client.getUserInfo(item.id);

        return convertView;
    }

    private void playThis() {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(User.ID, user.getId());
        intent.putExtra(User.NICKNAME, user.nickname);
        intent.putExtra(User.HEADPIC, user.headPic);
        intent.putExtra(User.ANNOUNCEMENT, user.getAnnouncement());
        intent.putExtra(User.VIDEOPLAYURL, user.videoPlayUrl);
        context.startActivity(intent);
    }

    private void alertDialog(ViewGroup parent) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_favorite, parent, false);
        dialog.setView(dialogView);
        dialog.setCancelable(true);

        CircleImageView image = dialogView.findViewById(R.id.iv_picture);
        Picasso.get()
                .load(user.headPic)
                .error(R.drawable.user_default)
                .into(image);

        TextView id = dialogView.findViewById(R.id.tv_id);
        id.setText(user.getId());

        TextView nickname = dialogView.findViewById(R.id.tv_nickname);
        nickname.setText(user.nickname);

        TextView status = dialogView.findViewById(R.id.tv_status);
        status.setText(user.getAnnouncement());

        TextView seen = dialogView.findViewById(R.id.tv_lastseen);
        seen.setText(user.getPlayStartTime());

        dialog.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favorite.remove(new UserFav().convertFrom(user));
                dataSet.remove(user);
                notifyDataSetChanged();
            }
        });

        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
