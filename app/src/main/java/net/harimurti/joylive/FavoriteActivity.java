package net.harimurti.joylive;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import net.harimurti.joylive.Api.JoyUser;
import net.harimurti.joylive.Classes.Preferences;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    private ArrayList<JoyUser> listUser = new ArrayList<>();
    private FavoriteAdapter favoriteAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setTitle(R.string.app_favorite);

        pref = new Preferences();

        favoriteAdapter = new FavoriteAdapter(this, listUser);
        final ListView listView = findViewById(R.id.list_content);
        listView.setAdapter(favoriteAdapter);
        listView.setEmptyView(findViewById(R.id.empty));

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshList(true);
            }
        });

        RefreshList(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshList(false);
    }

    private void RefreshList(boolean force) {
        ArrayList<JoyUser> listFav = pref.getFavorite();

        if (!force) {
            if (listFav == null) return;
            if (listUser.size() == listFav.size())
                return;
        }

        listUser.clear();
        listUser.addAll(pref.getFavorite());
        favoriteAdapter.notifyDataSetChanged();

        swipeRefresh.setRefreshing(false);
    }
}
