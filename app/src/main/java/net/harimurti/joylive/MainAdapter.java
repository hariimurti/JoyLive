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
import net.harimurti.joylive.Classes.Share;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends ArrayAdapter<User> {
    private Context context;

    private class ViewHolder {
        CircleImageView image;
        TextView nickname;
        TextView status;
        TextView viewer;
        ImageButton play;
        LinearLayout layout;

        public ViewHolder(View view) {
            image = view.findViewById(R.id.iv_picture);
            nickname = view.findViewById(R.id.tv_nickname);
            status = view.findViewById(R.id.tv_status);
            viewer = view.findViewById(R.id.tv_viewer);
            play = view.findViewById(R.id.ib_play);
            layout = view.findViewById(R.id.layout_user);
        }
    }

    public MainAdapter(Context context, ArrayList<User> dataSet) {
        super(context, R.layout.main_content, dataSet);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.main_content, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.get()
                .load(user.getProfilePic())
                .error(R.drawable.ic_no_image)
                .into(viewHolder.image);

        if (user.getPrice() > 0)
            viewHolder.nickname.setText(String.format(Locale.getDefault(),
                    "%s - %d \uD83D\uDCB0", user.getNickname(), user.getPrice()));
        else
            viewHolder.nickname.setText(user.getNickname());

        viewHolder.status.setText(user.getAnnouncement());
        viewHolder.viewer.setText(user.getViewerHumanReadable());
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra(User.ID, user.getId());
                intent.putExtra(User.NICKNAME, user.getNickname());
                intent.putExtra(User.PROFILEPIC, user.getProfilePic());
                intent.putExtra(User.ANNOUNCEMENT, user.getAnnouncement());
                intent.putExtra(User.LINKSTREAM, user.getLinkStream());
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
                .inflate(R.layout.dialog_user, parent, false);
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

        TextView fans = dialogView.findViewById(R.id.tv_fans);
        fans.setText(user.getFansNum());

        TextView price = dialogView.findViewById(R.id.tv_price);
        price.setText(String.format(Locale.getDefault(), "%d \uD83D\uDCB0", user.getPrice()));

        TextView since = dialogView.findViewById(R.id.tv_starttime);
        since.setText(user.getPlayStartTime());

        TextView viewer = dialogView.findViewById(R.id.tv_viewer);
        viewer.setText(user.getViewer());

        dialog.setNeutralButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Share(context).Link(user);
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
