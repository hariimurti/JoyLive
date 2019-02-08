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

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<UserInfo> {
    private Context context;
    private ArrayList<UserInfo> dataSet;

    private static class ViewHolder {
        ImageView image;
        TextView nickname;
        TextView status;
        ImageButton play;
//        ImageButton share;
//        ImageButton favorite;
    }

    public ListAdapter(Context context, ArrayList<UserInfo> dataSet) {
        super(context, R.layout.content, dataSet);
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final UserInfo content = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.content, parent, false);
            viewHolder.image = convertView.findViewById(R.id.iv_picture);
            viewHolder.nickname = convertView.findViewById(R.id.tv_nickname);
            viewHolder.status = convertView.findViewById(R.id.tv_status);
            viewHolder.play = convertView.findViewById(R.id.ib_play);
//            viewHolder.share = convertView.findViewById(R.id.ib_share);
//            viewHolder.favorite = convertView.findViewById(R.id.ib_favorite);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.get().load(content.getImage()).error(R.drawable.ic_no_image).into(viewHolder.image);
        viewHolder.nickname.setText(content.getNickname());
        viewHolder.status.setText(content.getStatus());
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notification.Toast("Open Stream : " + content.getNickname());
                MainActivity.OpenPlayer(content);
            }
        });
//        viewHolder.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Notification.Toast("Share : " + content.getLinkShare());
//                MainActivity.ShareLink("Ayo tonton " + content.getNickname() + " disini:\n"+ content.getLinkShare());
//            }
//        });
//        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Notification.Toast("Favorit : " + content.getNickname());
//            }
//        });

        return convertView;
    }

//    private Drawable getDrawable(int id) {
//        return ContextCompat.getDrawable(getContext(), id);
//    }

//    public void setContentList(ArrayList<UserInfo> dataSet) {
//        this.dataSet.clear();
//        this.dataSet.addAll(dataSet);
//        notifyDataSetChanged();
//    }
}
