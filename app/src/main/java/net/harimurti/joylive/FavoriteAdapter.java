package net.harimurti.joylive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.harimurti.joylive.JsonClass.User;
import net.harimurti.joylive.Classes.Checker;
import net.harimurti.joylive.Classes.Preferences;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteAdapter extends ArrayAdapter<User> {
    private Context context;
    private ArrayList<User> dataSet;

    private class ViewHolder {
        CircleImageView picture;
        TextView nickname;
        TextView status;
        TextView lastseen;
        ImageButton play;
        LinearLayout layout;

        public ViewHolder(View view) {
            picture = view.findViewById(R.id.iv_picture);
            nickname = view.findViewById(R.id.tv_nickname);
            status = view.findViewById(R.id.tv_status);
            lastseen = view.findViewById(R.id.tv_lastseen);
            play = view.findViewById(R.id.ib_play);
            layout = view.findViewById(R.id.layout_user);
        }
    }

    public FavoriteAdapter(Context context, ArrayList<User> dataSet) {
        super(context, R.layout.main_content, dataSet);
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.favorite_content, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.get()
                .load(user.nickname)
                .error(R.drawable.user_default)
                .into(viewHolder.picture);

        Checker checker = new Checker()
                .link(user.getLinkPlaylist())
                .checkingText("Wait...")
                .onlineText("Live")
                .offlineText("Offline")
                .into(viewHolder.status)
                .into(viewHolder.play);
        checker.execute();

        viewHolder.nickname.setText(user.nickname);
        viewHolder.lastseen.setText(user.getPlayStartTime());
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra(User.MID, user.getId());
                intent.putExtra(User.NICKNAME, user.nickname);
                intent.putExtra(User.PROFILEPIC, user.headPic);
                intent.putExtra(User.ANNOUNCEMENT, user.getAnnouncement());
                intent.putExtra(User.LINKSTREAM, user.videoPlayUrl);
                context.startActivity(intent);
            }
        });
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailedUser(parent, user);
            }
        });

        return convertView;
    }

    private void showDetailedUser(ViewGroup parent, User user) {
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
                Preferences pref = new Preferences();
                pref.remFavorite(user);
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
