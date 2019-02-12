package net.harimurti.joylive;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.harimurti.joylive.Api.JoyUser;
import net.harimurti.joylive.Classes.Preferences;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteAdapter extends ArrayAdapter<JoyUser> {
    private Context context;

    private class ViewHolder {
        CircleImageView image;
        TextView nickname;
        TextView status;
        ImageButton play;

        public ViewHolder(View view) {
            image = view.findViewById(R.id.iv_picture);
            nickname = view.findViewById(R.id.tv_nickname);
            status = view.findViewById(R.id.tv_status);
            play = view.findViewById(R.id.ib_play);
        }
    }

    public FavoriteAdapter(Context context, ArrayList<JoyUser> dataSet) {
        super(context, R.layout.main_content, dataSet);
        this.context = context;
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
        viewHolder.nickname.setText(user.getNickname());
        viewHolder.status.setText(user.getPlayStartTime());
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

        return convertView;
    }
}
