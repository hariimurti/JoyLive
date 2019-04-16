package net.harimurti.joylive;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.harimurti.joylive.Classes.Favorite;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.JsonData.UserFav;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    private ArrayList<UserFav> userFavs = new ArrayList<>();
    private FavoriteAdapter favoriteAdapter;
    private Favorite favorite;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout layoutNoUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setTitle(R.string.app_favorite);

        favorite = new Favorite();
        favoriteAdapter = new FavoriteAdapter(this, userFavs);
        final ListView listView = findViewById(R.id.list_content);
        listView.setAdapter(favoriteAdapter);

        layoutNoUser = findViewById(R.id.layout_no_user);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UpdateFavList();
            }
        });

        UpdateFavList();
    }

    private void UpdateFavList() {
        ArrayList<UserFav> favs = favorite.get();

        userFavs.clear();
        if (!favs.isEmpty()) {
            layoutNoUser.setVisibility(View.GONE);
            userFavs.addAll(favs);
        }
        else {
            layoutNoUser.setVisibility(View.VISIBLE);
            Notification.Toast(getString(R.string.no_favorite_users));
        }

        favoriteAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favoritemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                UpdateFavList();
                break;

            default:
                break;
        }

        return true;
    }
}
