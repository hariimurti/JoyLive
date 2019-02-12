package net.harimurti.joylive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.harimurti.joylive.Api.JoyUser;
import net.harimurti.joylive.Classes.Checker;
import net.harimurti.joylive.Classes.Preferences;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteAdapter extends ArrayAdapter<JoyUser> {
    private Context context;
    private ArrayList<JoyUser> dataSet;

    private class ViewHolder {
        CircleImageView image;
        TextView nickname;
        TextView status;
        TextView lastseen;
        ImageButton play;
        LinearLayout layout;

        public ViewHolder(View view) {
            image = view.findViewById(R.id.iv_picture);
            nickname = view.findViewById(R.id.tv_nickname);
            status = view.findViewById(R.id.tv_status);
            lastseen = view.findViewById(R.id.tv_lastseen);
            play = view.findViewById(R.id.ib_play);
            layout = view.findViewById(R.id.layout_user);
        }
    }

    public FavoriteAdapter(Context context, ArrayList<JoyUser> dataSet) {
        super(context, R.layout.main_content, dataSet);
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final JoyUser user = getItem(position);
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
                .load(user.getProfilePic())
                .error(R.drawable.ic_no_image)
                .into(viewHolder.image);

        Checker checker = new Checker();
        checker.link(user.getLinkPlaylist());
        checker.into(viewHolder.play);
        checker.setText("Checking...");
        checker.into(viewHolder.status);
        checker.execute();

        viewHolder.nickname.setText(user.getNickname());
        viewHolder.lastseen.setText(user.getPlayStartTime());
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra(JoyUser.ID, user.getId());
                intent.putExtra(JoyUser.NICKNAME, user.getNickname());
                intent.putExtra(JoyUser.PROFILEPIC, user.getProfilePic());
                intent.putExtra(JoyUser.ANNOUNCEMENT, user.getAnnouncement());
                intent.putExtra(JoyUser.LINKSTREAM, user.getLinkStream());
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

    private void showDetailedUser(ViewGroup parent, JoyUser user) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_favorite, parent, false);
        dialog.setView(dialogView);
        dialog.setCancelable(true);

        CircleImageView image = dialogView.findViewById(R.id.iv_picture);
        Picasso.get()
                .load(user.getProfilePic())
                .error(R.drawable.ic_no_image)
                .into(image);
        TextView id = dialogView.findViewById(R.id.tv_id);
        id.setText(user.getId());
        TextView nickname = dialogView.findViewById(R.id.tv_nickname);
        nickname.setText(user.getNickname());
        TextView status = dialogView.findViewById(R.id.tv_status);
        status.setText(user.getAnnouncement());
        TextView starttime = dialogView.findViewById(R.id.tv_lastseen);
        starttime.setText(user.getPlayStartTime());

        dialog.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Preferences pref = new Preferences();
                pref.remFavorite(user);

                dataSet.clear();
                dataSet.addAll(pref.getFavorite());
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
