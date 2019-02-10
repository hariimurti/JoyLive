package net.harimurti.joylive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.harimurti.joylive.Classes.Link;
import net.harimurti.joylive.Api.JoyUser;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<JoyUser> {

    private class ViewHolder {
        ImageView image;
        TextView nickname;
        TextView status;
        TextView viewer;
        ImageButton play;
        ImageButton menu;

        public ViewHolder(View view) {
            image = view.findViewById(R.id.iv_picture);
            nickname = view.findViewById(R.id.tv_nickname);
            status = view.findViewById(R.id.tv_status);
            viewer = view.findViewById(R.id.tv_viewer);
            play = view.findViewById(R.id.ib_play);
            menu = view.findViewById(R.id.ib_menu);
        }
    }

    public ListAdapter(Context context, ArrayList<JoyUser> dataSet) {
        super(context, R.layout.main_content, dataSet);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final JoyUser user = getItem(position);
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
        viewHolder.nickname.setText(user.getNickname());
        viewHolder.status.setText(user.getAnnouncement());
        viewHolder.viewer.setText(user.getViewer());
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Link.OpenPlayer(user);
            }
        });
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity.AddFavUser(user);
            }
        });

        return convertView;
    }
}
