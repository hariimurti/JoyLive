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
import net.harimurti.joylive.JsonData.JoyUser;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<JoyUser> {

    private static class ViewHolder {
        ImageView image;
        TextView nickname;
        TextView status;
        TextView viewer;
        ImageButton play;
        ImageButton menu;
    }

    public ListAdapter(Context context, ArrayList<JoyUser> dataSet) {
        super(context, R.layout.content, dataSet);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final JoyUser content = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.content, parent, false);
            viewHolder.image = convertView.findViewById(R.id.iv_picture);
            viewHolder.nickname = convertView.findViewById(R.id.tv_nickname);
            viewHolder.status = convertView.findViewById(R.id.tv_status);
            viewHolder.viewer = convertView.findViewById(R.id.tv_viewer);
            viewHolder.play = convertView.findViewById(R.id.ib_play);
            viewHolder.menu = convertView.findViewById(R.id.ib_menu);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.get().load(content.getProfilePic()).error(R.drawable.ic_no_image).into(viewHolder.image);
        viewHolder.nickname.setText(content.getNickname());
        viewHolder.status.setText(content.getAnnouncement());
        viewHolder.viewer.setText(content.getViewer());
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Link.OpenPlayer(content);
            }
        });
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.AddFavUser(content);
            }
        });

        return convertView;
    }
}
